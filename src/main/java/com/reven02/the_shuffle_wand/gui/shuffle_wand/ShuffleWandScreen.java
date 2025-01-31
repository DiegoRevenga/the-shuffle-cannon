package com.reven02.the_shuffle_wand.gui.shuffle_wand;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ShuffleWandScreen extends CottonInventoryScreen<ShuffleWandGUI> {

    public ShuffleWandScreen(ShuffleWandGUI description, PlayerInventory inventory, Text title) {
        super(description, inventory, title);
    }
}
