package com.reven02.the_shuffle_wand.item;

import com.reven02.the_shuffle_wand.TheShuffleWand;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item SHUFFLE_WAND = register("shuffle_wand", new Item(new Item.Settings().maxCount(1)));
    public static final Item ENDER_DUST = register("ender_dust", new Item(new Item.Settings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(TheShuffleWand.MOD_ID, name), item);
    }

    public static void initialize() {
        TheShuffleWand.log("Registering Mod Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SHUFFLE_WAND);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ENDER_DUST);
        });
    }
}
