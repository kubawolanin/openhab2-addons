/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.voice.ivona.internal;

import com.ivonacloud.aws.auth.AWSCredentials;
import com.ivonacloud.aws.auth.AWSCredentialsProvider;

/**
 * Custom credential class for Ivona
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class IvonaCredentials implements AWSCredentialsProvider {

    public IvonaCredentials(String mAccessKey, String mSecretKey) {
        super();
        this.mSecretKey = mSecretKey;
        this.mAccessKey = mAccessKey;
    }

    private String mSecretKey;
    private String mAccessKey;

    @Override
    public AWSCredentials getCredentials() {
        AWSCredentials awsCredentials = new AWSCredentials() {

            @Override
            public String getAWSSecretKey() {
                return mSecretKey;
            }

            @Override
            public String getAWSAccessKeyId() {
                return mAccessKey;
            };
        };
        return awsCredentials;
    }

    @Override
    public void refresh() {

    }

}
