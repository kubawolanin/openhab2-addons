/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.voice.ivona.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.core.ConfigConstants;
import org.eclipse.smarthome.core.audio.AudioException;
import org.eclipse.smarthome.core.audio.AudioFormat;
import org.eclipse.smarthome.core.audio.AudioStream;
import org.eclipse.smarthome.core.voice.TTSException;
import org.eclipse.smarthome.core.voice.TTSService;
import org.eclipse.smarthome.core.voice.Voice;
import org.openhab.voice.ivona.internal.cloudapi.CachedIvonaCloudImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a TTS service implementation for using Ivona TTS service.
 *
 * @author Kuba Wolanin
 */
public class IvonaTTSService implements TTSService {

    /** Cache folder name is below userdata/ivona/cache. */
    private static final String CACHE_FOLDER_NAME = "ivona/cache";

    // API Key, API Secret and Endpoint comes from ConfigAdmin
    private static final String CONFIG_ACCESS_KEY = "accessKey";
    private static final String CONFIG_SECRET_KEY = "secretKey";
    private static final String CONFIG_ENDPOINT = "endpoint";
    private String accessKey = null;
    private String secretKey = null;
    private String endpoint = null;

    private final Logger logger = LoggerFactory.getLogger(IvonaTTSService.class);

    /**
     * We need the cached implementation to allow for FixedLengthAudioStream.
     */
    private CachedIvonaCloudImplementation ivonaImpl;

    /**
     * Set of supported voices
     */
    private HashSet<Voice> voices;

    /**
     * Set of supported audio formats
     */
    private HashSet<AudioFormat> audioFormats;

    /**
     * DS activate, with access to ConfigAdmin
     */
    protected void activate(Map<String, Object> config) {
        try {
            modified(config);
            ivonaImpl = initVoiceImplementation();
            voices = initVoices();
            audioFormats = initAudioFormats();

            logger.info("Using Ivona cache folder {}", getCacheFolderName());
        } catch (Throwable t) {
            logger.error("Failed to activate Ivona: {}", t.getMessage(), t);
        }
    }

    protected void modified(Map<String, Object> config) {
        if (config != null) {
            this.accessKey = config.containsKey(CONFIG_ACCESS_KEY) ? config.get(CONFIG_ACCESS_KEY).toString() : null;
            this.secretKey = config.containsKey(CONFIG_SECRET_KEY) ? config.get(CONFIG_SECRET_KEY).toString() : null;

            if (config.containsKey(CONFIG_ENDPOINT)) {
                this.endpoint = "tts." + config.get(CONFIG_ENDPOINT).toString() + ".ivonacloud.com";
            } else {
                this.endpoint = null;
            }
        }
    }

    @Override
    public Set<Voice> getAvailableVoices() {
        return this.voices;
    }

    @Override
    public Set<AudioFormat> getSupportedFormats() {
        return this.audioFormats;
    }

    @Override
    public AudioStream synthesize(String text, Voice voice, AudioFormat requestedFormat) throws TTSException {
        logger.debug("Synthesize '{}' for voice '{}' in format {}", text, voice.getUID(), requestedFormat);
        // Validate known API key
        if (this.accessKey == null) {
            throw new TTSException("Missing API key, configure it first before using");
        }
        if (this.secretKey == null) {
            throw new TTSException("Missing API Secret, configure it first before using");
        }
        // Validate arguments
        // trim text
        text = text.trim();
        if ((null == text) || text.isEmpty()) {
            throw new TTSException("The passed text is null or empty");
        }
        if (!this.voices.contains(voice)) {
            throw new TTSException("The passed voice is unsupported");
        }
        boolean isAudioFormatSupported = false;
        for (AudioFormat currentAudioFormat : this.audioFormats) {
            if (currentAudioFormat.isCompatible(requestedFormat)) {
                isAudioFormatSupported = true;
                break;
            }
        }
        if (!isAudioFormatSupported) {
            throw new TTSException("The passed AudioFormat is unsupported");
        }

        // now create the input stream for given text, locale, format. There is
        // only a default voice
        try {
            File cacheAudioFile = ivonaImpl.getTextToSpeechAsFile(this.accessKey, this.secretKey, this.endpoint, text,
                    voice.getLocale().toLanguageTag(), requestedFormat.getCodec());

            if (cacheAudioFile == null) {
                throw new TTSException("Could not read from Ivona service");
            }
            AudioStream audioStream = new IvonaAudioStream(cacheAudioFile);
            return audioStream;
        } catch (AudioException ex) {
            throw new TTSException("Could not create AudioStream: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new TTSException("Could not read from Ivona service: " + ex.getMessage(), ex);
        }
    }

    /**
     * Initializes this.voices.
     *
     * @return The voices of this instance
     */
    private final HashSet<Voice> initVoices() {
        HashSet<Voice> voices = new HashSet<Voice>();
        Set<Locale> locales = ivonaImpl.getAvailableLocales();
        for (Locale local : locales) {
            Set<String> voiceLabels = ivonaImpl.getAvailableVoices(local);
            for (String voiceLabel : voiceLabels) {
                voices.add(new IvonaVoice(local, voiceLabel));
            }
        }
        return voices;
    }

    /**
     * Initializes this.audioFormats
     *
     * @return The audio formats of this instance
     */
    private final HashSet<AudioFormat> initAudioFormats() {
        HashSet<AudioFormat> audioFormats = new HashSet<AudioFormat>();
        Set<String> formats = ivonaImpl.getAvailableAudioFormats();
        for (String format : formats) {
            audioFormats.add(getAudioFormat(format));
        }
        return audioFormats;
    }

    /**
     * Up to now only MP3 supported.
     */
    private final AudioFormat getAudioFormat(String format) {
        // MP3 format
        if (AudioFormat.CODEC_MP3.equals(format)) {
            // we use by default: MP3, 44khz_16bit_mono
            Boolean bigEndian = null; // not used here
            Integer bitDepth = 16;
            Integer bitRate = null;
            Long frequency = 44000L;

            return new AudioFormat(AudioFormat.CONTAINER_NONE, AudioFormat.CODEC_MP3, bigEndian, bitDepth, bitRate,
                    frequency);
        } else {
            throw new RuntimeException("Audio format " + format + "not yet supported");
        }
    }

    private final CachedIvonaCloudImplementation initVoiceImplementation() {
        CachedIvonaCloudImplementation apiImpl = new CachedIvonaCloudImplementation(getCacheFolderName());
        return apiImpl;
    }

    String getCacheFolderName() {
        String folderName = ConfigConstants.getUserDataFolder();
        // we assume that this folder does NOT have a trailing separator
        return folderName + File.separator + CACHE_FOLDER_NAME;
    }

    @Override
    public String getId() {
        return "ivona";
    }

    @Override
    public String getLabel(Locale locale) {
        return "Ivona Text-to-Speech Engine";
    }

}
