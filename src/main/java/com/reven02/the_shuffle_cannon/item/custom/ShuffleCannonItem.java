package com.reven02.the_shuffle_cannon.item.custom;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.item.ModItems;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShuffleCannonItem extends BlockItem {

    // TODO Move these constants to its proper classes once created
    public static int INVENTORY_SIZE = 9;
    public static int MAX_RATIO = 10;

    public static final String ID = "shuffle_cannon";

    public ShuffleCannonItem() {
        super(Blocks.AIR, new Item.Properties()
                .setId(ResourceKey.create(ModItems.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(TheShuffleCannon.MOD_ID, ID)))
                .stacksTo(1)
                .component(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        ShuffleCannonDataComponent data = stack.get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data != null) {
            List<Pair<Item, Integer>> content = data.cannonContent().stream()
                    .filter(p -> p.getFirst() != Items.AIR) // Skip gaps in the inventory
                    .toList();

            if (!content.isEmpty()) {
                tooltip.add(Component.empty()); // Line break
            }

            for (Pair<Item, Integer> pair : content) {
                Item item = pair.getFirst();
                Integer ratio = pair.getSecond();

                MutableComponent tooltipText = Component.empty();
                tooltipText.append(Component.literal("◆ ").withColor(ChatFormatting.LIGHT_PURPLE.getColor()));
                tooltipText.append(Component.literal(item.getName().getString()).withColor(ChatFormatting.GRAY.getColor()));
                tooltipText.append(" ");
                tooltipText.append(Component.literal("■".repeat(ratio)).withColor(ChatFormatting.BLUE.getColor()));
                tooltipText.append(Component.literal("□".repeat(MAX_RATIO - ratio)).withColor(ChatFormatting.GRAY.getColor()));

                tooltip.add(tooltipText);
            }
        }
    }
}
