package com.reven02.the_shuffle_cannon;

import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.gui.ModGUIs;
import com.reven02.the_shuffle_cannon.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheShuffleCannon implements ModInitializer {
	public static final String MOD_ID = "the_shuffle_cannon";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void log(String msg) {
		LOGGER.info("[{}] {}", MOD_ID, msg);
	}

	@Override
	public void onInitialize() {
		log("Initializing mod (server)");

		ModItems.initialize();
		ModGUIs.initialize();
		ModComponents.initialize();
	}
}