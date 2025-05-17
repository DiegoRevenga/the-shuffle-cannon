package com.reven02.the_shuffle_cannon.item;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheShuffleCannon.MOD_ID);

    public static final DeferredItem<Item> SHUFFLE_CANNON = ITEMS.register("shuffle_cannon", () -> new Item(new Item.Properties()
            .setId(ResourceKey.create(ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(TheShuffleCannon.MOD_ID, "shuffle_cannon")))
    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SHUFFLE_CANNON);
        }
    }
}
