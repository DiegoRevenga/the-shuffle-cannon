package com.reven02.the_shuffle_wand.gui.shuffle_wand;

import com.mojang.serialization.Codec;
import com.reven02.the_shuffle_wand.TheShuffleWand;
import com.reven02.the_shuffle_wand.component.ModComponents;
import com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent.ShuffleWandDataComponent;
import io.github.cottonmc.cotton.gui.ItemSyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.IntConsumer;

import static com.reven02.the_shuffle_wand.gui.ModGUIs.SHUFFLE_WAND_SCREEN_HANDLER_TYPE;

public class ShuffleWandGUI extends ItemSyncedGuiDescription {

    static final int SIZE = 9;

    SimpleInventory wandInventory;

    public ShuffleWandGUI(int syncId, PlayerInventory playerInventory, StackReference owner) {
        super(SHUFFLE_WAND_SCREEN_HANDLER_TYPE, syncId, playerInventory, owner);

        this.wandInventory = new ShuffleWandInventory(SIZE);
        this.populateInventory();
        this.wandInventory.addListener(this::saveInventory);

        // Root panel
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(new Insets(0, 7, 7, 7));

        // Item slots
        WItemSlot itemSlot = WItemSlot.of(wandInventory, 0, SIZE, 1);
        itemSlot.setInputFilter(this::filter);  // Avoid duplicates
        root.add(itemSlot, 0, 1);

        // Ratio sliders and displays
        for (int i = 0; i < SIZE; i++) {
            WSlider slider = new WSlider(1, 10, Axis.VERTICAL);
            slider.setValue(this.getRatio(i));
            slider.setValueChangeListener(saveRatio(i));
            root.add(slider, i, 2, 1, 2);

            WDynamicLabel label = new WDynamicLabel(() -> Integer.toString(slider.getValue()));
            label.setAlignment(HorizontalAlignment.CENTER);
            label.setVerticalAlignment(VerticalAlignment.CENTER);
            root.add(label, i, 4);
        }

        root.add(this.createPlayerInventoryPanel(), 0, 5);

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

        wandItemStack.set(ModComponents.SHUFFLE_WAND_DATA_COMPONENT, ShuffleWandDataComponent.fromNewInventory(inventory, oldData));
    }

    private int getRatio(int index) {
        final ShuffleWandDataComponent data = this.ownerStack.get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT);
        if (data == null || index >= data.wandContent().size()) {
            return 1;
        }
        return (data.wandContent().get(index).getSecond());  // FIXME Doesn't work
    }

    // TODO Send new value to server
    private IntConsumer saveRatio(int index) {
        ItemStack wandItemStack = this.owner.get();

        ShuffleWandDataComponent oldData = wandItemStack.get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT);
        if (oldData == null) {
            return value -> {};
        }

        Identifier MSG_ID = Identifier.of(TheShuffleWand.MOD_ID, String.format("slider_%d", index));

        // Receive message from client side
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(MSG_ID, Codec.INT, newRatio -> {
            wandItemStack.set(ModComponents.SHUFFLE_WAND_DATA_COMPONENT, ShuffleWandDataComponent.fromNewRatio(newRatio, index, oldData));
        });

        return value -> {
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(MSG_ID, Codec.INT, value);
        };
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
