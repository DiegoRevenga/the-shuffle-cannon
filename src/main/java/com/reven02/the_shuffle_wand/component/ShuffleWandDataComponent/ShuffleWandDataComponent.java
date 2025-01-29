package com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

public record ShuffleWandDataComponent(List<Pair<Item, Integer>> wandContent) {

    public static final ShuffleWandDataComponent DEFAULT = new ShuffleWandDataComponent(List.of());

    public static ShuffleWandDataComponent of(Inventory inventory, ShuffleWandDataComponent oldData) {
        List<Pair<Item, Integer>> oldContent = oldData.wandContent();
        List<Pair<Item, Integer>> newContent = new ArrayList<>();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isOf(Items.AIR)) { continue; }

            Pair<Item, Integer> oldItem = oldContent.stream()
                    .filter(pair -> itemStack.isOf(pair.getFirst()))
                    .findFirst()
                    .orElse(null);

            if (oldItem != null) {  // Item was already in the wand
                newContent.add(oldItem);
            }
            else {  // Item is new -> Insert with default ratio = 1
                newContent.add(Pair.of(itemStack.getItem(), 1));
            }
        }

        return new ShuffleWandDataComponent(newContent);
    }

    static Codec<Item> itemCodec = Registries.ITEM.getCodec().fieldOf("item").codec();
    static Codec<Integer> ratioCodec = Codec.INT.fieldOf("ratio").codec();

    public static final Codec<ShuffleWandDataComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.pair(itemCodec, ratioCodec).listOf().fieldOf("wand_content").forGetter(ShuffleWandDataComponent::wandContent)
        ).apply(builder, ShuffleWandDataComponent::new);
    });
}
