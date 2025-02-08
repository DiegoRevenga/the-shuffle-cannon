package com.reven02.the_shuffle_cannon.gui.shuffle_cannon;

import net.minecraft.inventory.SimpleInventory;

public class ShuffleCannonInventory extends SimpleInventory {
    public ShuffleCannonInventory(int size) {
        super(size);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
}
