package com.reven02.the_shuffle_wand.gui.shuffle_wand;

import com.reven02.the_shuffle_wand.component.ModComponents;
import com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent.ShuffleWandDataComponent;
import io.github.cottonmc.cotton.gui.ItemSyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.reven02.the_shuffle_wand.gui.ModGUIs.SCREEN_HANDLER_TYPE;

public class ShuffleWandGUI extends ItemSyncedGuiDescription {

    static final int SIZE = 9;

    SimpleInventory wandInventory;

    public ShuffleWandGUI(int syncId, PlayerInventory playerInventory, StackReference owner) {
        super(SCREEN_HANDLER_TYPE, syncId, playerInventory, owner);

        this.wandInventory = new ShuffleWandInventory(SIZE);
        this.populateInventory();
        this.wandInventory.addListener(this::saveInventory);

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(wandInventory, 0, SIZE, 1);

        // Avoid duplicates
        itemSlot.setInputFilter(this::filter);

        root.add(itemSlot, 0, 1);
        root.add(this.createPlayerInventoryPanel(), 0, 3);
        root.validate(this);
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

    private void saveInventory(Inventory inventory) {
        ItemStack wandItemStack = this.owner.get();

        ShuffleWandDataComponent oldData = wandItemStack.get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT);
        if (oldData == null) {
            return;
        }

        wandItemStack.set(ModComponents.SHUFFLE_WAND_DATA_COMPONENT, ShuffleWandDataComponent.of(inventory, oldData));
    }

    private boolean filter(ItemStack stack) {
        ShuffleWandDataComponent data = this.owner.get().get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT);
        if (data == null) {
            return false;
        }

        boolean isBlock = stack.getItem() instanceof BlockItem;
        if (!isBlock) {
            return false;
        }

        boolean duplicated = data.wandContent().stream().anyMatch(pair -> stack.isOf(pair.getFirst()));
        return !duplicated;
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
}
