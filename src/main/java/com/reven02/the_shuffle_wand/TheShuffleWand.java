package com.reven02.the_shuffle_wand;

import com.reven02.the_shuffle_wand.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheShuffleWand implements ModInitializer {
	public static final String MOD_ID = "the_shuffle_wand";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void log(String msg) {
		LOGGER.info("[{}] {}", MOD_ID, msg);
	}

	@Override
	public void onInitialize() {
		log("Initializing mod (server)");

		ModItems.initialize();
	}
}