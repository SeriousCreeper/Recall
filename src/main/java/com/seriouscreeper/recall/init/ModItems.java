package com.seriouscreeper.recall.init;


import com.seriouscreeper.recall.items.ItemRecall;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {
    @ObjectHolder("recall:item_recall")
    public static ItemRecall itemRecall;

    /*
    @SideOnly(Side.CLIENT)
    public static void initModels() {
        itemRecall.initModel();
    }
    */
}
