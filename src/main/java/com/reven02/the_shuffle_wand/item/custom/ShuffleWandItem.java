package com.reven02.the_shuffle_wand.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
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

        if (!world.isClient) {
            Block block = Blocks.AMETHYST_BLOCK;

            world.setBlockState(placingPos, block.getDefaultState());
            world.playSound(null, placingPos, block.getDefaultState().getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS);
        }

        return ActionResult.SUCCESS;
    }
}
