package com.reven02.the_shuffle_cannon.gui.custom;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;
import com.reven02.the_shuffle_cannon.gui.lib.VerticalSlider;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ShuffleCannonScreen extends AbstractContainerScreen<ShuffleCannonMenu> {

    private final int HEIGHT = 184;

    private int x = 0;
    private int y = 0;

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TheShuffleCannon.MOD_ID,
            "textures/gui/shuffle_cannon/shuffle_cannon_gui.png"
    );

    public ShuffleCannonScreen(ShuffleCannonMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (width - imageWidth) / 2;
        this.y = (height - imageHeight) / 2;

        int shift = (ShuffleCannonMenu.SLOT_SIZE - VerticalSlider.WIDTH) / 2 - 1;

        for (int i = 0; i < ShuffleCannonMenu.INV_SIZE; i++) {
            this.addRenderableWidget(new VerticalSlider(
                    this.x + ShuffleCannonMenu.HORIZONTAL_MARGIN + shift + i * ShuffleCannonMenu.SLOT_SIZE, // x
                    this.y + ShuffleCannonMenu.CANNON_INVENTORY_Y + ShuffleCannonMenu.SLOT_SIZE + 2,        // y
                    1, 10,  // min, max
                    1,      // initial value
                    (int n) -> {
                        // TODO send values to the server
                    }
            ));
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Optional<GuiEventListener> slider = this.getChildAt(mouseX, mouseY);
        if (slider.isPresent()) {
            return slider.get().mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

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
