package com.seriouscreeper.recall.config;


import com.seriouscreeper.recall.items.ItemRecall;
import com.seriouscreeper.recall.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class Config {
    public static boolean PlaySounds = true;
    public static boolean EmitParticles = true;
    //public static boolean AllowCrossDimension = true;

    public static void readConfig() {
        Configuration cfg = CommonProxy.config;

        try {
            cfg.load();
            ItemRecall.MaxDamage = cfg.getInt("durability", "General", 50, 1, Integer.MAX_VALUE, "Set max durability of item");
            ItemRecall.MaxDuration = cfg.getInt("casttime", "General", 100, 1, Integer.MAX_VALUE, "How long it should take to use the item");
            PlaySounds = cfg.getBoolean("soundsEnabled", "General", true, "Play sounds when using item");
            EmitParticles = cfg.getBoolean("particlesEnabled", "General", true, "Emit particles when using item");
        } catch(Exception e) {
        } finally {
            if(cfg.hasChanged())
                cfg.save();
        }
    }
}
