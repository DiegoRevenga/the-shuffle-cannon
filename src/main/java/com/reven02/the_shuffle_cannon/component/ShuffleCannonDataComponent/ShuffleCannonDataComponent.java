package com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.reven02.the_shuffle_cannon.gui.shuffle_cannon.ShuffleCannonGUI;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.Collections;
import java.util.List;

public record ShuffleCannonDataComponent(List<Pair<Item, Integer>> cannonContent) {

    private static final List<Pair<Item, Integer>> EMPTY_CONTENT = Collections.nCopies(ShuffleCannonGUI.SIZE, Pair.of(Items.AIR, 1));

    public static final ShuffleCannonDataComponent DEFAULT = new ShuffleCannonDataComponent(EMPTY_CONTENT);

    static Codec<Item> itemCodec = Registries.ITEM.getCodec().fieldOf("item").codec();
    static Codec<Integer> ratioCodec = Codec.INT.fieldOf("ratio").codec();

    public static final Codec<ShuffleCannonDataComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.pair(itemCodec, ratioCodec).listOf().fieldOf("cannon_content").forGetter(ShuffleCannonDataComponent::cannonContent)
        ).apply(builder, ShuffleCannonDataComponent::new);
    });
}
