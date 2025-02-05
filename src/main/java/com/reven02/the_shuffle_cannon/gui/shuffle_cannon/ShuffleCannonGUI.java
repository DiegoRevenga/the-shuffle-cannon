package com.reven02.the_shuffle_cannon.gui.shuffle_cannon;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.item.ModItems;
import com.reven02.the_shuffle_cannon.item.custom.ShuffleCannonItem;
import io.github.cottonmc.cotton.gui.ItemSyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import static com.reven02.the_shuffle_cannon.gui.ModGUIs.SHUFFLE_CANNON_SCREEN_HANDLER_TYPE;

public class ShuffleCannonGUI extends ItemSyncedGuiDescription {

    public static final int SIZE = 9;
    public static final int MAX_RATIO = 10;

    private final SimpleInventory wandInventory;
    private final List<WSlider> sliders = new ArrayList<>();

    public ShuffleCannonGUI(int syncId, PlayerInventory playerInventory, StackReference owner) {
        super(SHUFFLE_CANNON_SCREEN_HANDLER_TYPE, syncId, playerInventory, owner);

        this.wandInventory = new ShuffleCannonInventory(SIZE);
        this.populateInventory();
        this.wandInventory.addListener(inv -> this.saveInfo());

        // Root panel
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(new Insets(0, 7, 7, 7));

        // Item slots
        WItemSlot itemSlot = WItemSlot.of(wandInventory, 0, SIZE, 1);
        itemSlot.setInputFilter(this::slotFilter);  // Avoid duplicates
        root.add(itemSlot, 0, 1);

        // Ratio sliders and displays
        for (int i = 0; i < SIZE; i++) {
            WSlider slider = new WSlider(1, MAX_RATIO, Axis.VERTICAL);
            this.sliders.add(slider);
            slider.setValue(this.getRatio(i));
            slider.setDraggingFinishedListener(sliderListener(i));

            root.add(slider, i, 2, 1, 2);

            WDynamicLabel label = new WDynamicLabel(() -> Integer.toString(slider.getValue()));
            label.setAlignment(HorizontalAlignment.CENTER);
            label.setVerticalAlignment(VerticalAlignment.CENTER);
            root.add(label, i, 4);
        }

        root.add(this.createPlayerInventoryPanel(), 0, 5);

        root.validate(this);
    }

    private void saveInfo() {
        List<Pair<Item, Integer>> newContent = new ArrayList<>(SIZE);

        for (int i = 0; i < this.wandInventory.size(); i++) {
            ItemStack itemStack = this.wandInventory.getStack(i);
            WSlider slider = this.sliders.get(i);

            newContent.add(Pair.of(itemStack.getItem(), slider.getValue()));
        }

        this.owner.get().set(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, new ShuffleCannonDataComponent(newContent));
    }

    private void populateInventory() {
        ShuffleCannonDataComponent data = this.owner.get().get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) throw ShuffleCannonItem.CANNON_MISSING_ERROR;

        for (int i = 0; i < SIZE; i++) {
            Item item = data.cannonContent().get(i).getFirst();

            this.wandInventory.setStack(i, item.getDefaultStack());
        }
    }

    private int getRatio(int index) {
        final ShuffleCannonDataComponent data = this.owner.get().get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) throw ShuffleCannonItem.CANNON_MISSING_ERROR;

        return (data.cannonContent().get(index).getSecond());
    }

    private IntConsumer sliderListener(int index) {
        Identifier MSG_ID = Identifier.of(TheShuffleCannon.MOD_ID, String.format("slider_%d", index));

        // Receive: [Server] <- Client
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(MSG_ID, Codec.INT, newRatio -> {
            this.sliders.get(index).setValue(newRatio);
            saveInfo();
        });

        // Send: [Client] -> Server
        return value -> ScreenNetworking.of(this, NetworkSide.CLIENT).send(MSG_ID, Codec.INT, value);
    }

    private boolean slotFilter(ItemStack stack) {
        ShuffleCannonDataComponent data = this.owner.get().get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) {
            return false;
        }

        boolean isShuffleCannon = stack.isOf(ModItems.SHUFFLE_CANNON);
        boolean isBlock = stack.getItem() instanceof BlockItem;
        if (isShuffleCannon || !isBlock) {
            return false;
        }

        boolean duplicated = data.cannonContent().stream().anyMatch(pair -> stack.isOf(pair.getFirst()));
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
