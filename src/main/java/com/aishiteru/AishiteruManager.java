package com.aishiteru;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import org.lwjgl.input.Keyboard;

public final class AishiteruManager {
    private static final String QUESTION_TEXT = "DID YOU SAY AISHITERU?";
    private static final String ANSWER_TEXT = "YES";
    private static final int QUESTION_COLOR = 0xFF2020;
    private static final int ANSWER_DELAY_TICKS = 25;
    private static final int DISPLAY_DURATION_TICKS = 50;
    private static final float MAXIMUM_TEXT_SCALE = 3.0F;
    private static final float MAXIMUM_SCREEN_WIDTH_RATIO = 0.9F;

    private static boolean rightShiftWasDown;
    private static boolean answerSent;
    private static int ticksSinceTrigger = -1;

    private AishiteruManager() {
    }

    public static void onClientTick(MinecraftClient client) {
        boolean rightShiftDown = Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        if (rightShiftDown && !rightShiftWasDown && client.player != null && client.currentScreen == null) {
            trigger();
        }

        rightShiftWasDown = rightShiftDown;

        if (ticksSinceTrigger < 0) {
            return;
        }

        ticksSinceTrigger++;

        if (!answerSent && ticksSinceTrigger >= ANSWER_DELAY_TICKS) {
            answerSent = true;

            if (client.player != null) {
                client.player.sendChatMessage(ANSWER_TEXT);
            }
        }

        if (ticksSinceTrigger >= DISPLAY_DURATION_TICKS) {
            ticksSinceTrigger = -1;
        }
    }

    public static void onHudRender(MinecraftClient client) {
        if (ticksSinceTrigger < 0) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        Window window = new Window(client);
        float scaledWidth = (float) window.getScaledWidth();
        float scaledHeight = (float) window.getScaledHeight();
        int textWidth = textRenderer.getStringWidth(QUESTION_TEXT);
        float textScale = Math.min(MAXIMUM_TEXT_SCALE, scaledWidth * MAXIMUM_SCREEN_WIDTH_RATIO / textWidth);

        GlStateManager.pushMatrix();
        GlStateManager.translate(scaledWidth / 2.0F, scaledHeight / 2.0F, 0.0F);
        GlStateManager.scale(textScale, textScale, 1.0F);
        GlStateManager.enableBlend();
        textRenderer.drawWithShadow(QUESTION_TEXT, -textWidth / 2.0F, -textRenderer.fontHeight / 2.0F, QUESTION_COLOR);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void trigger() {
        ticksSinceTrigger = 0;
        answerSent = false;
        AishiteruSoundPlayer.play();
    }
}
