package sin.weapons;

import sin.GameClient;
import sin.hud.BarManager.BH;

/**
 * Ammo Manager - Used for everything ammo.
 * @author SinisteRing
 */
public class AmmoManager {
    private static GameClient app;
    
    public static class Ammo{
        private boolean reloading = false;
        private int clip;
        private int max;
        private BH barIndex;

        public Ammo(int max, boolean left){
            this.clip = max;
            this.max = max;
            if(left){
                this.barIndex = BH.AMMO_LEFT;
            }else{
                this.barIndex = BH.AMMO_RIGHT;
            }
            //app.hud.bar[barIndex].setMax(max);
            app.getHUD().setBarMax(barIndex, max);
            app.getHUD().updateBar(barIndex, clip);
        }
        
        public void incClip(){
            this.clip++;
        }
        public void decClip(){
            this.clip--;
        }
        
        public void setReloading(boolean reloading){
            this.reloading = reloading;
        }
        public boolean isReloading(){
            return reloading;
        }
        public void setClip(int clip){
            this.clip = clip;
        }
        public int getClip(){
            return clip;
        }
        public int getMax(){
            return max;
        }
        public BH getBarIndex(){
            return barIndex;
        }

        public void updateBar(){
            app.getHUD().updateBar(barIndex, clip);
        }
        public float reload(){
            return 0;
        }
        public void recharge(float tpf){
            // Does nothing initially.
        }
    }
    public static class ReloadAmmo extends Ammo{
        private float time;

        public ReloadAmmo(int max, float time, boolean left){
            super(max, left);
            this.time = time;
        }

        @Override
        public float reload(){
            this.setReloading(true);
            this.setClip(this.getMax());
            return time;
        }
    }
    public static class RechargeAmmo extends Ammo{
        private float interval;
        private float time = 0;

        public RechargeAmmo(int max, float interval, boolean left){
            super(max, left);
            this.interval = interval;
        }

        @Override
        public void recharge(float tpf){
            if(this.getClip() < this.getMax()){
                time += tpf;
                if(time >= interval){
                    this.incClip();
                    updateBar();
                    time = 0;
                }
            }
        }
    }
    
    public static void initialize(GameClient app){
        AmmoManager.app = app;
    }
}
