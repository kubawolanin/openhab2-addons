/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.voice.ivona.internal.cloudapi;

import com.ivonacloud.aws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.ivonacloud.services.tts.IvonaSpeechCloudClient;
import com.ivonacloud.services.tts.model.CreateSpeechRequest;
import com.ivonacloud.services.tts.model.Input;

public class IvonaGetCreateSpeechURL {
    static IvonaSpeechCloudClient speechCloud;

    private static void init() {
        speechCloud = new IvonaSpeechCloudClient(
                new ClasspathPropertiesFileCredentialsProvider("resources/IvonaCredentials.properties"));
        speechCloud.setEndpoint("https://tts.eu-west-1.ivonacloud.com");
    }

    public static void main(String[] args) throws Exception {

        init();

        CreateSpeechRequest createSpeechRequest = new CreateSpeechRequest();
        Input input = new Input();
        Voice voice = new Voice();

        voice.setName("Salli");
        input.setData("This is a sample text to be synthesized.");

        createSpeechRequest.setInput(input);
        createSpeechRequest.setVoice(voice);

        System.out.println("Requested URL: " + speechCloud.getCreateSpeechUrl(createSpeechRequest));
    }
}
