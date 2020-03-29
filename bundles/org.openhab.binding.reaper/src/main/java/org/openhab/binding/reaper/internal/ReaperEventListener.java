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

import java.util.EventListener;

/**
 * Interface which has to be implemented by a class in order to get status
 * updates from a {@link ReaperConnection}
 *
 * @author Kuba Wolanin - Initial contribution
 */
public interface ReaperEventListener extends EventListener {
    public enum ReaperState {
        PLAY, PAUSE, END, STOP, REWIND, FASTFORWARD
    }

    public enum ReaperPlaylistState {
        ADD, ADDED, INSERT, REMOVE, REMOVED, CLEAR
    }

    void updateConnectionState(boolean connected);

    void updateVolume(int volume);

    void updatePlayerState(ReaperState state);

    void updateMuted(boolean muted);

}
