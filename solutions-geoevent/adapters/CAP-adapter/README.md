# CAP-Adapter
====================


![Image of geomessage-adapter](CAP_logo.JPG "solutions-geoevent-java")

The CAP adapter provides an example of how to injest Common Alert Protocol (CAP) messages as geoevents.

# Prerequisites
1. Install ArcGIS geoevent server
2. download and install geoevent server sdk (sdk included with geoevent server instalation)
3. follow sdk documentation to set up maven repository
4. Install the ArcGIS Java Runtime 10.1.1 sdk

## Instructions

### General Help

* [New to Github? Get started here.](http://htmlpreview.github.com/?https://github.com/Esri/esri.github.com/blob/master/help/esri-getting-to-know-github.html)

## Getting Started

1. The path to the repository in the module's pom.xml may need to be modified to point at Systems folder in the geoevent server install location.  In the example below the repo points to the default location of the systems folder.
 
 ```
<repository> 
  <id>GEP</id>
  <name>GeoEvent Processor embedded repository</name>
  <layout>default</layout>
  <url>file:///Program Files/ArcGIS/Server/GeoEventProcessor/System</url> 
</repository>
```

### Building just the CAP-Adapter 

See the [solutions/geoevent README.md](https://github.com/ArcGIS/solutions-geoevent-java/edit/master/README.md) for instructions on building all processors, adapters and transports
 
1. navigate to the ./solutions-geoevent-java/solutions-geoevent/adapters/CAP-adapter directory on the command prompt
2. type mvn install at the prompt and hit return
3. Browse to ./solutions-geoevent-java/solutions-geoevent/adapters/CAP-adapter/target (this directory is created when you execute mvn install).
4. Copy the CAP-Adapter-10.2.0.jar file and paste it into the deploy directory on your GeoEvent server (<GeoEventServer install location>\deploy\ -- default location is C:\Program Files\ArcGIS\Server\GeoEventProcessor\deploy)
 
### Validating Install
 
1. Browse to the 'Site' tab in GeoEvent Service manager
2. Click on the 'Adapters' tab and you should see the CAP-adapter as one of the available input adapters

## Resources

* Learn more about Esri's [ArcGIS for the Military](http://solutions.arcgis.com/military/).
* Learn more about Esri's [ArcGIS GeoEvent Processor for Server Resource Center](http://pro.arcgis.com/share/geoevent-processor/)

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
