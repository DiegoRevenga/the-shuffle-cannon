package com.reven02.the_shuffle_cannon.item.custom;

import com.mojang.datafixers.util.Pair;
import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.gui.shuffle_cannon.ShuffleCannonGUI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ShuffleCannonItem extends BlockItem {

    public static final Identifier ID = Identifier.of(TheShuffleCannon.MOD_ID, "shuffle_cannon");
    public static final RuntimeException CANNON_MISSING_ERROR = new IllegalStateException("Cannon StackReference is missing");

    private static final Random RANDOM = new Random();

    public ShuffleCannonItem() {
        super(Blocks.AIR, new Settings()
                .maxCount(1)
                .component(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT)
        );
    }

    /**
     * Opens the Shuffle Cannon GUI if sneaking.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            user.openHandledScreen(this.createScreenHandlerFactory(user, hand));
            return TypedActionResult.success(user.getMainHandStack());
        }
        return TypedActionResult.pass(user.getMainHandStack());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = super.useOnBlock(context);

        // Avoid spending the Shuffle Cannon item when placing blocks
        context.getStack().setCount(1);

        return actionResult;
    }

    /**
     * Picks a random block from the item content
     */
    @Override
    protected @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        ShuffleCannonDataComponent data = context.getStack().get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) {
            throw CANNON_MISSING_ERROR;
        }

        List<Pair<Item, Integer>> content = data.cannonContent().stream()
                .filter(p -> !p.getFirst().getDefaultStack().isEmpty())  // Skip gaps in the inventory
                .toList();

        if (content.isEmpty()) {
            return null;
        }

        int totalWeight = content.stream().mapToInt(Pair::getSecond).sum();
        int randomValue = RANDOM.nextInt(totalWeight);

        int cumulativeWeight = 0;
        for (Pair<Item, Integer> pair : content) {
            cumulativeWeight += pair.getSecond();
            if (randomValue < cumulativeWeight) {
                BlockItem blockItem = (BlockItem) pair.getFirst();

                boolean playerHasEnough = this.spendPlayerInventory(context, blockItem);
                if (!playerHasEnough) {
                    return null;
                }

                BlockState blockState = blockItem.getBlock().getPlacementState(context);
                // Check entity collisions
                return blockState != null && this.canPlace(context, blockState) ? blockState : null;
            }
        }

        return null;
    }

    /**
     * Spends the picked block from the player's inventory.
     * @param context
     * @return Whether the player has the placing block in his inventory. (Always {@code true} in creative)
     */
    private boolean spendPlayerInventory(ItemPlacementContext context, Item blockItem) {
        PlayerEntity player = Objects.requireNonNull(context.getPlayer());

        if (player.isCreative()) {
            return true;
        }

        // Search in main inventory
        int slotWithStack = player.getInventory().getSlotWithStack(blockItem.getDefaultStack());
        // Search in offhand too
        if (slotWithStack == -1 && player.getInventory().offHand.getFirst().isOf(blockItem)) {
            slotWithStack = PlayerInventory.OFF_HAND_SLOT;
        }

        if (slotWithStack == -1) {
            return false;
        }

        // Spend block from inventory only on server
        if (!context.getWorld().isClient()) {
            player.getInventory().removeStack(slotWithStack, 1);
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        // Debug: Cannon content
        ShuffleCannonDataComponent data = stack.get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data != null) {
            for (Pair<Item, Integer> pair : data.cannonContent()) {
                Item item = pair.getFirst();
                Integer ratio = pair.getSecond();

                tooltip.add(Text.translatable("item.the_shuffle_cannon.shuffle_cannon.tooltip", item.getName(), ratio));
            }
        }
    }

    @Override
    public String getTranslationKey() {
        return "item.the_shuffle_cannon.shuffle_cannon";
    }

    @Override
    public void appendBlocks(Map<Block, Item> map, Item item) {
        // IMPORTANT! Empty method to avoid linking ShuffleCannonItem to AirBlock
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        stack.set(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT);

        return stack;
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory(PlayerEntity player, Hand hand) {
        EquipmentSlot slot = switch (hand) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
        };
        ItemStack cannonStack = player.getEquippedStack(slot);

        return new ExtendedScreenHandlerFactory<ItemStack>() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("item.the_shuffle_cannon.shuffle_cannon");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ShuffleCannonGUI(syncId, playerInventory, StackReference.of(player, slot));
            }

            @Override
            public ItemStack getScreenOpeningData(ServerPlayerEntity player) {
                return cannonStack;
            }
        };
    }
}
