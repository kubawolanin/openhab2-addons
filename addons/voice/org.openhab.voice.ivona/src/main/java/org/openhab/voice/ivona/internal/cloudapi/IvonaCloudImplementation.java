/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.voice.ivona.internal.cloudapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.smarthome.core.audio.AudioFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ivonacloud.aws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.ivonacloud.services.tts.IvonaSpeechCloudClient;

/**
 * This class implements the Cloud service from Ivona. For more information,
 * see API documentation at http://www.ivona.org/api/documentation.aspx.
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class IvonaCloudImplementation implements IvonaCloudAPI {

    static IvonaSpeechCloudClient speechCloud;

    private final Logger logger = LoggerFactory.getLogger(IvonaCloudImplementation.class);

    private static Set<String> supportedAudioFormats = getSupportedAudioFormats();
    private static Set<Locale> supportedLocales = getSupportedLocales();
    private static Set<String> supportedVoices = getSupportedVoices();

    /**
     * Will support only "MP3" for the moment.
     */
    private static Set<String> getSupportedAudioFormats() {
        Set<String> formats = new HashSet<String>();
        formats.add(AudioFormat.CODEC_MP3);
        return formats;
    }

    @Override
    public Set<String> getAvailableAudioFormats() {
        return supportedAudioFormats;
    }

    private static Set<Locale> getSupportedLocales() {
        Set<Locale> locales = new HashSet<Locale>();
        locales.add(Locale.forLanguageTag("pl-pl"));
        // TODO listVoices ?
        return locales;
    }

    @Override
    public Set<Locale> getAvailableLocales() {
        return supportedLocales;
    }

    private static Set<String> getSupportedVoices() {
        // one default voice for every locale
        Set<String> voices = new HashSet<String>();
        for (int i = 1; i <= getSupportedLocales().size(); i++) {
            voices.add("Ivona");
        }
        return voices;
    }

    @Override
    public Set<String> getAvailableVoices() {
        return supportedVoices;
    }

    @Override
    public Set<String> getAvailableVoices(Locale locale) {
        Set<String> voices = new HashSet<String>();
        for (Locale voiceLocale : supportedLocales) {
            if (voiceLocale.toLanguageTag().equalsIgnoreCase(locale.toLanguageTag())) {
                voices.add("Ivona");
                break;
            }
        }
        return voices;
    }

    /**
     * This method will return an input stream to an audio stream for the given
     * parameters.
     *
     * It will do that using a plain URL connection to avoid any external
     * dependencies.
     */
    @Override
    public InputStream getTextToSpeech(String accessKey, String secretKey, String endpoint, String text, String locale,
            String audioFormat) throws IOException {

        speechCloud = new IvonaSpeechCloudClient(
                new ClasspathPropertiesFileCredentialsProvider("resources/IvonaCredentials.properties"));

        speechCloud.setEndpoint("https://" + endpoint);

        String url = createURL(accessKey, secretKey, text, locale, audioFormat);
        logger.debug("Call {}", url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("POST");

        int status = connection.getResponseCode();

        if (HttpURLConnection.HTTP_OK != status) {
            logger.error("Call {} returned HTTP {}", url, status);
            throw new IOException("Could not read from service: HTTP code" + status);
        }
        if (logger.isTraceEnabled()) {
            for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                logger.trace("Response.header: {}={}", header.getKey(), header.getValue());
            }
        }
        String contentType = connection.getHeaderField("Content-Type");
        InputStream is = connection.getInputStream();
        // check if content type is text/plain, then we have an error
        if (contentType.contains("text/plain")) {
            byte[] bytes = new byte[256];
            is.read(bytes, 0, 256);
            // close before throwing an exception
            try {
                is.close();
            } catch (IOException ex) {
                // ignore
            }
            throw new IOException(
                    "Could not read audio content, service return an error: " + new String(bytes, "UTF-8"));
        } else {
            return is;
        }
    }

    // internal

    /**
     * This method will create the URL for the cloud service. The text will be
     * URI encoded as it is part of the URL.
     *
     * It is in package scope to be accessed by tests.
     */
    String createURL(String accessKey, String secretKey, String text, String locale, String audioFormat) {
        String encodedMsg;
        try {
            encodedMsg = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("UnsupportedEncodingException for UTF-8 MUST NEVER HAPPEN! Check your JVM configuration!", ex);
            // fall through and use msg un-encoded
            encodedMsg = text;
        }

        // TODO CreateSpeech generate URL
        // https://github.com/IvonaSoftware/ivona-speechcloud-sdk-java/blob/master/src/samples/IvonaSpeechCloudCreateSpeech/SampleIvonaSpeechCloudGetCreateSpeechURL.java

        return "http://api.ivona.org/?key=" + accessKey + "&hl=" + locale + "&c=" + audioFormat
                + "&f=44khz_16bit_mono&src=" + encodedMsg;
    }

}
