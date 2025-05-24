package com.reven02.the_shuffle_cannon.gui.lib;

import com.reven02.the_shuffle_cannon.TheShuffleCannon;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

public class VerticalSlider extends AbstractWidget {
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

    private final int minValue;
    private final int maxValue;
    private int value;
    private boolean dragging = false;

    private final ValueChangedCallback onValueChanged;

    public interface ValueChangedCallback {
        void onChanged(int newValue);
    }

    public VerticalSlider(int x, int y, int minValue, int maxValue, int initialValue, ValueChangedCallback callback) {
        super(x, y, WIDTH, HEIGHT, Component.literal(""));
        this.minValue = minValue;
        this.maxValue = maxValue;
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
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.dragging = true;
        this.updateValueFromMouse(mouseY);
        return true;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        // FIXME: Doesn't work
        if (this.dragging) {
            this.updateValueFromMouse(mouseY);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.dragging = false;
    }

    private void updateValueFromMouse(double mouseY) {
        double relativeY = mouseY - this.getY();
        double ratio = 1.0 - Mth.clamp(relativeY / (double) (HEIGHT - 8), 0.0, 1.0);
        int newValue = (int) Mth.lerp(ratio, minValue, maxValue);
        if (newValue != value) {
            value = newValue;
            onValueChanged.onChanged(value);
        }
    }

    private double getValueRatio() {
        return (double) (value - minValue) / (double) (maxValue - minValue);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}
}
