package com.reven02.the_shuffle_wand.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShuffleWandItem extends Item {

    public ShuffleWandItem() {
        super(new Item.Settings().maxCount(1));
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
}
