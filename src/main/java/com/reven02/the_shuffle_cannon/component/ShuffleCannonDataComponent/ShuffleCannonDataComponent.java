package com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent;

import com.reven02.the_shuffle_cannon.item.custom.ShuffleCannonItem;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import io.netty.buffer.ByteBuf;

import java.util.Collections;
import java.util.List;

public record ShuffleCannonDataComponent(List<Pair<net.minecraft.world.item.Item, Integer>> cannonContent) {

    public static String ID = "shuffle_cannon_data_component";

    private static final List<Pair<Item, Integer>> EMPTY_CONTENT = Collections.nCopies(ShuffleCannonItem.INVENTORY_SIZE, Pair.of(Items.AIR, 1));

    public static final ShuffleCannonDataComponent DEFAULT = new ShuffleCannonDataComponent(EMPTY_CONTENT);

    static Codec<Item> itemCodec = BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").codec();
    static Codec<Integer> ratioCodec = Codec.INT.fieldOf("ratio").codec();

    public static final Codec<ShuffleCannonDataComponent> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            Codec.pair(itemCodec, ratioCodec).listOf().fieldOf("cannon_content").forGetter(ShuffleCannonDataComponent::cannonContent)
        ).apply(builder, ShuffleCannonDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, ShuffleCannonDataComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
