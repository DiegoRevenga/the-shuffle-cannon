package com.reven02.the_shuffle_cannon.item;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.item.custom.ShuffleCannonItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item SHUFFLE_CANNON = register(ShuffleCannonItem.ID, new ShuffleCannonItem());
    public static final Item ENDER_DUST = register(Identifier.of(TheShuffleCannon.MOD_ID, "ender_dust"), new Item(new Item.Settings()));


    private static Item register(Identifier identifier, Item item) {
        return Registry.register(Registries.ITEM, identifier, item);
    }

    public static void initialize() {
        TheShuffleCannon.log("Registering Mod Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SHUFFLE_CANNON);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ENDER_DUST);
        });
    }
}
