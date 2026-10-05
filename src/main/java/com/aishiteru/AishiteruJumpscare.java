package com.aishiteru;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AishiteruJumpscare {
    private static final Logger LOGGER = LogManager.getLogger("AISHITERU");
    private static final String TEXTURE_RESOURCE_PATH = "/assets/aishiteru/textures/jumpscare.png";
    private static final String TEXTURE_NAME = "aishiteru_jumpscare";
    private static final float START_SCALE = 0.7F;
    private static final float END_SCALE = 1.35F;
    private static final float SHAKE_INTENSITY = 6.0F;
    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final Random RANDOM = new Random();

    private static Identifier textureIdentifier;
    private static int textureWidth;
    private static int textureHeight;
    private static boolean textureLoadAttempted;

    private AishiteruJumpscare() {
    }

    public static void render(MinecraftClient client, float progress, float scaledWidth, float scaledHeight) {
        Identifier texture = getTexture(client);

        if (texture == null) {
            return;
        }

        float clampedProgress = Math.max(0.0F, Math.min(1.0F, progress));
        float zoom = START_SCALE + (END_SCALE - START_SCALE) * clampedProgress;
        float coverScale = Math.max(scaledWidth / textureWidth, scaledHeight / textureHeight) * zoom;
        float drawWidth = textureWidth * coverScale;
        float drawHeight = textureHeight * coverScale;
        float shakeOffsetX = (RANDOM.nextFloat() - 0.5F) * 2.0F * SHAKE_INTENSITY;
        float shakeOffsetY = (RANDOM.nextFloat() - 0.5F) * 2.0F * SHAKE_INTENSITY;
        float drawX = (scaledWidth - drawWidth) / 2.0F + shakeOffsetX;
        float drawY = (scaledHeight - drawHeight) / 2.0F + shakeOffsetY;

        DrawableHelper.fill(0, 0, (int) Math.ceil(scaledWidth), (int) Math.ceil(scaledHeight), BACKGROUND_COLOR);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(texture);
        GlStateManager.translate(drawX, drawY, 0.0F);
        GlStateManager.scale(drawWidth / textureWidth, drawHeight / textureHeight, 1.0F);
        DrawableHelper.drawTexture(0, 0, 0.0F, 0.0F, textureWidth, textureHeight, textureWidth, textureHeight, textureWidth, textureHeight);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static Identifier getTexture(MinecraftClient client) {
        if (textureLoadAttempted) {
            return textureIdentifier;
        }

        textureLoadAttempted = true;

        try (InputStream resourceStream = AishiteruJumpscare.class.getResourceAsStream(TEXTURE_RESOURCE_PATH)) {
            if (resourceStream == null) {
                LOGGER.error("Jumpscare texture not found: " + TEXTURE_RESOURCE_PATH);
                return null;
            }

            BufferedImage image = ImageIO.read(resourceStream);

            if (image == null) {
                LOGGER.error("Jumpscare texture could not be decoded");
                return null;
            }

            textureWidth = image.getWidth();
            textureHeight = image.getHeight();
            textureIdentifier = client.getTextureManager().registerDynamicTexture(TEXTURE_NAME, new NativeImageBackedTexture(image));
        } catch (Throwable throwable) {
            LOGGER.error("Failed to load jumpscare texture", throwable);
            textureIdentifier = null;
        }

        return textureIdentifier;
    }
}
