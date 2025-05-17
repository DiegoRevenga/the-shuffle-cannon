package com.reven02.the_shuffle_cannon.item.custom;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.item.ModItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

public class ShuffleCannonItem extends BlockItem {

    public static final String ID = "shuffle_cannon";

    public ShuffleCannonItem() {
        super(Blocks.AIR, new Item.Properties()
                .setId(ResourceKey.create(ModItems.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(TheShuffleCannon.MOD_ID, ID)))
                .stacksTo(1)
                .component(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT)
        );
    }
}
