package com.reven02.the_shuffle_wand.gui.shuffle_wand;

import net.minecraft.inventory.SimpleInventory;

public class ShuffleWandInventory extends SimpleInventory {
    public ShuffleWandInventory(int size) {
        super(size);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
}
