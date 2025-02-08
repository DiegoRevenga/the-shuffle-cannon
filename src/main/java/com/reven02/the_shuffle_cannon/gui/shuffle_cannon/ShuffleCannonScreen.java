package com.reven02.the_shuffle_cannon.gui.shuffle_cannon;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ShuffleCannonScreen extends CottonInventoryScreen<ShuffleCannonGUI> {

    public ShuffleCannonScreen(ShuffleCannonGUI description, PlayerInventory inventory, Text title) {
        super(description, inventory, title);
    }
}
