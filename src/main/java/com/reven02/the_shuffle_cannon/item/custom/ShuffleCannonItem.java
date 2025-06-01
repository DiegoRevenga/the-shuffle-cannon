package com.reven02.the_shuffle_cannon.item.custom;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.gui.custom.ShuffleCannonMenu;
import com.reven02.the_shuffle_cannon.gui.lib.VerticalSlider;
import com.reven02.the_shuffle_cannon.item.ModItems;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ShuffleCannonItem extends BlockItem implements MenuProvider {

    // TODO Move these constants to their proper classes once created
    public static int INVENTORY_SIZE = 9;
    public static int MAX_RATIO = 10;

    public static final RuntimeException CANNON_MISSING_ERROR = new IllegalStateException("Cannon StackReference is missing");

    public static final String ID = "shuffle_cannon";

    private static final Random RANDOM = new Random();

    public ShuffleCannonItem() {
        super(Blocks.AIR, new Item.Properties()
                .setId(ResourceKey.create(ModItems.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(TheShuffleCannon.MOD_ID, ID)))
                .stacksTo(1)
                .component(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT)
        );
    }

    /**
     * Opens the Shuffle Cannon GUI if sneaking.
     */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        InteractionResult result = super.useOn(context);

        // Avoid spending the Shuffle Cannon item when placing blocks
        context.getItemInHand().setCount(1);

        return result;
    }

    /**
     * Picks a random block from the item content
     */
    @Override
    protected @Nullable BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        if (context.getLevel().isClientSide()) {
            return null;
        }

        ShuffleCannonDataComponent data = context.getItemInHand().get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) {
            throw CANNON_MISSING_ERROR;
        }

        List<Pair<Item, Integer>> content = data.cannonContent().stream()
                .filter(p -> p.getFirst() != Items.AIR)  // Skip gaps in the inventory
                .toList();

        if (content.isEmpty()) {
            return null;
        }

        int totalWeight = content.stream().mapToInt(Pair::getSecond).sum();
        int randomValue = RANDOM.nextInt(totalWeight);

        System.out.printf("Total weight: %d\n" , totalWeight);
        System.out.printf("random: %d\n" , randomValue);

        BlockItem selectedBlock = null;

        // Choose random block
        int cumulativeWeight = 0;
        for (Pair<Item, Integer> pair : content) {
            cumulativeWeight += pair.getSecond();
            System.out.printf("cumulativeWeight: %d\n" , cumulativeWeight);

            if (randomValue < cumulativeWeight) {
                selectedBlock = (BlockItem) pair.getFirst(); // Block chosen
                break;
            }
        }

        if (selectedBlock == null) {
            return null;
        }

        // Spend player's inventory and check if he has enough
        boolean playerHasEnough = this.spendPlayerInventory(context, selectedBlock);
        if (!playerHasEnough) {
            Player player = Objects.requireNonNull(context.getPlayer());

            player.displayClientMessage(
                    Component.translatable("item.the_shuffle_cannon.shuffle_cannon.not_enough",
                            Component.literal(selectedBlock.getName().getString()).withColor(ChatFormatting.LIGHT_PURPLE.getColor())),
                    true
            );

            player.playNotifySound(
                    SoundEvents.CRAFTER_CRAFT,
                    SoundSource.BLOCKS,
                    1.0f,
                    2f
            );

            return null;
        }

        BlockState blockState = selectedBlock.getBlock().getStateForPlacement(context);
        if (blockState == null) {
            return null;
        }

        // Check entity collisions
        blockState = this.canPlace(context, blockState) ? blockState : null;

        // Send block sound to client
        if (blockState != null) {
            Objects.requireNonNull(context.getPlayer()).playNotifySound(
                    blockState.getSoundType(context.getLevel(), context.getClickedPos(), context.getPlayer()).getPlaceSound(),
                    SoundSource.BLOCKS,
                    1.0f,
                    0.80f  // NOTE Calculated in-game to sound as normal as possible :/
            );
        }

        return blockState;
    }

    /**
     * Spends the picked block from the player's inventory.
     * @return Whether the player has the placing block in his inventory. (Always {@code true} in creative)
     */
    private boolean spendPlayerInventory(BlockPlaceContext context, Item blockItem) {
        Player player = Objects.requireNonNull(context.getPlayer());

        if (player.isCreative()) {
            return true;
        }

        // Search in main inventory
        int slotWithStack = player.getInventory().findSlotMatchingItem((new ItemStack(blockItem)));
        // Search in offhand too
        if (slotWithStack == -1 && player.getInventory().offhand.getFirst().is(blockItem)) {
            slotWithStack = Inventory.SLOT_OFFHAND;
        }

        if (slotWithStack == -1) {
            return false;
        }

        // Spend block from inventory only on server
        if (!context.getLevel().isClientSide()) {
            player.getInventory().removeItem(slotWithStack, 1);
        }
        return true;
    }

    @Override
    public void registerBlocks(@NotNull Map<Block, Item> blockToItemMap, @NotNull Item item) {
        // IMPORTANT! Empty method to avoid linking ShuffleCannonItem to AirBlock
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        ShuffleCannonDataComponent data = stack.get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data != null) {
            List<Pair<Item, Integer>> content = data.cannonContent().stream()
                    .filter(p -> p.getFirst() != Items.AIR) // Skip gaps in the inventory
                    .toList();

            if (!content.isEmpty()) {
                tooltip.add(Component.empty()); // Line break
            }

            for (Pair<Item, Integer> pair : content) {
                Item item = pair.getFirst();
                int ratio = Math.clamp(pair.getSecond(), VerticalSlider.MIN_VALUE, VerticalSlider.MAX_VALUE);

                MutableComponent tooltipText = Component.empty();
                tooltipText.append(Component.literal("◆ ").withColor(ChatFormatting.LIGHT_PURPLE.getColor()));
                tooltipText.append(Component.literal(item.getName().getString()).withColor(ChatFormatting.GRAY.getColor()));
                tooltipText.append(" ");
                tooltipText.append(Component.literal("■".repeat(ratio)).withColor(ChatFormatting.BLUE.getColor()));
                tooltipText.append(Component.literal("□".repeat(MAX_RATIO - ratio)).withColor(ChatFormatting.GRAY.getColor()));

                tooltip.add(tooltipText);
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("item.the_shuffle_cannon.shuffle_cannon");
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack shuffleCannonItem = player.getItemInHand(hand);

        if (player.isCrouching() && !level.isClientSide()) {
            player.openMenu(this, (RegistryFriendlyByteBuf extraData) -> extraData.writeJsonWithCodec(ItemStack.CODEC, shuffleCannonItem));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, Player player) {
        ItemStack shuffleCannonStack = player.getItemInHand(player.getUsedItemHand());

        return new ShuffleCannonMenu(containerId, playerInventory, shuffleCannonStack);
    }
}
