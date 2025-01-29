package com.reven02.the_shuffle_wand.gui;

import com.reven02.the_shuffle_wand.component.ModComponents;
import com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent.ShuffleWandDataComponent;
import io.github.cottonmc.cotton.gui.ItemSyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.reven02.the_shuffle_wand.gui.ModGUIs.SCREEN_HANDLER_TYPE;

public class ShuffleWandGUI extends ItemSyncedGuiDescription {

    static final int SIZE = 9;

    Inventory wandInventory = new SimpleInventory(SIZE);

    public ShuffleWandGUI(int syncId, PlayerInventory playerInventory, StackReference owner) {
        super(SCREEN_HANDLER_TYPE, syncId, playerInventory, owner);

        this.populateInventory();

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(wandInventory, 0, SIZE, 1);
//        itemSlot.addChangeListener(this::changeListener);
        root.add(itemSlot, 0, 1);

        root.add(this.createPlayerInventoryPanel(), 0, 3);

        root.validate(this);
    }

    @Override
    public boolean canUse(PlayerEntity entity) {
        ItemStack left = ownerStack;
        ItemStack right = owner.get();

        // Just checks count and item
        if (left == right) {
            return true;
        } else {
            return left.getCount() == right.getCount() && left.isOf(right.getItem());
        }
    }

    private void populateInventory() {
        ShuffleWandDataComponent data = this.ownerStack.get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT);
        if (data != null) {
            for (int i = 0; i < data.wandContent().size(); i++) {
                if (i >= SIZE) { break; }

                Item item = data.wandContent().get(i).getFirst();
                Integer ratio = data.wandContent().get(i).getSecond();

                this.wandInventory.setStack(i, item.getDefaultStack());
            }
        }
    }

    /**
     * Handles a changed item stack in an item slot.
     *
     * @param slot      the item slot widget
     * @param inventory the item inventory of the slot
     * @param index     the index of the slot in the inventory
     * @param stack     the changed item stack
     */
    private void changeListener(WItemSlot slot, Inventory inventory, int index, ItemStack stack) {
        ItemStack wandItemStack = this.owner.get();

        BundleContentsComponent content = wandItemStack.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (content == null) {
            return;
        }

        // Adding an item
        if (!stack.isEmpty()) {
            List<ItemStack> itemStacks = new ArrayList<>(content.stream().toList());
            itemStacks.add(stack);

            wandItemStack.set(DataComponentTypes.BUNDLE_CONTENTS,  new BundleContentsComponent(itemStacks));
        }
    }
}
