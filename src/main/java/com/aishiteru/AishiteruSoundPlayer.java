package com.aishiteru;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AishiteruSoundPlayer {
    private static final Logger LOGGER = LogManager.getLogger("AISHITERU");
    private static final String SOUND_RESOURCE_PATH = "/assets/aishiteru/sounds/aishiteru.wav";

    private AishiteruSoundPlayer() {
    }

    public static void play() {
        Thread playbackThread = new Thread(AishiteruSoundPlayer::playSound, "AISHITERU Sound");
        playbackThread.setDaemon(true);
        playbackThread.start();
    }

    private static void playSound() {
        try (InputStream resourceStream = AishiteruSoundPlayer.class.getResourceAsStream(SOUND_RESOURCE_PATH)) {
            if (resourceStream == null) {
                LOGGER.error("Sound resource not found: " + SOUND_RESOURCE_PATH);
                return;
            }

            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(resourceStream))) {
                Clip clip = AudioSystem.getClip();
                clip.addLineListener(lineEvent -> {
                    if (lineEvent.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.open(audioInputStream);
                clip.start();
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to play sound", exception);
        }
    }
}
