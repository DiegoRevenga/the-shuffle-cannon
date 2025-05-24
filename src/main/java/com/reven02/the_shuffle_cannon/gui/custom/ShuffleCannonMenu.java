package com.reven02.the_shuffle_cannon.gui.custom;

import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.gui.ModMenuTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ShuffleCannonMenu extends AbstractContainerMenu {

    public final int CANNON_INVENTORY_Y = 19;
    public final int PLAYER_INVENTORY_Y = 102;
    public final int HOTBAR_Y = 160;

    public final ItemStack owner;
    private final Container container = new SimpleContainer(9);
    private final int[] sliderValues = new int[9];

    // TODO Save inventory

    // Client constructor
    public ShuffleCannonMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, extraData.readJsonWithCodec(ItemStack.CODEC));
    }

    // Server constructor
    public ShuffleCannonMenu(int id, Inventory playerInventory, ItemStack owner) {
        super(ModMenuTypes.SHUFFLE_CANNON_MENU.get(), id);

        this.owner = owner;
        this.populateInventory(owner);

        // Add 9 custom slots
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(
                    container, i,
                    8 + i * 18,
                    CANNON_INVENTORY_Y
            ));
        }

        // Add player inventory
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(
                        playerInventory, l + i * 9 + 9,
                        8 + l * 18,
                        PLAYER_INVENTORY_Y + i * 18
                ));
            }
        }

        // Add player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(
                    playerInventory, i,
                    8 + i * 18,
                    HOTBAR_Y
            ));
        }

        for (int i = 0; i < 9; i++) {
            this.addDataSlot(DataSlot.shared(sliderValues, i));
        }
    }

    private void populateInventory(ItemStack shuffleCannonStack) {
        ShuffleCannonDataComponent data = shuffleCannonStack.get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) {
            return;
        }

        for (int i = 0; i < data.cannonContent().size(); i++) {
            this.container.setItem(i, data.cannonContent().get(i).getFirst().getDefaultInstance());
            this.sliderValues[i] = data.cannonContent().get(i).getSecond();
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return null; // FIXME
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

