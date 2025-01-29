package com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

public record ShuffleWandDataComponent(List<Pair<Item, Integer>> wandContent) {

    public static final ShuffleWandDataComponent DEFAULT = new ShuffleWandDataComponent(List.of());

    public static ShuffleWandDataComponent add(ShuffleWandDataComponent data, Item newItem, Integer newRatio) {
        List<Pair<Item, Integer>> newData = new ArrayList<>(data.wandContent());
        newData.add(Pair.of(newItem, newRatio));

        return new ShuffleWandDataComponent(newData);
    }

    static Codec<Item> itemCodec = Registries.ITEM.getCodec().fieldOf("item").codec();
    static Codec<Integer> ratioCodec = Codec.INT.fieldOf("ratio").codec();

    public static final Codec<ShuffleWandDataComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.pair(itemCodec, ratioCodec).listOf().fieldOf("wand_content").forGetter(ShuffleWandDataComponent::wandContent)
        ).apply(builder, ShuffleWandDataComponent::new);
    });
}
