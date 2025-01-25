package com.reven02.the_shuffle_wand.item;

import com.reven02.the_shuffle_wand.TheShuffleWand;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final RegistryKey<Item> SHUFFLE_WAND_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(TheShuffleWand.MOD_ID, "shuffle_wand"));
    public static final Item SHUFFLE_WAND = register(
            new Item(new Item.Settings().maxCount(1)),
            SHUFFLE_WAND_KEY
    );

    public static final RegistryKey<Item> ENDER_DUST_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(TheShuffleWand.MOD_ID, "ender_dust"));
    public static final Item ENDER_DUST = register(
            new Item(new Item.Settings()),
            ENDER_DUST_KEY
    );

    private static Item register(Item item, RegistryKey<Item> registryKey) {
        return Registry.register(Registries.ITEM, registryKey.getValue(), item);
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
