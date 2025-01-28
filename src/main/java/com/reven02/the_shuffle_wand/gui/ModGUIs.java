package com.reven02.the_shuffle_wand.gui;

import com.reven02.the_shuffle_wand.TheShuffleWand;
import com.reven02.the_shuffle_wand.item.custom.ShuffleWandItem;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class ModGUIs {

    public static ScreenHandlerType<ShuffleWandGUI> SCREEN_HANDLER_TYPE;

    public static void initialize() {
        TheShuffleWand.log("Registering mod GUIs");

        SCREEN_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, ShuffleWandItem.ID,
                new ScreenHandlerType<>((int syncId, PlayerInventory playerInventory) -> new ShuffleWandGUI(syncId, playerInventory, StackReference.EMPTY),
                        FeatureFlags.VANILLA_FEATURES));
    }

    public static void initializeClient() {
        TheShuffleWand.log("Registering mod GUIs (client)");

        HandledScreens.<ShuffleWandGUI, ShuffleWandScreen>register(SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new ShuffleWandScreen(gui, inventory.player.getInventory(), title));
    }
}
