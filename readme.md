
# HomeCtrl

## Status
__master__:<br>
[![Build Status](https://travis-ci.org/atennert/de.atennert.homectrl.svg?branch=master)](https://travis-ci.org/atennert/de.atennert.homectrl)
[![Release](https://img.shields.io/github/release/atennert/de.atennert.homectrl.svg)](https://github.com/atennert/de.atennert.homectrl/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

__devel__:<br>
[![Build Status](https://travis-ci.org/atennert/de.atennert.homectrl.svg?branch=devel)](https://travis-ci.org/atennert/de.atennert.homectrl)

## Contents:

* [Introduction](#intro)
* [How to get the example working?](#make-it-work)
* [Device configuration](#device-config)
* [Event bus](#event-bus)
* [How to add control modules?](#controls)
* [How to add data processors?](#processors)
* [Communication protocols](#protocols)
* [Copyright](#copyright)

## Introduction <a id="intro"></a>

HomeCtrl is an event based home or building automation system. The name is constructed from "home"
and the label of the English keyboard control button "Ctrl".

You are currently looking at version 3 of the HomeCtrl software. This version is a Java application,
that acts as a server. It is designed as a central control unit that can communicate with devices
and user interfaces using different communication protocols with my COM library as a back-end. The
current available protocols are HTTP for reading and writing and EnOcean for reading.

The HomeCtrl software consists of 6 projects:

* *homectrl*: the core application
* *homectrl-com-if*: all the stuff for interacting with the COM library the world beyond that
* *sensors*: software "sensors" for stuff, that is computed by the server
* *dataprocessing*: software modules for sensor data preprocessing
* *controls*: software modules for controlling devices
* *server*: the application project with all configurations

Version 3 of HomeCtrl is the first, where the server can actually control the devices. In versions 2
and earlier, devices where directly controlled by the user and the server was only creating the
connection between user and devices. There is no user interface software yet, but that is planned
(besides, a well configured system doesn't need user interaction ;)). The runtime structure and
behavior of the application is defined in a device configuration file, which is written in JSON. It
contains all devices, the processors and the controls with their individual settings.


## How to get the example working? <a id="make-it-work"></a>

1. Compile the server project as runnable jar file (simply build the Gradle file in the main directory).
2. Rename example-server.properties to server.properties.
3. Change the data in server.properties to what you like (or go with the default).
4. Rename example-configuration.json to configuration.json.
5. Change the configuration in configuration.json to what your system looks like. This one might be a bit tricky. In case of doubt, check the chapter about device configuration.
6. Start the application and lean back.


## Device configuration <a id="device-config"></a>


TODO

**Light at the end of the tunnel ...**

I know that this manual JSON writing is hideous. I'm planning on a visual tool to create and edit configuration files. It will mostly be colorful clicking and dragging and dropping. Even a simulation mode would be nice.

Unfortunately, it will probably take a while to get to this point.


## Event bus <a id="event-bus"></a>

TODO


## How to add control modules? <a id="controls"></a>

TODO


## How to add data processors? <a id="processors"></a>

TODO


## Communication protocols <a id="protocols"></a>

TODO


## Release notes

TODO
