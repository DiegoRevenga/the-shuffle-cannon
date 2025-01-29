//package com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.item.Item;
//import net.minecraft.registry.Registries;
//
//public record ShuffleWandInfo(Item item, int ratio) {
//
//    public static final Codec<ShuffleWandInfo> CODEC = RecordCodecBuilder.create(builder -> {
//        return builder.group(
//                Registries.ITEM.getCodec().fieldOf("item").forGetter(ShuffleWandInfo::item),
//                Codec.INT.fieldOf("ratio").forGetter(ShuffleWandInfo::ratio)
//        ).apply(builder, ShuffleWandInfo::new);
//    });
//}
