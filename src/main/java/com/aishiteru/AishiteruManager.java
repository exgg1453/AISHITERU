package com.aishiteru;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import org.lwjgl.input.Keyboard;

public final class AishiteruManager {
    private static final String QUESTION_TEXT = "DID YOU SAY AISHITERU?";
    private static final String ANSWER_TEXT = "YES";
    private static final String PROTECTED_PLAYER_NAME = "AISHITERU";
    private static final int QUESTION_COLOR = 0xFF2020;
    private static final int ANSWER_DELAY_TICKS = 25;
    private static final int JUMPSCARE_DURATION_TICKS = 20;
    private static final int DISPLAY_DURATION_TICKS = 50;
    private static final float MAXIMUM_TEXT_SCALE = 3.0F;
    private static final float MAXIMUM_SCREEN_WIDTH_RATIO = 0.9F;

    private static boolean rightShiftWasDown;
    private static boolean answerSent;
    private static boolean jumpscareEnabled;
    private static int ticksSinceTrigger = -1;

    private AishiteruManager() {
    }

    public static void onClientTick(MinecraftClient client) {
        boolean rightShiftDown = Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        if (rightShiftDown && !rightShiftWasDown && client.player != null && client.currentScreen == null) {
            trigger(client);
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

    public static void onHudRender(MinecraftClient client, float tickDelta) {
        if (ticksSinceTrigger < 0) {
            return;
        }

        Window window = new Window(client);
        float scaledWidth = (float) window.getScaledWidth();
        float scaledHeight = (float) window.getScaledHeight();
        float jumpscareTicks = ticksSinceTrigger + tickDelta - ANSWER_DELAY_TICKS;

        if (jumpscareEnabled && jumpscareTicks >= 0.0F && jumpscareTicks < JUMPSCARE_DURATION_TICKS) {
            AishiteruJumpscare.render(client, jumpscareTicks / JUMPSCARE_DURATION_TICKS, scaledWidth, scaledHeight);
            return;
        }

        renderQuestion(client, scaledWidth, scaledHeight);
    }

    private static void renderQuestion(MinecraftClient client, float scaledWidth, float scaledHeight) {
        TextRenderer textRenderer = client.textRenderer;
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

    private static void trigger(MinecraftClient client) {
        ticksSinceTrigger = 0;
        answerSent = false;
        jumpscareEnabled = !PROTECTED_PLAYER_NAME.equalsIgnoreCase(client.player.getGameProfile().getName());
        AishiteruSoundPlayer.play(client);
    }
}
