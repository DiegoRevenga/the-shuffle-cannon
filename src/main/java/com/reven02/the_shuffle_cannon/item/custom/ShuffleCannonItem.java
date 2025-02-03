package com.reven02.the_shuffle_cannon.item.custom;

import com.mojang.datafixers.util.Pair;
import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.component.ModComponents;
import com.reven02.the_shuffle_cannon.component.ShuffleCannonDataComponent.ShuffleCannonDataComponent;
import com.reven02.the_shuffle_cannon.gui.shuffle_cannon.ShuffleCannonGUI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ShuffleCannonItem extends BlockItem {

    public static final Identifier ID = Identifier.of(TheShuffleCannon.MOD_ID, "shuffle_cannon");
    public static final RuntimeException CANNON_MISSING_ERROR = new IllegalStateException("Cannon StackReference is missing");

    private Block placingBlock = Blocks.AIR;
    private static final Random RANDOM = new Random();
    private boolean playerHasEnough = true;

    public ShuffleCannonItem() {
        super(Blocks.AIR, new Settings()
                .maxCount(1)
                .component(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT)
        );
    }

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
        pickPlacingBlock(context);

        spendPlayerInventory(context);

        if (!this.playerHasEnough) {
            return ActionResult.PASS;
        }

        ActionResult actionResult = super.useOnBlock(context);

        // Avoid spending the Shuffle Cannon item when placing blocks
        context.getStack().setCount(1);

        return actionResult;
    }

    private void pickPlacingBlock(ItemUsageContext context) {
        ShuffleCannonDataComponent data = context.getStack().get(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT);
        if (data == null) {
            throw CANNON_MISSING_ERROR;
        }

        List<Pair<Item, Integer>> content = data.cannonContent().stream()
                .filter(p -> !p.getFirst().getDefaultStack().isEmpty())  // Skip gaps in the inventory
                .toList();

        if (content.isEmpty()) {
            this.placingBlock = Blocks.AIR;
            return;
        }

        int totalWeight = content.stream().mapToInt(Pair::getSecond).sum();
        int randomValue = RANDOM.nextInt(0, totalWeight);

        int cumulativeWeight = 0;
        for (Pair<Item, Integer> pair : content) {
            cumulativeWeight += pair.getSecond();
            if (randomValue < cumulativeWeight) {
                this.placingBlock = ((BlockItem) pair.getFirst()).getBlock();
                break;
            }
        }
    }

    private void spendPlayerInventory(ItemUsageContext context) {
        PlayerEntity player = Objects.requireNonNull(context.getPlayer());

        if (player.isCreative()) {
            this.playerHasEnough = true;
            return;
        }

        // Search in main inventory
        int slotWithStack = player.getInventory().getSlotWithStack(this.placingBlock.asItem().getDefaultStack());
        // Search in offhand too
        if (slotWithStack == -1 && player.getInventory().offHand.getFirst().isOf(this.placingBlock.asItem())) {
            slotWithStack = PlayerInventory.OFF_HAND_SLOT;
        }

        if (slotWithStack != -1) {
            this.playerHasEnough = true;
            player.getInventory().removeStack(slotWithStack, 1);
        }
        else {
            this.playerHasEnough = false;
        }
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
    public Block getBlock() {
        return this.placingBlock;
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
