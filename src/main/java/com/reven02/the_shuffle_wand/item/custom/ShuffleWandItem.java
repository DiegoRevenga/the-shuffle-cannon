package com.reven02.the_shuffle_wand.item.custom;

import com.mojang.datafixers.util.Pair;
import com.reven02.the_shuffle_wand.TheShuffleWand;
import com.reven02.the_shuffle_wand.component.ModComponents;
import com.reven02.the_shuffle_wand.component.ShuffleWandDataComponent.ShuffleWandDataComponent;
import com.reven02.the_shuffle_wand.gui.shuffle_wand.ShuffleWandGUI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ShuffleWandItem extends Item {

    public static final Identifier ID = Identifier.of(TheShuffleWand.MOD_ID, "shuffle_wand");

    public ShuffleWandItem() {
        super(new Item.Settings()
                .maxCount(1)
                .component(ModComponents.SHUFFLE_WAND_DATA_COMPONENT, ShuffleWandDataComponent.DEFAULT)
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //user.openHandledScreen(new SimpleNamedScreenHandlerFactory(this, this.getDisplayName()));
        user.openHandledScreen(createScreenHandlerFactory(user, hand));
        return TypedActionResult.success(user.getMainHandStack());
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory(PlayerEntity player, Hand hand) {
        EquipmentSlot slot = switch (hand) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
        };
        ItemStack wandStack = player.getEquippedStack(slot);

        return new ExtendedScreenHandlerFactory<ItemStack>() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("item.the_shuffle_wand.shuffle_wand");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ShuffleWandGUI(syncId, playerInventory, StackReference.of(player, slot));
            }

            @Override
            public ItemStack getScreenOpeningData(ServerPlayerEntity player) {
                return wandStack;
            }
        };
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos clickedPos = context.getBlockPos();
        BlockPos placingPos = clickedPos.offset(context.getSide());

        World world = context.getWorld();

        Block block = Blocks.AMETHYST_BLOCK;

        // Check entity collisions
        if (!world.canPlace(block.getDefaultState(), placingPos, ShapeContext.absent())) {
            return ActionResult.FAIL;
        }

        // Check is not overriding blocks
        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
        if (!itemPlacementContext.canPlace()) {
            return ActionResult.FAIL;
        }

        // Place block
        // Both in client and server to avoid de-sync
        world.setBlockState(placingPos, block.getDefaultState());

        // ***** Only-Server context *****
        if (!world.isClient) {
            world.playSound(null, placingPos, block.getDefaultState().getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        ShuffleWandDataComponent data = stack.get(ModComponents.SHUFFLE_WAND_DATA_COMPONENT);

        if (data != null) {
            for (Pair<Item, Integer> pair : data.wandContent()) {
                Item item = pair.getFirst();
                Integer ratio = pair.getSecond();

                tooltip.add(Text.translatable("item.the_shuffle_wand.shuffle_wand.content", item.getName(), ratio));
            }
        }
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        stack.set(ModComponents.SHUFFLE_WAND_DATA_COMPONENT, ShuffleWandDataComponent.DEFAULT);

        return stack;
    }
}
