package com.reven02.the_shuffle_wand;

import com.reven02.the_shuffle_wand.gui.ModGUIs;
import net.fabricmc.api.ClientModInitializer;

public class TheShuffleWandClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheShuffleWand.log("Initializing mod (client)");

        ModGUIs.initializeClient();
    }
}
