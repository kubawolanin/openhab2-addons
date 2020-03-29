/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.reaper.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link ReaperBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Kuba Wolanin - Initial contribution
 */
@NonNullByDefault
public class ReaperBindingConstants {

    private static final String BINDING_ID = "reaper";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_REAPER = new ThingTypeUID(BINDING_ID, "reaper");

    // List of all Channel ids

    // List of thing parameters names
    public static final String HOST_PARAMETER = "ipAddress";
    public static final String HTTP_PORT_PARAMETER = "httpPort";
    public static final String HTTP_USER_PARAMETER = "httpUser";
    public static final String HTTP_PASSWORD_PARAMETER = "httpPassword";
    public static final String REFRESH_PARAMETER = "refreshInterval";

    // List of all Channel ids
    public static final String CHANNEL_VOLUME = "volume";
    public static final String CHANNEL_CONTROL = "control";
    public static final String CHANNEL_STOP = "stop";
    public static final String CHANNEL_METRONOME = "metronome";
    public static final String CHANNEL_ACTION = "action";

}
