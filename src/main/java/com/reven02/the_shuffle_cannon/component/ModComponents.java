package com.reven02.the_shuffle_cannon.component;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModComponents {

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TheShuffleCannon.MOD_ID);

    public static final Supplier<DataComponentType<ShuffleCannonDataComponent>> SHUFFLE_CANNON_DATA_COMPONENT = COMPONENTS.registerComponentType(
            ShuffleCannonDataComponent.ID,
            builder -> builder
                    .persistent(ShuffleCannonDataComponent.CODEC)
                    .networkSynchronized(ShuffleCannonDataComponent.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        TheShuffleCannon.log("registering mod components");
        COMPONENTS.register(eventBus);
    }
}
