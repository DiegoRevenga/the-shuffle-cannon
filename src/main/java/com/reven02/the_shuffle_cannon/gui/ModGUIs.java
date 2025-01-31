package com.reven02.the_shuffle_cannon.gui;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.gui.shuffle_cannon.ShuffleCannonGUI;
import com.reven02.the_shuffle_cannon.gui.shuffle_cannon.ShuffleCannonScreen;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModGUIs {

    public static ScreenHandlerType<ShuffleCannonGUI> SHUFFLE_CANNON_SCREEN_HANDLER_TYPE;

    public static void initialize() {
        TheShuffleCannon.log("Registering mod GUIs");

        SHUFFLE_CANNON_SCREEN_HANDLER_TYPE = new ExtendedScreenHandlerType<ShuffleCannonGUI, ItemStack>((syncId, playerInventory, cannonStack) -> {
            StackReference cannonReference = StackReference.of(() -> cannonStack, stack -> {});
            return new ShuffleCannonGUI(syncId, playerInventory, cannonReference);
        }, PacketCodecs.codec(ItemStack.CODEC).cast());

        Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(TheShuffleCannon.MOD_ID, "shuffle_cannon_gui"),
                SHUFFLE_CANNON_SCREEN_HANDLER_TYPE
        );
    }

    public static void initializeClient() {
        TheShuffleCannon.log("Registering mod GUIs (client)");

        HandledScreens.<ShuffleCannonGUI, ShuffleCannonScreen>register(
                SHUFFLE_CANNON_SCREEN_HANDLER_TYPE,
                ShuffleCannonScreen::new
        );
    }
}
