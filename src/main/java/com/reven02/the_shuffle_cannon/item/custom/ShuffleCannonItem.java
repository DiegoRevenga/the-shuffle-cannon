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

public class ShuffleCannonItem extends BlockItem {

    public static final Identifier ID = Identifier.of(TheShuffleCannon.MOD_ID, "shuffle_cannon");

    private Block placingBlock = Blocks.AIR;

    public ShuffleCannonItem() {
        super(Blocks.AIR, new Item.Settings()
                .maxCount(1)
                .component(ModComponents.SHUFFLE_CANNON_DATA_COMPONENT, ShuffleCannonDataComponent.DEFAULT)
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.openHandledScreen(this.createScreenHandlerFactory(user, hand));
        return TypedActionResult.success(user.getMainHandStack());
    }

    @Override
    public Block getBlock() {
        return this.placingBlock;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        this.placingBlock = Blocks.ACACIA_STAIRS;
        ActionResult actionResult = super.useOnBlock(context);

        // Avoid spending the Shuffle Cannon item when placing blocks
        context.getStack().decrementUnlessCreative(-1, context.getPlayer());

        return actionResult;
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
