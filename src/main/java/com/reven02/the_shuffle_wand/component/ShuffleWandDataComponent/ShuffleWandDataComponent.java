package com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.Collections;
import java.util.List;

public record ShuffleWandDataComponent(List<Pair<Item, Integer>> wandContent) {

    private static final List<Pair<Item, Integer>> EMPTY_CONTENT = Collections.nCopies(9, Pair.of(Items.AIR, 1));

    public static final ShuffleWandDataComponent DEFAULT = new ShuffleWandDataComponent(EMPTY_CONTENT);

    static Codec<Item> itemCodec = Registries.ITEM.getCodec().fieldOf("item").codec();
    static Codec<Integer> ratioCodec = Codec.INT.fieldOf("ratio").codec();

    public static final Codec<ShuffleWandDataComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.pair(itemCodec, ratioCodec).listOf().fieldOf("wand_content").forGetter(ShuffleWandDataComponent::wandContent)
        ).apply(builder, ShuffleWandDataComponent::new);
    });
}
