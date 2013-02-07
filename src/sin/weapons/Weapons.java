/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.weapons;

import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.data.DecalData;
import sin.data.ShotData;
import sin.hud.HUD;
import sin.network.Networking;
import sin.tools.T;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class Weapons{
    private static GameClient app;
    
    // Functionality & Enums:
    private static class Ammo{
        protected boolean reloadable = false;
        protected boolean reloading = false;
        protected boolean recharges = false;
        protected int clip;
        protected int max;
        private int barIndex;

        public Ammo(int max, boolean left){
            this.clip = max;
            this.max = max;
            if(left){
                this.barIndex = HUD.AMMO_LEFT;
            }else{
                this.barIndex = HUD.AMMO_RIGHT;
            }
            //app.hud.bar[barIndex].setMax(max);
            app.hud.setBarMax(barIndex, max);
            app.hud.updateBar(barIndex, clip);
        }

        public void updateBar(){
            app.hud.updateBar(barIndex, clip);
        }
        public void shot(){
            clip--;
            this.updateBar();
        }
        public float reload(){
            return 0;
        }
        public void recharge(float tpf){
            // Does nothing initially.
        }
        private static class ReloadAmmo extends Ammo{
            private float time;

            public ReloadAmmo(int max, float time, boolean left){
                super(max, left);
                this.time = time;
                reloadable = true;
            }

            @Override
            public float reload(){
                clip = max;
                return time;
            }
        }
        private static class RechargeAmmo extends Ammo{
            private float interval;
            private float time = 0;

            public RechargeAmmo(int max, float interval, boolean left){
                super(max, left);
                this.interval = interval;
                recharges = true;
            }

            @Override
            public void recharge(float tpf){
                time += tpf;
                if(clip < max && time >= interval){
                    clip++;
                    updateBar();
                    time = 0;
                }
            }
        }
    }
    private static class Damage{
        private float base;

        public Damage(float base){
            this.base = base;
        }

        public float getBase(){
            return base;
        }
        private float calculate(int part){
            float dmg = base;
            if(part == 0) {
                dmg *= 1.5f;
            }
            return dmg;
        }
        private int hitPlayer(String name){
            if(name.contains("head")) {
                return 0;
            }else if(name.contains("torso")) {
                return 1;
            }else if(name.contains("arm")) {
                return 2;
            }else if(name.contains("leg")) {
                return 3;
            }
            return -1;
        }
        private void handle(Ray ray){
            CollisionResults results = new CollisionResults();
            GameClient.collisionNode.collideWith(ray, results);
            if(results.size() > 0){
                CollisionResult closest = results.getClosestCollision();
                //createTracer(cam.getLocation(), closest.getContactPoint());
                World.CG.createLine(GameClient.tracerNode, "tracer", 3, app.getCamera().getLocation(), closest.getContactPoint(), ColorRGBA.Blue);
                int part = hitPlayer(closest.getGeometry().getName());
                if(part >= 0){
                    int player = Integer.parseInt(closest.getGeometry().getName().substring(0, 2));
                    float dmg = calculate(part);
                    app.hud.addFloatingText(GameClient.players[player].getLocation().clone().addLocal(T.v3f(0, 4, 0)), GameClient.character.getLocation(), dmg);
                    if(Networking.isConnected()) {
                        Networking.client.send(new ShotData(Networking.CLIENT_ID, player, dmg));
                    }
                }else{
                    app.dcs.createDecal(closest.getContactPoint());
                    if(Networking.isConnected()) {
                        Networking.client.send(new DecalData(closest.getContactPoint()));
                    }
                }
            }
        }
    }
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
            spread_mult += s_recoil*(app.recoil.getRecoil(true)+app.recoil.getRecoil(false));
            if(!GameClient.character.getPlayer().onGround()) {
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
    private static class WeaponAudio{
        private AudioNode fireNode;

        private static String GetSound(String weapon, String sound){
            return "Sounds/Weapons/"+weapon+"/"+sound+".ogg";
        }

        public WeaponAudio(String weapon, float fireVolume){
            fireNode = new AudioNode(app.getAssetManager(), GetSound(weapon, "fire"), false);
            fireNode.setPositional(false);
            fireNode.setVolume(fireVolume);
        }

        public void fire(){
            fireNode.playInstance();
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
        MELEE, PISTOL, ASSAULT, SNIPER, LAUNCHER, LASER
    }

    // Weapon Template:
    public static abstract class Weapon{
        // Required fields (Important):
        private Archetype archetype;
        private Classification classification;
        protected boolean left;

        // Helper Classes:
        protected Ammo ammo;
        protected Damage damage;
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

        public void setArchetype(Archetype archetype){
            this.archetype = archetype;
        }
        public void setClassification(Classification classification){
            this.classification = classification;
        }
        public Archetype getArchetype(){
            return archetype;
        }
        public Classification getClassification(){
            return classification;
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
            if(ammo.reloading){
                rot.multLocal(new Quaternion().fromAngleAxis(FastMath.PI/16, Vector3f.UNIT_X));
            }
            model.setLocalRotation(rot);
        }
        public void fire(){
            Vector3f target = app.getCamera().getDirection().clone();
            spread.apply(target);
            damage.handle(new Ray(app.getCamera().getLocation(), target));
            app.recoil.recoil(recoils.up(), recoils.left());
            ammo.shot();
            audio.fire();
            cooling += cooldown;
            muzzle.muzzle.emitAllParticles();
        }
        public void cool(float tpf){
            if(cooling != 0){
                cooling -= tpf;
                if(cooling < 0) {
                    cooling = 0;
                    ammo.updateBar();
                }
            }
            if(ammo.recharges) {
                ammo.recharge(tpf);
            }
            this.updateModel();
        }
        public void tick(float tpf){
            //if(!firing) return;
            if(firing && cooling == 0 && ammo.clip > 0){
                fire();
            }else if(ammo.clip == 0 && ammo.reloadable){
                ammo.reload();
            }
            this.cool(tpf);
        }

        public void enable(){
            GameClient.character.getNode().attachChild(model);
            app.hud.setBarMax(ammo.barIndex, ammo.max);
            ammo.updateBar();
        }
        public void enable(Node node){
            node.attachChild(model);
            app.hud.setBarMax(ammo.barIndex, ammo.max);
            ammo.updateBar();
        }
        public void disable(){
            GameClient.character.getNode().detachChild(model);
            firing = false;
        }
    }

    private static abstract class MeleeWeapon extends Weapon{
        protected abstract void CreateModel();
        public MeleeWeapon(Archetype archetype, Classification classification, boolean left){
            super(archetype, classification, left);
        }
    }
    public static abstract class RangedWeapon extends Weapon{
        protected abstract void CreateModel();
        public RangedWeapon(Archetype archetype, Classification classification, boolean left){
            super(archetype, classification, left);
        }
        public static abstract class RangedReloadWeapon extends RangedWeapon{
            protected abstract void CreateModel();
            public RangedReloadWeapon(Archetype archetype, Classification classification, boolean left){
                super(archetype, classification, left);
            }
            public void reload(){
                if(ammo.reloadable && cooling == 0) {
                    cooling += ammo.reload();
                }
            }
        }
        public static abstract class RangedEnergyWeapon extends RangedWeapon{
            protected abstract void CreateModel();
            public RangedEnergyWeapon(Archetype archetype, Classification classification, boolean left){
                super(archetype, classification, left);
            }
        }
        // Weapons:
        public static class M4A1 extends RangedReloadWeapon{
            protected final void CreateModel(){
                model.setLocalScale(.6f, .6f, .6f);
                World.CG.createCylinder(model, "", .15f, 2f, T.v3f(0, 0, 3.2f), "Textures/wall.png", T.v2f(1, 1));
                World.CG.createBox(model, "", T.v3f(.25f, .25f, 2.5f), T.v3f(0, 0, 1f), "Textures/BC_Tex.png", T.v2f(1, 1));
            }
            public M4A1(boolean left){
                super(Archetype.MODERN, Classification.ASSAULT, left);
                name = "M4A1";
                audio = new WeaponAudio(name, 1);
                ammo = new Ammo.ReloadAmmo(30, 1.2f, left);
                damage = new Damage(4.5f);
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
                World.CG.createBox(model, "", T.v3f(.2f, .2f, 1.7f), T.v3f(0f, 0f, 2f), "Textures/BC_Tex.png", T.v2f(1, 1));
                World.CG.createBox(model, "", T.v3f(.15f, .2f, .3f), T.v3f(0f, .2f, 2f), "Textures/brick.png", T.v2f(1, 1));
            }
            public AK47(boolean left){
                super(Archetype.MODERN, Classification.ASSAULT, left);
                name = "AK47";
                audio = new WeaponAudio(name, 1.3f);
                ammo = new Ammo.ReloadAmmo(30, 1.7f, left);
                damage = new Damage(5.5f);
                recoils = new Recoils(50, 75, -19, 27);
                spread = new Spread(0, 20);
                automatic = true;
                cooldown = 0.14f;
                CreateModel();
            }
        }
        public static class Raygun extends RangedEnergyWeapon{
            protected final void CreateModel(){
                model.setLocalScale(.6f, .6f, .6f);
                World.CG.createCylinder(model, "", .2f, 3f, T.v3f(0, 0, 2.5f), "Textures/BC_Tex.png", T.v2f(1, 1));
            }
            public Raygun(boolean left){
                super(Archetype.ENERGY, Classification.LASER, left);
                name = "Raygun";
                audio = new WeaponAudio(name, 0.5f);
                ammo = new Ammo.RechargeAmmo(100, 0.2f, left);
                damage = new Damage(2.5f);
                recoils = new Recoils(15, 30, -10, 10);
                spread = new Spread(0, 5);
                automatic = true;
                cooldown = 0.06f;
                CreateModel();
            }
        }
        public static class LaserPistol extends RangedEnergyWeapon{
            protected final void CreateModel(){
                model.setLocalScale(.6f, .6f, .6f);
                World.CG.createBox(model, "", T.v3f(.2f, .2f, .9f), T.v3f(0, 0, 3.5f), "Textures/BC_Tex.png", T.v2f(1, 1));
                World.CG.createBox(model, "", T.v3f(.15f, .4f, .25f), T.v3f(0f, -.4f, 3f), "Textures/wall.png", T.v2f(1, 1));
            }
            public LaserPistol(boolean left){
                super(Archetype.ENERGY, Classification.PISTOL, left);
                name = "LaserPistol";
                audio = new WeaponAudio(name, 1.3f);
                ammo = new Ammo.RechargeAmmo(20, 0.5f, left);
                damage = new Damage(6.8f);
                recoils = new Recoils(40, 60, -15, 15);
                spread = new Spread(0, 15);
                automatic = false;
                cooldown = 0.2f;
                CreateModel();
            }
        }
    }
    
    public static void initialize(GameClient app){
        Weapons.app = app;
    }
}
