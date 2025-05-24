package com.reven02.the_shuffle_cannon.gui;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.gui.custom.ShuffleCannonMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TheShuffleCannon.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ShuffleCannonMenu>> SHUFFLE_CANNON_MENU = MENUS.register(
            "shuffle_cannon_menu",
            () -> IMenuTypeExtension.create(ShuffleCannonMenu::new)
    );

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
