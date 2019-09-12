package com.seriouscreeper.recall.init;


import com.seriouscreeper.recall.items.ItemRecall;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    @GameRegistry.ObjectHolder("recall:item_recall")
    public static ItemRecall itemRecall;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        itemRecall.initModel();
    }
}
