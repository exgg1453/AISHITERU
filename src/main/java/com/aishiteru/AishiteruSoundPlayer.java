package com.aishiteru;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

public final class AishiteruSoundPlayer {
    private static final Logger LOGGER = LogManager.getLogger("AISHITERU");
    private static final String SOUND_RESOURCE_PATH = "/assets/aishiteru/sounds/aishiteru.ogg";
    private static final String SOUND_IDENTIFIER = "aishiteru.ogg";
    private static final int MAXIMUM_SEARCH_DEPTH = 2;

    private AishiteruSoundPlayer() {
    }

    public static void play(MinecraftClient client) {
        try {
            URL soundUrl = AishiteruSoundPlayer.class.getResource(SOUND_RESOURCE_PATH);

            if (soundUrl == null) {
                LOGGER.error("Sound resource not found: " + SOUND_RESOURCE_PATH);
                return;
            }

            SoundSystem soundSystem = findSoundSystem(client.getSoundManager(), 0, new IdentityHashMap<Object, Boolean>());

            if (soundSystem == null) {
                LOGGER.error("Minecraft sound system is not available");
                return;
            }

            float volume = client.options.getSoundVolume(SoundCategory.MASTER);

            if (volume <= 0.0F) {
                return;
            }

            String sourceName = soundSystem.quickPlay(false, soundUrl, SOUND_IDENTIFIER, false, 0.0F, 0.0F, 0.0F, SoundSystemConfig.ATTENUATION_NONE, 0.0F);
            soundSystem.setVolume(sourceName, volume);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to play sound", throwable);
        }
    }

    private static SoundSystem findSoundSystem(Object target, int depth, Map<Object, Boolean> visitedObjects) throws IllegalAccessException {
        if (target == null || depth > MAXIMUM_SEARCH_DEPTH || visitedObjects.containsKey(target)) {
            return null;
        }

        visitedObjects.put(target, Boolean.TRUE);

        if (target instanceof SoundSystem) {
            return (SoundSystem) target;
        }

        for (Class<?> currentClass = target.getClass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive() || field.getType().isArray()) {
                    continue;
                }

                field.setAccessible(true);
                Object fieldValue = field.get(target);

                if (fieldValue instanceof SoundSystem) {
                    return (SoundSystem) fieldValue;
                }

                if (fieldValue != null && fieldValue.getClass().getName().startsWith("net.minecraft.")) {
                    SoundSystem nestedSoundSystem = findSoundSystem(fieldValue, depth + 1, visitedObjects);

                    if (nestedSoundSystem != null) {
                        return nestedSoundSystem;
                    }
                }
            }
        }

        return null;
    }
}
