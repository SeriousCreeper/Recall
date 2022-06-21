package com.seriouscreeper.recall.config;


//import net.minecraftforge.common.config.Configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.seriouscreeper.recall.items.ItemRecall;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue PlaySounds;
    public static ForgeConfigSpec.BooleanValue EmitParticles;
    public static ForgeConfigSpec.BooleanValue AllowCrossDimension;

    static {
        COMMON_BUILDER.comment("General settings").push("general");

        PlaySounds = COMMON_BUILDER.comment("Play sounds when using item").define("soundsEnabled", true);
        EmitParticles = COMMON_BUILDER.comment("Emit particles when using item").define("particlesEnabled", true);
        AllowCrossDimension = COMMON_BUILDER.comment("Allow to teleport across dimensions").define("allowCrossDimension", true);
        ItemRecall.MaxDamage = COMMON_BUILDER.comment("Set max durability of item").defineInRange("durability", 50, 1, Integer.MAX_VALUE);
        ItemRecall.MaxDuration = COMMON_BUILDER.comment("How long it should take to use the item").defineInRange("casttime", 100, 1, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}
