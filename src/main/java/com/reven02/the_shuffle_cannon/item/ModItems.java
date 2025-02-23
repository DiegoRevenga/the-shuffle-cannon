package com.reven02.the_shuffle_cannon.item;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.item.custom.ShuffleCannonItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModItems {

    public static final Item SHUFFLE_CANNON = register(ShuffleCannonItem.SHUFFLE_CANNON_KEY, new ShuffleCannonItem());

    private static Item register(RegistryKey<Item> key, Item item) {
        return Registry.register(Registries.ITEM, key.getValue(), item);
    }

    public static void initialize() {
        TheShuffleCannon.log("Registering Mod Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SHUFFLE_CANNON);
        });
    }
}
