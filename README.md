# Kerio Connect Data Generator

This is school project from University of West Bohemia, class KIV/ZSWI.
Main goal of this project is to generate test dataset for Kerio Connect. Data are generated and then shiped via Microsoft's EWS protocol. Users are generated with Kerio Admin libs.

## Run
Download lastest release and then issue the command `java -jar KerioConnectDataGenerator-jar-with-dependencies.jar`

## Build
```mvn package```

## Dependencies
Some of dependencies (e.g. Kerio admin) are not in central maven repositories. Kerio java admin lib is not available yet (05/16). Please see [http://www.kerio.com/learn-community/developer-zone/details#downloads] for updates.


## Licence
WTFLPv2 see: http://www.wtfpl.net/txt/copying/
