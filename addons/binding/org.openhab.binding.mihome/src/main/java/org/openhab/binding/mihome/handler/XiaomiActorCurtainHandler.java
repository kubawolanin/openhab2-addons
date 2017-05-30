/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mihome.handler;

import static org.openhab.binding.mihome.XiaomiGatewayBindingConstants.*;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * Manage the Xiaomi Smart Curtain over the API
 *
 * @author Kuba Wolanin - initial contribution
 */
public class XiaomiActorCurtainHandler extends XiaomiActorBaseHandler {

    private final Logger logger = LoggerFactory.getLogger(XiaomiActorCurtainHandler.class);

    public XiaomiActorCurtainHandler(Thing thing) {
        super(thing);
    }

    @Override
    void execute(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_CURTAIN_CONTROL)) {
            String status = command.toString().toLowerCase();
            getXiaomiBridgeHandler().writeToDevice(itemId, new String[] { "status" }, new Object[] { status });
        } else if (channelUID.getId().equals(CHANNEL_CURTAIN_LEVEL)) {
            String status = command.toString().toLowerCase();
            getXiaomiBridgeHandler().writeToDevice(itemId, new String[] { "curtain_level" }, new Object[] { status });
        } else {
            logger.warn("Can't handle command {} on channel {}", command, channelUID);
        }
    }

    @Override
    void parseHeartbeat(JsonObject data) {
        if (data.has("curtain_level")) {
            updateState(CHANNEL_CURTAIN_LEVEL, new DecimalType(data.get("curtain_level").getAsBigDecimal()));
        }
    }

    @Override
    void parseReadAck(JsonObject data) {
        parseHeartbeat(data);
    }

    @Override
    void parseReport(JsonObject data) {
        parseHeartbeat(data);
    }

    @Override
    void parseWriteAck(JsonObject data) {
        parseHeartbeat(data);
    }
}
