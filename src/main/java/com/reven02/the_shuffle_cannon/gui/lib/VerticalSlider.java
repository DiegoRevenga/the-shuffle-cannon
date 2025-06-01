package com.reven02.the_shuffle_cannon.gui.lib;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

public class VerticalSlider extends AbstractWidget {
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 10;

    public static final int WIDTH = 8;
    private static final int HEIGHT = 36;

    private static final int TRACK_WIDTH = 6;
    private static final int KNOB_SIZE = WIDTH;

    private static final ResourceLocation SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath(
            TheShuffleCannon.MOD_ID,
            "textures/gui/lib/vertical_slider.png"
    );

    private static final ResourceLocation KNOB_SPRITE = ResourceLocation.fromNamespaceAndPath(
            TheShuffleCannon.MOD_ID,
            "textures/gui/lib/vertical_slider_knob.png"
    );

    private int value;

    private final ValueChangedCallback onValueChanged;

    public interface ValueChangedCallback {
        void onChanged(int newValue);
    }

    public VerticalSlider(int x, int y, int initialValue, ValueChangedCallback callback) {
        super(x, y, WIDTH, HEIGHT, Component.literal(""));
        this.value = initialValue;
        this.onValueChanged = callback;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SLIDER_SPRITE);
        RenderSystem.setShaderTexture(1, KNOB_SPRITE);

        int sliderPos = (int) ((1.0 - getValueRatio()) * (HEIGHT - 8));
        // Background bar
        guiGraphics.blit(
                RenderType::guiTextured,
                SLIDER_SPRITE,
                this.getX() + 1, this.getY(),
                1, 0,
                TRACK_WIDTH, HEIGHT,
                WIDTH, HEIGHT
        );
        // Knob
        guiGraphics.blit(
                RenderType::guiTextured,
                KNOB_SPRITE,
                this.getX(), this.getY() + sliderPos,
                0, 0,
                KNOB_SIZE, KNOB_SIZE,
                KNOB_SIZE,KNOB_SIZE
        );

        Font font = Minecraft.getInstance().font;
        String value = Integer.toString(this.value);

        int textX = (this.getX() + this.getWidth() / 2) - font.width(value) / 2;
        int textY = this.getY() + this.getHeight() + 4;

        guiGraphics.drawString(font, value, textX, textY, 0x3F3F3F, false);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        this.updateValueFromMouse(mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.updateValueFromMouse(mouseY);
        return true;
    }

    private void updateValueFromMouse(double mouseY) {
        double relativeY = mouseY - (this.getY() + (double) KNOB_SIZE / 2);
        double ratio = 1.0 - Mth.clamp(relativeY / (double) (HEIGHT - 8), 0.0, 1.0);
        int newValue = (int) Mth.lerp(ratio, MIN_VALUE, MAX_VALUE);
        if (newValue != value) {
            value = newValue;
            onValueChanged.onChanged(value);
        }
    }

    private double getValueRatio() {
        return (double) (this.value - MIN_VALUE) / (double) (MAX_VALUE - MIN_VALUE);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}
}
