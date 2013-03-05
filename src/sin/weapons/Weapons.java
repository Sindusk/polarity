package sin.weapons;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.character.Character;
import sin.hud.HUD;
import sin.network.Networking;
import sin.tools.T;
import sin.weapons.AmmoManager.RechargeAmmo;
import sin.weapons.AmmoManager.ReloadAmmo;
import sin.weapons.AttackManager.AttackTemplate;
import sin.weapons.AttackManager.MeleeAttack;
import sin.weapons.AttackManager.RangedProjectileAttack;
import sin.weapons.AttackManager.RangedRayAttack;
import sin.weapons.RecoilManager.RH;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Weapons{
    private static GameClient app;
    
    // Recoils:
    private static class Recoils{
        private float up_min, up_max, left_min, left_max;

        public Recoils(float up_min, float up_max, float left_min, float left_max){
            this.up_min = up_min;
            this.up_max = up_max;
            this.left_min = left_min;
            this.left_max = left_max;
        }

        public float up(){
            return up_min+(FastMath.nextRandomFloat()*(up_max-up_min));
        }
        public float left(){
            return left_min+(FastMath.nextRandomFloat()*(left_max-left_min));
        }
    }
    // Spreads:
    private static class Spread{
        private static final float SPREAD_INC = 0.04f;
        private float s_base;
        private float s_recoil;

        public Spread(float base, float recoil){
            this.s_base = base*SPREAD_INC;
            this.s_recoil = recoil*SPREAD_INC;
        }

        public void apply(Vector3f target){
            float spread_mult = s_base;
            spread_mult += s_recoil*(RecoilManager.getRecoil(RH.UP)+RecoilManager.getRecoil(RH.LEFT));
            if(!Character.getControl().onGround()) {
                spread_mult += s_base*SPREAD_INC*.05;
            }
            float spread_sub = spread_mult/2;
            Vector3f offset = T.v3f(
                    (spread_mult*FastMath.rand.nextFloat())-spread_sub,
                    (spread_mult*FastMath.rand.nextFloat())-spread_sub,
                    (spread_mult*FastMath.rand.nextFloat())-spread_sub);
            target.addLocal(offset);
        }
    }
    // Audio & Models:
    private static class WeaponAudio{
        private String weapon;
        private AudioNode fireNode;

        private static String getSound(String weapon, String sound){
            return "Sounds/Weapons/"+weapon+"/"+sound+".ogg";
        }

        public WeaponAudio(String weapon, float fireVolume){
            this.weapon = getSound(weapon, "fire");
            fireNode = new AudioNode(app.getAssetManager(), this.weapon, false);
            fireNode.setPositional(false);
            fireNode.setVolume(fireVolume);
        }

        public void fire(){
            fireNode.playInstance();
            Networking.sendSound(weapon);
        }
    }
    private static class WeaponMuzzle{
        private ParticleEmitter muzzle;

        private String getEffect(String effect){
            return "Textures/Effects/"+effect+".png";
        }

        public WeaponMuzzle(Node node){
            muzzle = new ParticleEmitter("Muzzle", ParticleMesh.Type.Triangle, 8);
            Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
            mat.setTexture("Texture", app.getAssetManager().loadTexture(getEffect("flame")));
            muzzle.setMaterial(mat);
            muzzle.setStartColor(ColorRGBA.Red);
            muzzle.setImagesX(2);
            muzzle.setImagesY(2);
            muzzle.setGravity(T.v3f(0, 2, 0));
            muzzle.setStartSize(0.1f);
            muzzle.setEndSize(0.3f);
            muzzle.setLowLife(0.2f);
            muzzle.setHighLife(0.4f);
            muzzle.getParticleInfluencer().setInitialVelocity(T.v3f(0, 1, 0));
            muzzle.getParticleInfluencer().setVelocityVariation(0.1f);
            muzzle.setLocalTranslation(T.v3f(0, 0, 5));
            muzzle.setParticlesPerSec(0);
            node.attachChild(muzzle);
        }
    }
    private enum Archetype{
        ANCIENT, MODERN, ENERGY, EXPLOSIVE, ELEMENTAL
    }
    private enum Classification{
        SLASHING, PISTOL, ASSAULT, SNIPER, LAUNCHER, LASER
    }

    // Weapon Template:
    public static abstract class Weapon{
        // Required fields (Important):
        private Archetype archetype;
        private Classification classification;
        protected boolean left;

        // Helper Classes:
        protected AttackTemplate damage;
        protected Recoils recoils;
        protected Spread spread;
        protected WeaponAudio audio;
        protected WeaponMuzzle muzzle;

        // Other Variables:
        protected Node model = new Node();
        protected String name;
        protected boolean automatic;
        protected boolean firing = false;
        protected float cooldown;
        protected float cooling = 0;

        protected abstract void CreateModel();
        public Weapon(Archetype archetype, Classification classification, boolean left){
            this.archetype = archetype;
            this.classification = classification;
            this.left = left;
            muzzle = new WeaponMuzzle(model);
        }

        public Archetype getArchetype(){
            return archetype;
        }
        public Classification getClassification(){
            return classification;
        }
        public Node getModel(){
            return model;
        }
        
        public void setFiring(boolean firing){
            this.firing = firing;
        }

        public void updateModel(){
            Vector3f loc = app.getCamera().getLeft().clone();
            loc.setX(loc.getX()*0.9f);
            loc.setY(loc.getY()-0.5f);
            loc.setZ(loc.getZ()*0.9f);
            if(!left){
                loc.setX(loc.getX()*-1);
                loc.setZ(loc.getZ()*-1);
            }
            model.setLocalTranslation(loc);
            Quaternion rot = app.getCamera().getRotation().clone();
            model.setLocalRotation(rot);
        }
        public void fire(){
            Vector3f target = app.getCamera().getDirection().clone();
            spread.apply(target);
            damage.attack(new Ray(app.getCamera().getLocation(), target));
            RecoilManager.recoil(recoils.up(), recoils.left());
            audio.fire();
            cooling += cooldown;
        }
        public void cool(float tpf){
            if(cooling != 0){
                cooling -= tpf;
                if(cooling < 0) {
                    cooling = 0;
                }
            }
            this.updateModel();
        }
        public void tick(float tpf){
            if(firing && cooling == 0){
                fire();
            }
            this.cool(tpf);
        }

        public void enable(Node node){
            node.attachChild(model);
        }
        public void disable(){
            Character.getNode().detachChild(model);
            firing = false;
        }
    }
    
    // Melee Classes:
    private static abstract class MeleeWeapon extends Weapon{
        protected abstract void CreateModel();
        public MeleeWeapon(Archetype archetype, Classification classification, boolean left){
            super(archetype, classification, left);
        }
    }
    
    // Ranged Classes:
    public static abstract class RangedWeapon extends Weapon{
        protected abstract void CreateModel();
        public RangedWeapon(Archetype archetype, Classification classification, boolean left){
            super(archetype, classification, left);
        }
    }
    public static abstract class RangedReloadWeapon extends RangedWeapon{
        // Helper Classes:
        protected ReloadAmmo ammo;
        
        protected abstract void CreateModel();
        public RangedReloadWeapon(Archetype archetype, Classification classification, boolean left){
            super(archetype, classification, left);
        }
        
        @Override
        public void updateModel(){
            super.updateModel();
            Quaternion rot = app.getCamera().getRotation().clone();
            if(ammo.isReloading()){
                rot.multLocal(new Quaternion().fromAngleAxis(FastMath.PI/16, Vector3f.UNIT_X));
            }
            model.setLocalRotation(rot);
        }
        @Override
        public void cool(float tpf){
            super.cool(tpf);
            this.updateModel();
        }
        @Override
        public void enable(Node node){
            super.enable(node);
            HUD.setBarMax(ammo.getBarIndex(), ammo.getMax());
            ammo.updateBar();
        }
        @Override
        public void fire(){
            super.fire();
            ammo.decClip();
            ammo.updateBar();
        }
        @Override
        public void tick(float tpf){
            if(cooling == 0){
                if(ammo.isReloading()){
                    ammo.setReloading(false);
                    ammo.updateBar();
                }else if(ammo.getClip() == 0 && !ammo.isReloading()){
                    this.reload();
                    return;
                }else if(firing){
                    this.fire();
                }
            }
            this.cool(tpf);
        }
        
        public void reload(){
            if(!ammo.isReloading() && ammo.getClip() != ammo.getMax()) {
                cooling += ammo.reload();
            }
        }
    }
    public static abstract class RangedRechargeWeapon extends RangedWeapon{
        // Necessary Classes:
        protected RechargeAmmo ammo;
        
        protected abstract void CreateModel();
        public RangedRechargeWeapon(Archetype archetype, Classification classification, boolean left){
            super(archetype, classification, left);
        }
        
        @Override
        public void cool(float tpf){
            super.cool(tpf);
            ammo.recharge(tpf);
        }
        @Override
        public void enable(Node node){
            super.enable(node);
            HUD.setBarMax(ammo.getBarIndex(), ammo.getMax());
            ammo.updateBar();
        }
        @Override
        public void fire(){
            super.fire();
            ammo.decClip();
            ammo.updateBar();
        }
        @Override
        public void tick(float tpf){
            if(cooling == 0 && firing && !(ammo.getClip() == 0)){
                this.fire();
            }
            this.cool(tpf);
        }
    }
    
    // Melee Weapons:
    public static class Sword extends MeleeWeapon{
        protected final void CreateModel() {
            model.setLocalScale(.5f, .5f, .5f);
            Geometry geo = CG.createCylinder(model, "", .3f, .6f, T.v3f(0, -.5f, 4), ColorRGBA.Green, T.v2f(1, 1));
            geo.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI/2, Vector3f.UNIT_X));
            CG.createBox(model, "", T.v3f(.2f, 1.4f, .2f), T.v3f(0, 0.7f, 4), ColorRGBA.Orange);
        }
        public Sword(boolean left){
            super(Archetype.ANCIENT, Classification.SLASHING, left);
            name = "Sword";
            audio = new WeaponAudio(name, 1);
            damage = new MeleeAttack("damage(6.5)", 14f);
            recoils = new Recoils(0, 0, 0, 0);
            spread = new Spread(0, 0);
            automatic = true;
            cooldown = 0.45f;
            CreateModel();
        }
    }
    
    // Ranged Weapons:
    public static class M4A1 extends RangedReloadWeapon{
        protected final void CreateModel(){
            model.setLocalScale(.6f, .6f, .6f);
            CG.createCylinder(model, "", .15f, 2f, T.v3f(0, 0, 3.2f), T.getMaterialPath("wall"), T.v2f(1, 1));
            CG.createBox(model, "", T.v3f(.25f, .25f, 2.5f), T.v3f(0, 0, 1f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
        }
        public M4A1(boolean left){
            super(Archetype.MODERN, Classification.ASSAULT, left);
            name = "M4A1";
            audio = new WeaponAudio(name, 1);
            ammo = new ReloadAmmo(30, 1.2f, left);
            damage = new RangedProjectileAttack("", "damage(4.5):destroy", 100f, 100f);
            recoils = new Recoils(35, 65, -25, 25);
            spread = new Spread(0, 15);
            automatic = true;
            cooldown = 0.09f;
            CreateModel();
        }
    }
    public static class AK47 extends RangedReloadWeapon{
        protected final void CreateModel(){
            model.setLocalScale(.6f, .6f, .6f);
            CG.createBox(model, "", T.v3f(.2f, .2f, 1.7f), T.v3f(0f, 0f, 2f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            CG.createBox(model, "", T.v3f(.15f, .2f, .3f), T.v3f(0f, .2f, 2f), T.getMaterialPath("brick"), T.v2f(1, 1));
        }
        public AK47(boolean left){
            super(Archetype.MODERN, Classification.ASSAULT, left);
            name = "AK47";
            audio = new WeaponAudio(name, 1.3f);
            ammo = new ReloadAmmo(30, 1.7f, left);
            damage = new RangedProjectileAttack("spiral(0,0.1,0.1)", "damage(5.5):destroy", 100f, 85f);
            recoils = new Recoils(50, 75, -19, 27);
            spread = new Spread(0, 20);
            automatic = true;
            cooldown = 0.14f;
            CreateModel();
        }
    }
    
    public static class LaserPistol extends RangedRechargeWeapon{
        protected final void CreateModel(){
            model.setLocalScale(.6f, .6f, .6f);
            CG.createBox(model, "", T.v3f(.2f, .2f, .9f), T.v3f(0, 0, 3.5f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            CG.createBox(model, "", T.v3f(.15f, .4f, .25f), T.v3f(0f, -.4f, 3f), T.getMaterialPath("wall"), T.v2f(1, 1));
        }
        public LaserPistol(boolean left){
            super(Archetype.ENERGY, Classification.PISTOL, left);
            name = "Laser Pistol";
            audio = new WeaponAudio(name, 1.3f);
            ammo = new RechargeAmmo(20, 0.5f, left);
            damage = new RangedRayAttack("damage(6.8)", 65f);
            recoils = new Recoils(40, 60, -15, 15);
            spread = new Spread(0, 15);
            automatic = false;
            cooldown = 0.2f;
            CreateModel();
        }
    }
    public static class Raygun extends RangedRechargeWeapon{
        protected final void CreateModel(){
            model.setLocalScale(.6f, .6f, .6f);
            CG.createCylinder(model, "", .2f, 3f, T.v3f(0, 0, 2.5f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
        }
        public Raygun(boolean left){
            super(Archetype.ENERGY, Classification.LASER, left);
            name = "Raygun";
            audio = new WeaponAudio(name, 0.5f);
            ammo = new RechargeAmmo(100, 0.2f, left);
            damage = new RangedRayAttack("damage(2.5)", 85f);
            recoils = new Recoils(15, 30, -10, 10);
            spread = new Spread(0, 5);
            automatic = true;
            cooldown = 0.06f;
            CreateModel();
        }
    }
    
    public static class RocketLauncher extends RangedReloadWeapon{
        protected final void CreateModel(){
            model.setLocalScale(.8f, .8f, .8f);
            CG.createCylinder(model, "", .3f, 4f, T.v3f(0, 0, 2.5f), T.getMaterialPath("brick"), T.v2f(1, 1));
        }
        public RocketLauncher(boolean left){
            super(Archetype.EXPLOSIVE, Classification.ASSAULT, left);
            name = "Rocket Launcher";
            audio = new WeaponAudio(name, 0.5f);
            ammo = new ReloadAmmo(1, 1.3f, left);
            damage = new RangedProjectileAttack("", "aoe(3, 3.4):destroy", 150f, 150f);
            recoils = new Recoils(180, 240, 30, -30);
            spread = new Spread(0, 0);
            automatic = false;
            cooldown = 0.3f;
            CreateModel();
        }
    }
    
    public static void initialize(GameClient app){
        Weapons.app = app;
    }
}
