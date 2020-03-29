# Reaper Binding

_Give some details about what this binding is meant for - a protocol, system, specific device._

_If possible, provide some resources like pictures, a YouTube video, etc. to give an impression of what can be done with this binding. You can place such resources into a `doc` folder next to this README.md._

## Supported Things

_Please describe the different supported things / devices within this section._
_Which different types are supported, which models were tested etc.?_
_Note that it is planned to generate some part of this based on the XML files within `src/main/resources/ESH-INF/thing` of your binding._

## Discovery

_Describe the available auto-discovery features here. Mention for what it works and what needs to be kept in mind when using it._

## Binding Configuration

_If your binding requires or supports general configuration settings, please create a folder `cfg` and place the configuration file `<bindingId>.cfg` inside it. In this section, you should link to this file and provide some information about the options. The file could e.g. look like:_

```
# Configuration for the Philips Hue Binding
#
# Default secret key for the pairing of the Philips Hue Bridge.
# It has to be between 10-40 (alphanumeric) characters
# This may be changed by the user for security reasons.
secret=openHABSecret
```

_Note that it is planned to generate some part of this based on the information that is available within `src/main/resources/ESH-INF/binding` of your binding._

_If your binding does not offer any generic configurations, you can remove this section completely._

## Thing Configuration

_Describe what is needed to manually configure a thing, either through the (Paper) UI or via a thing-file. This should be mainly about its mandatory and optional configuration parameters. A short example entry for a thing file can help!_

_Note that it is planned to generate some part of this based on the XML files within `src/main/resources/ESH-INF/thing` of your binding._

A manual setup through a things/reaper.things file could look like this:

```
Thing reaper:reaper:myReaper "Reaper DAW" @ "Studio" [ipAddress="192.168.1.100", httpPort=8080]
```

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within `src/main/resources/ESH-INF/thing` of your binding._

| channel | type   | description                 |
| ------- | ------ | --------------------------- |
| control | Switch | This is the control channel |

## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, \*.sitemap)._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_

Reaper binding

Control your DAW directly from openHAB.

https://github.com/ReaTeam/Doc/blob/master/web_interface_modding.md

GET/TRACK/0/SEND/0;TRANSPORT;BEATPOS;NTRACK;TRACK;GET/40364;GET/1157;

## Prerequisites

In order for this binding to work, you need to enable web interface in Reaper.

1. Go to REAPER Preferences (Ctrl + P)
1. Click on Control/OSC/web
1. Click on Add
1. Select Contorl surface mode "Web browser interface"

https://www.youtube.com/watch?v=CkMAj8CpvIU

Params:

- host (IP or hostname) / rc.reaper.fm
- port (8080)
- username (optional)
- password (optional)

interal

http://192.168.0.48:9999/_/40667;

Get playback state
playstate
0|stopped
1|playing
2|paused
5|recording
6|record paused

http://192.168.0.48:9999/_/TRANSPORT;

TRANSPORT 0 226.976667 0 81.2.84 81.2.84
isRepeatOn

## Channels:

playbackState
common#online
common#lastUpdateOnlineStatus
common#action

calculate the tempo?

BEATPOS

### Set Master Volume

http://192.168.0.48:9999/_/SET/TRACK/0/VOL/0 - -150.00db
http://192.168.0.48:9999/_/SET/TRACK/0/VOL/1 - 0.00db

### Set playback

http://192.168.0.48:9999/_/1007;

Play 1007
Play/Pause 40073
Stop 40667
Record/stop 1013
Go to previous marker 40172 (prev)
Go to next marker 40173 (next)
Rewind a little bit 40084
Fast forward a little bit 40085

Save project 40026

Metronome ON 41745
Metronome OFF 41746

### Custom action

String

http://192.168.0.48:9999/_/TRANSPORT;BEATPOS;TRACK/0;GET/40364;

Metronome state:
CMDSTATE 40364 1

### Items

```java
Dimmer Reaper_Volume "Volume [%d]" { channel="reaper:reaper:myReaper:volume" }
Player Reaper_Control "Control" { channel="reaper:reaper:myReaper:control" }
Switch Reaper_Stop "Stop" { channel="reaper:reaper:myReaper:stop" }
Switch Reaper_Metronome "Metronome" { channel="reaper:reaper:myReaper:metronome" }
String Reaper_Action "Action" { channel="reaper:reaper:myReaper:action" }
```
