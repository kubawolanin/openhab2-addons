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

import static org.openhab.binding.reaper.internal.ReaperBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ReaperHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Kuba Wolanin - Initial contribution
 */
@NonNullByDefault
public class ReaperHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(ReaperHandler.class);

    private @Nullable ReaperConfiguration config;

    public ReaperHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        switch (channelUID.getIdWithoutGroup()) {
            case CHANNEL_VOLUME:
                if (command instanceof PercentType) {
                    connection.setVolume(((PercentType) command).intValue());
                } else if (command.equals(IncreaseDecreaseType.INCREASE)) {
                    connection.increaseVolume();
                } else if (command.equals(IncreaseDecreaseType.DECREASE)) {
                    connection.decreaseVolume();
                } else if (command.equals(OnOffType.OFF)) {
                    connection.setVolume(0);
                } else if (command.equals(OnOffType.ON)) {
                    connection.setVolume(100);
                } else if (RefreshType.REFRESH == command) {
                    connection.updateVolume();
                }
                break;
            case CHANNEL_CONTROL:
                if (command instanceof PlayPauseType) {
                    if (command.equals(PlayPauseType.PLAY)) {
                        connection.playerPlayPause();
                    } else if (command.equals(PlayPauseType.PAUSE)) {
                        connection.playerPlayPause();
                    }
                } else if (command instanceof NextPreviousType) {
                    if (command.equals(NextPreviousType.NEXT)) {
                        connection.playerNext();
                    } else if (command.equals(NextPreviousType.PREVIOUS)) {
                        connection.playerPrevious();
                    }
                } else if (command instanceof RewindFastforwardType) {
                    if (command.equals(RewindFastforwardType.REWIND)) {
                        connection.playerRewind();
                    } else if (command.equals(RewindFastforwardType.FASTFORWARD)) {
                        connection.playerFastForward();
                    }
                } else if (RefreshType.REFRESH == command) {
                    connection.updatePlayerStatus();
                }
                break;
            case CHANNEL_STOP:
                if (command.equals(OnOffType.ON)) {
                    stop();
                } else if (RefreshType.REFRESH == command) {
                    connection.updatePlayerStatus();
                }
                break;
            case CHANNEL_INPUTACTION:
                if (command instanceof StringType) {
                    connection.inputAction(command.toString());
                    updateState(CHANNEL_INPUTACTION, UnDefType.UNDEF);
                } else if (RefreshType.REFRESH == command) {
                    updateState(CHANNEL_INPUTACTION, UnDefType.UNDEF);
                }
                break;

        }
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        // logger.debug("Start initializing!");
        config = getConfigAs(ReaperConfiguration.class);

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before
        // leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already
        // be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running
        // connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the
        // real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task
        // decide for the real status.
        // the framework is then able to reuse the resources from the thing handler
        // initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        // http://192.168.0.48:9999/_/TRANSPORT;BEATPOS;TRACK/0;GET/40364;
        String response = "TRANSPORT\t0\t78.852902\t0\t29.1.00\t29.1.00\nBEATPOS\t0\t78.852902000019583\t112.000000000000000\t28\t0.000000000010000\t4\t4\n";

        // TRANSPORT 0 78.852902 0 29.1.00 29.1.00
        // BEATPOS 0 78.852902000019583 112.000000000000000 28 0.000000000010000 4 4
        // TRACK 0 MASTER 512 0.374387 0.000000 -1500 -1500 1.000000 0 0 0 1 0
        // CMDSTATE 40364 1

        String[] lines = response.split("\\n");
        String transportLine = lines[0];
        String beatposLine = lines[1];
        String trackLine = lines[2];
        String cmdstateLine = lines[3];

        String[] transport = transportLine.split("\\t");
        int playState = Integer.parseInt(transport[1]);
        String isRepeatOn = transport[3];

        String[] track = trackLine.split("\\t");
        String masterVolume = track[4];

        String[] metronome = cmdstateLine.split("\\t");
        String isMetronomeOn = metronome[2];

        // logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details
        // for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not
        // work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }
}
