package com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.Map;

public record ShuffleWandDataComponent(Map<Item, Integer> wandContent) {

    public static final ShuffleWandDataComponent DEFAULT = new ShuffleWandDataComponent(Map.of());

    public static final Codec<ShuffleWandDataComponent> CODEC = RecordCodecBuilder.create(builder -> {
        Codec<Map<Item, Integer>> mapCodec = Codec.unboundedMap(Registries.ITEM.getCodec(), Codec.INT);

        return builder.group(
                mapCodec.fieldOf("wand_content").forGetter(ShuffleWandDataComponent::wandContent)
        ).apply(builder, ShuffleWandDataComponent::new);
    });
}
