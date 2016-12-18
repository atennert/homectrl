
# HomeCtrl


Contents:
- Introduction
- How to get the example working?
- Device configuration
- Event bus
- How to add control modules?
- How to add data processors?
- Communication protocols
- Copyright

## Introduction

HomeCtrl is an event based home or building automation system. The name is constructed from "home"
and the label of the English keyboard control button "Ctrl".

You are currently looking at version 3 of the HomeCtrl software. This version is a Java application,
that acts as a server. It is designed as a central control unit that can communicate with devices
and user interfaces using different communication protocols with my COM library as a back-end. The
current available protocols are HTTP for reading and writing and EnOcean for reading.

The HomeCtrl software consists of 6 projects:
- homectrl: the core application
- homectrl-com-if: all the stuff for interacting with the COM library the world beyond that
- sensors: software "sensors" for stuff, that is computed by the server
- dataprocessing: software modules for sensor data preprocessing
- controls: software modules for controlling devices
- server: the application project with all configurations

Version 3 of HomeCtrl is the first, where the server can actually control the devices. In versions 2
and earlier, devices where directly controlled by the user and the server was only creating the
connection between user and devices. There is no user interface software yet, but that is planned
(besides, a well configured system doesn't need user interaction ;)). The runtime structure and
behavior of the application is defined in a device configuration file, which is written in JSON. It
contains all devices, the processors and the controls with their individual settings.


## How to get the example working?

- rename example-addresses.properties to addresses.properties
- rename example-configuration.json to configuration.json
- adjust the addresses in both files

Compile the server project as runnable Jar, start the application and have fun :)


## Device configuration
 
TODO


## Event bus

TODO


## How to add control modules?

TODO


## How to add data processors?

TODO


## Communication protocols

TODO


## Copyright

The following copyright license is effective for all resources of the HomeCtrl software, unless
there is a different copyright information in or for the resource. If a resource has it's own
copyright information, then the resources license statement replaces the license statement below,
for this particular resource.


Copyright 2016 Andreas Tennert

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.