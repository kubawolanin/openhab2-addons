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

You have to add your ACCESS_KEY and SECRET_KEY to your configuration, e.g. by adding a file "ivona.cfg" to the services folder, with this entry:

```
accessKey=1234567890
secretKey=abcd1234
```