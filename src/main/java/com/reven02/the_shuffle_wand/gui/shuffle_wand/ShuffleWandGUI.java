package com.reven02.the_shuffle_wand.gui.shuffle_wand;

import com.reven02.the_shuffle_wand.TheShuffleWand;
import com.reven02.the_shuffle_wand.component.ModComponents;
import com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent.ShuffleWandDataComponent;
import io.github.cottonmc.cotton.gui.GuiDescription;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.reven02.the_shuffle_wand.gui.ModGUIs.SCREEN_HANDLER_TYPE;

public class ShuffleWandGUI extends ItemSyncedGuiDescription {

    static final int SIZE = 9;
//    List<WSlider> sliders = new ArrayList<>();

    SimpleInventory wandInventory;

    public ShuffleWandGUI(int syncId, PlayerInventory playerInventory, StackReference owner) {
        super(SCREEN_HANDLER_TYPE, syncId, playerInventory, owner);

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
//            this.sliders.add(slider);
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

        wandItemStack.set(ModComponents.SHUFFLE_WAND_DATA_COMPONENT, ShuffleWandDataComponent.of(inventory, oldData));
    }

//    private void setSliderValues() {
//        final ShuffleWandDataComponent[] data = {this.ownerStack.get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT)};
//
//        final Identifier MSG_ID = Identifier.of(TheShuffleWand.MOD_ID, "gui_slider");
//        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(MSG_ID, ShuffleWandDataComponent.CODEC, serverData -> {
//            data[0] = serverData;
//        });
//        ScreenNetworking.of(this, NetworkSide.SERVER).send(MSG_ID, ShuffleWandDataComponent.CODEC, data[0]);
//
//        for (int i = 0; i < this.sliders.size(); i++) {
//            if (data[0] != null && i < data[0].wandContent().size()) {
//                sliders.get(i).setValue(data[0].wandContent().get(i).getSecond());  // FIXME Doesn't work
//            }
//        }
//    }

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
