# Geomessage Adapter
====================

![Image of geomessage-adapter](2525C.PNG "solutions-geoevent-java")

The Geomessage Adapter provides an example of an adapter that injests and writes out messages in Esri's geomessage format.  Geomessage is an xml format used to transmit messages to esri's military features.


# Prerequisites

1. Install ArcGIS geoevent server
2. download and install geoevent server sdk (sdk included with geoevent server instalation)
3. follow sdk documentation to set up maven repository
4. Clone the solutions-geoevent-java repository


## Instructions

### General Help

* [New to Github? Get started here.](http://htmlpreview.github.com/?https://github.com/Esri/esri.github.com/blob/master/help/esri-getting-to-know-github.html)

## Getting Started


### Building just the CAP-Adapter 
see the [solutions/geoevent README.md](https://github.com/ArcGIS/solutions-geoevent-java/edit/master/README.md) for instructions on building all processors, adapters and transports
 
1. navigate to the ./solutions-geoevent-java/solutions-geoevent/adapters/geomessage-adapter directory on the command prompt
2. type mvn install at the prompt and hit return
3. Browse to ./solutions-geoevent-java/solutions-geoevent//adapters/geomessage-adapter/target (this directory is created when you execute mvn install).
4. Copy the geomessage-adapter-10.2.0.jar file and paste it into the deploy directory on your GeoEvent server (<GeoEventServer install location>\deploy\ -- default location is C:\Program Files\ArcGIS\Server\GeoEventProcessor\deploy)
 
### Validating Install
 
1. Browse to the 'Site' tab in GeoEvent Service manager
2. Click on the 'Adapters' tab and you should see both a Geomessage input adapter and Geomessage output adapter listed.

## Resources

* Learn more about Esri's [ArcGIS for the Military](http://solutions.arcgis.com/military/).
* Learn more about Esri's[ArcGIS GeoEvent Processor for Server Resource Center](http://pro.arcgis.com/share/geoevent-processor/)
* Learn more about Esri's[ArcGIS Blog](http://blogs.esri.com/esri/arcgis/)
* Learn more about [twitter@esri](http://twitter.com/esri)

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing

Copyright 2012-2013 Esri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's
[license.txt](license.txt) file.


