# Ivona Speech Cloud

## Overview

IvonaTTS is an Internet based TTS service hosted at http://www.ivona.com/ and is owned by Amazon. 
It requires an API Key to get access to this service. Development Account is free and allows to use 50.000 "units" per month.

Per Speech Cloud documentation:
```A unit is a measure of the size of a text string sent for conversion into speech. A single unit consists of 200 characters, rounded up to the nearest whole unit for any given request. 
```

For more information, see http://developer.ivona.com/en/speechcloud/introduction.html

## Getting the API Key

In order to get this add-on to work, you need to create your personal API Key and Secret. 
Sign up here:
https://www.ivona.com/us/account/speechcloud/creation/

## Configuration

You have to add your API_KEY and API_SECRET to your configuration, e.g. by adding a file "ivona.cfg" to the services folder, with this entry:

```
apiKey=1234567890
apiSecret=abcd1234
```

## Caching

The VoiceRSS extension does cache audio files from previous requests, to reduce traffic, improve performance, reduce number of requests and provide same time offline capability.

For convenience, there is a tool where the audio cache can be generated in advance, to have a prefilled cache when starting this extension. You have to copy the generated data to your userdata/voicerss/cache folder.

Synopsis of this tool:

```
Usage: java org.openhab.voice.voicerss.tool.CreateTTSCache <args>
Arguments: --api-key <key> <cache-dir> <locale> { <text> | @inputfile }
  key       the VoiceRSS API Key, e.g. "123456789"
  cache-dir is directory where the files will be stored, e.g. "voicerss-cache"
  locale    the language locale, has to be valid, e.g. "en-us", "de-de"
  text      the text to create audio file for, e.g. "Hello World"
  inputfile a name of a file, where all lines will be translatet to text, e.g. "@message.txt"

Sample: java org.openhab.voice.voicerss.tool.CreateTTSCache --api-key 1234567890 cache en-US @messages.txt
```
