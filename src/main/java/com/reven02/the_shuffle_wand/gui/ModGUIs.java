package com.reven02.the_shuffle_wand.gui;

import com.reven02.the_shuffle_wand.TheShuffleWand;
import com.reven02.the_shuffle_wand.gui.shuffle_wand.ShuffleWandGUI;
import com.reven02.the_shuffle_wand.gui.shuffle_wand.ShuffleWandScreen;
import com.reven02.the_shuffle_wand.item.custom.ShuffleWandItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModGUIs {

    public static ScreenHandlerType<ShuffleWandGUI> SHUFFLE_WAND_SCREEN_HANDLER_TYPE;

    public static void initialize() {
        TheShuffleWand.log("Registering mod GUIs");

        SHUFFLE_WAND_SCREEN_HANDLER_TYPE = new ExtendedScreenHandlerType<ShuffleWandGUI, ItemStack>((syncId, playerInventory, wandStack) -> {
            StackReference wandReference = StackReference.of(() -> wandStack, stack -> { return; });
            return new ShuffleWandGUI(syncId, playerInventory, wandReference);
        }, PacketCodecs.codec(ItemStack.CODEC).cast());

        Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(TheShuffleWand.MOD_ID, "shuffle_wand_gui"),
                SHUFFLE_WAND_SCREEN_HANDLER_TYPE
        );
    }

    public static void initializeClient() {
        TheShuffleWand.log("Registering mod GUIs (client)");

        HandledScreens.<ShuffleWandGUI, ShuffleWandScreen>register(
                SHUFFLE_WAND_SCREEN_HANDLER_TYPE,
                ShuffleWandScreen::new
        );
    }
}
