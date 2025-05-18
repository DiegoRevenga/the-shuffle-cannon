package com.reven02.the_shuffle_cannon.item;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.item.custom.ShuffleCannonItem;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheShuffleCannon.MOD_ID);
    public static final DeferredItem<Item> SHUFFLE_CANNON = ITEMS.register(ShuffleCannonItem.ID, ShuffleCannonItem::new);

    public static void register(IEventBus eventBus) {
        TheShuffleCannon.log("registering mod items");
        ITEMS.register(eventBus);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SHUFFLE_CANNON);
        }
    }
}
