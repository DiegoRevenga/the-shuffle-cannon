package com.reven02.the_shuffle_cannon;

import com.reven02.the_shuffle_cannon.gui.ModGUIs;
import net.fabricmc.api.ClientModInitializer;

public class TheShuffleCannonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheShuffleCannon.log("Initializing mod (client)");

        ModGUIs.initializeClient();
    }
}
