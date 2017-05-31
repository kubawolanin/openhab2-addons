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
public abstract class XiaomiSensorBaseHandler extends XiaomiDeviceBaseHandler {

    private static final int VOLTAGE_MAX_MILLIVOLTS = 3100;
    private static final int VOLTAGE_MIN_MILLIVOLTS = 2700;
    private static final int BATT_LEVEL_LOW = 20;

    private final Logger logger = LoggerFactory.getLogger(XiaomiSensorBaseHandler.class);

    public XiaomiSensorBaseHandler(Thing thing) {
        super(thing);
    }

    /**
     * @param data
     */
    @Override
    void parseHeartbeat(JsonObject data) {
        if (data.get("voltage") != null) {
            Integer voltage = data.get("voltage").getAsInt();
            calculateBatteryLevelFromVoltage(voltage);
        }
        if (data.get("status") != null) {
            logger.trace(
                    "Got status {} - Apart from \"report\" all other status updates for sensors seem not right (Firmware 1.4.1.145)",
                    data.get("status"));
        }
    }

    /**
     * @param voltage
     */
    void calculateBatteryLevelFromVoltage(Integer voltage) {
        voltage = Math.min(VOLTAGE_MAX_MILLIVOLTS, voltage);
        voltage = Math.max(VOLTAGE_MIN_MILLIVOLTS, voltage);
        Integer battLevel = (int) ((float) (voltage - VOLTAGE_MIN_MILLIVOLTS)
                / (float) (VOLTAGE_MAX_MILLIVOLTS - VOLTAGE_MIN_MILLIVOLTS) * 100);
        updateState(CHANNEL_BATTERY_LEVEL, new DecimalType(battLevel));
        if (battLevel <= BATT_LEVEL_LOW) {
            updateState(CHANNEL_LOW_BATTERY, OnOffType.ON);
        } else {
            updateState(CHANNEL_LOW_BATTERY, OnOffType.OFF);
        }
    }

    @Override
    void parseReadAck(JsonObject data) {
        parseHeartbeat(data);
    }

    @Override
    void execute(ChannelUID channelUID, Command command) {
        logger.warn("Channel {} does not exist", channelUID);
    }
}
