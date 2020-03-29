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

import org.openhab.binding.reaper.internal.ReaperEventListener.ReaperState;

/**
 * The {@link ReaperPlayerState} is responsible for saving the state of a
 * player.
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class ReaperPlayerState {
    private int savedVolume;
    private ReaperState savedState;

    public int getSavedVolume() {
        return savedVolume;
    }

    public void setSavedVolume(int savedVolume) {
        this.savedVolume = savedVolume;
    }

    public ReaperState getSavedState() {
        return savedState;
    }

    public void setSavedState(ReaperState savedState) {
        this.savedState = savedState;
    }
}
