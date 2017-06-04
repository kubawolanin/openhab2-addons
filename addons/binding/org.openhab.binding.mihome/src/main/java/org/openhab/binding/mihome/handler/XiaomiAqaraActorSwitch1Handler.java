/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mihome.handler;

import static org.openhab.binding.mihome.XiaomiGatewayBindingConstants.CHANNEL_AQARA_CH0;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * @author Dieter Schmidt
 */
public class XiaomiAqaraActorSwitch1Handler extends XiaomiActorBaseHandler {

    private final Logger logger = LoggerFactory.getLogger(XiaomiAqaraActorSwitch1Handler.class);

    public XiaomiAqaraActorSwitch1Handler(Thing thing) {
        super(thing);
    }

    @Override
    void execute(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_AQARA_CH0)) {
            String status = command.toString().toLowerCase();
            getXiaomiBridgeHandler().writeToDevice(itemId, new String[] { "channel_0" }, new Object[] { status });
        } else {
            logger.error("Can't handle command {} on channel {}", command, channelUID);
        }
    }

    @Override
    void parseReport(JsonObject data) {
        if (data.has("channel_0")) {
            boolean isOn = data.get("channel_0").getAsString().toLowerCase().equals("on");
            updateState(CHANNEL_AQARA_CH0, isOn ? OnOffType.ON : OnOffType.OFF);
        }
    }

    @Override
    void parseHeartbeat(JsonObject data) {
        parseReport(data);
    }

    @Override
    void parseReadAck(JsonObject data) {
        parseReport(data);
    }

    @Override
    void parseWriteAck(JsonObject data) {
        parseReport(data);
    }
}
