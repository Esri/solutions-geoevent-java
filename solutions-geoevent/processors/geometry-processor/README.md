# geometry-processor

The geometry processor demonstrates how to extend GEP Server to build processors that perform custom actions and processing on geometries of a geoevent service.  

![Image of geomessage-adapter](Buffer.PNG)

## Features 

Multiple processors are included in the geometry-processor:

* Buffer Processor: converts the geoevent geometry to a buffer around the incoming event
* Ellipse Processor: converts the geoevent geometry to an ellipse centered on the incoming event geometry
* Range Fan Processor: converts the geoevent geometry to a rangefan with an origin at the incoming event geometry
* Visibility Processor: generate a viewshed with an observer located at the incoming geoevent geometry.  The visibility processor also allows the user to clip the viewshed to a polygon.
* Query Report Processor: executes a spatial query on feature services registered in the geoevent server's datastore.  The author of the service then configures a report based on returned features.

## Sections

* [Requirements](#requirements)
* [Building](#building)
* [Installation](#installation)
* [Testing](#testing)
* [Licensing](#licensing)

## Requirements

* See common [solutions-geoevent-java requirements](../../../README.md#requirements)
* The ArcGIS Runtime for Java SDK is required in order to run the standalone Maven Tests included with this project

## Building 

* See the [solutions-geoevent-java instructions](../../../README.md#instructions) for general instructions on 
    * verifying your Maven installation
    * setting the location of the GEP Server and GEP SDK repositories
    * and any other common required steps
 * Open a command prompt and navigate to `solutions-geoevent-java/solutions-geoevent/processors/geometry-processor`
 * * Enter `mvn install` at the prompt

## Installation

* Install the processors
    * Browse to `solutions-geoevent-java/solutions-processors/geometry-processor/target` (this directory is created when you execute mvn install).
    * Copy the jar file and paste it into the deploy directory on your GeoEvent server (<GEP install location>\deploy\ -- default location is C:\Program Files\ArcGIS\Server\GeoEventProcessor\deploy)
* Check the existing geoevent definitions
    *  Open the GeoEvent Processor Manager web application.
    *  Navigate to ‘Site’ > ‘GeoEvent Processor’ > ‘GeoEvent Definitions’ 
    *  Confirm that there exists a GEP geoevent definitions for the processor you want to run
        *  e.g. if you are going to run the buffer processor, confirm that a definition is available as shown

![Image of geoeventdefinition](doc/geoeventdefinition.png)

* If the required definitions are not available, do the following to install these definitions
    *  Navigate to ‘Site’ > ‘GeoEvent Processor’ > ‘Configuration Store’ and click ‘Import Configuration’
    *  Browse to `solutions-geoevent-java\data\configurations` and locate the `GeoEventDefinitions-GeometryProcessors.xml` configuration file. This file is located [here](../../../data/configurations/GeoEventDefinitions-GeometryProcessors.xml).
    *  On the GeoEvent Processor’s Import Configuration panel, click Import.

## Testing

### Validating the Installation
 
* See the [solutions-geoevent-java validation instructions](../../../README.md#validating-install).
    * Ensure that the Buffer, Ellipse, Rangefan, Visibility, Query-Report, etc. processors are present

### Testing with Simulated Test Data

* In the following steps you will configure GEP to receive and process simulated  data
* The following example configures the Ran Fan Processor, the other processors are configured in a similar manner

* Open the GEP Manager web application
* Create a Input to receive simulated data over TCP 
    * Navigate to ‘Site’ > ‘Services’ > 'Inputs'
    * Select Add Input and configure a TCP Input as shown

![Image of create connector](doc/geometry-range-fan-input.png)

* Next Create an Output to observe received data
    * Navigate to ‘Site’ > ‘Services’ > 'Outputs'
    * Select Add Input and configure an output to `Write to a .csv file`
* Use the GEP Service Designer to
    * Create a simple service to direct the Input data to the Output using the selected processor
    * The simple service and the processor configuration are shown below

![Image of service](doc/geometry-simple-service.png)
![Image of processor](doc/geometry-range-fan-service-design.png)

* Navigate to ‘Services’ > ‘Monitor’ and observe that you have a configuration similar to the following (note: your names/outputs may differ)

![Image of monitor](doc/monitor.png)

* Run the GEP Simulator and load the simulation file at solutions-geoevent-java\data\simulation_files\Geometry-RangeFans.csv
* Observe that the values increase on the GEP monitor and the selected outputs are updated. 
* You may now test the processors with additional outputs such as published feature services

## Licensing

Copyright 2013 Esri

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
[license.txt](../../../license.txt) file.
