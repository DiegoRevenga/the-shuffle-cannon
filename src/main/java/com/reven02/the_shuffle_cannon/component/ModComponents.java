package com.reven02.the_shuffle_cannon.component;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {

    public static final ComponentType<ShuffleCannonDataComponent> SHUFFLE_CANNON_DATA_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TheShuffleCannon.MOD_ID, "shuffle_cannon_data_component"),
            ComponentType.<ShuffleCannonDataComponent>builder().codec(ShuffleCannonDataComponent.CODEC).build()
    );

    public static void initialize() {
        TheShuffleCannon.log("Registering Mod Components");
    }
}
