package com.reven02.the_shuffle_wand.component;

import com.reven02.the_shuffle_wand.TheShuffleWand;
import com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent.ShuffleWandDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {

    public static final ComponentType<ShuffleWandDataComponent> SHUFFLE_WAND_DATA_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TheShuffleWand.MOD_ID, "shuffle_wand_data_component"),
            ComponentType.<ShuffleWandDataComponent>builder().codec(ShuffleWandDataComponent.CODEC).build()
    );

    public static void initialize() {
        TheShuffleWand.log("Registering Mod Components");
    }
}
