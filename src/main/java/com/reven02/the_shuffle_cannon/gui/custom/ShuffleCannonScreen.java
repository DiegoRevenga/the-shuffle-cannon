package com.reven02.the_shuffle_cannon.gui.custom;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ShuffleCannonScreen extends AbstractContainerScreen<ShuffleCannonMenu> {

    private final int HEIGHT = 184;

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TheShuffleCannon.MOD_ID,
            "textures/gui/shuffle_cannon/shuffle_cannon_gui.png"
    );

    public ShuffleCannonScreen(ShuffleCannonMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::get);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(
                RenderType::guiTextured,
                GUI_TEXTURE,
                x, y,
                0, 0,
                imageWidth, imageHeight, // GUI size
                256, 256 // Texture size (canvas)
        );
    }
}
