package com.seriouscreeper.recall;

import com.seriouscreeper.recall.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Recall.MODID, name = Recall.NAME, version = Recall.VERSION)
public class Recall
{
    public static final String MODID = "recall";
    public static final String NAME = "Recall";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @SidedProxy(clientSide = "com.seriouscreeper.recall.proxy.ClientProxy", serverSide = "com.seriouscreeper.recall.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static Recall instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
