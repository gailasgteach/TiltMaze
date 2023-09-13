# TiltMaze
# Updated TiltMaze on Google Play to use Android Target SDK 33,
# so it will work with newer Android devices
# 13 September 2023

This Gluon sample was generated from https://start.gluon.io

## Basic Requirements

A list of the basic requirements can be found online in the [Gluon documentation](https://docs.gluonhq.com/#_requirements).

## Quick instructions

### Run the sample on JVM/HotSpot:

    mvn gluonfx:run

### Run the sample as a native image:

    mvn gluonfx:build gluonfx:nativerun

### Run the sample as a native android image:

    mvn -Pandroid gluonfx:build gluonfx:package gluonfx:install gluonfx:nativerun

### Run the sample as a native iOS image:

    mvn -Pios gluonfx:build gluonfx:package gluonfx:install gluonfx:nativerun

## Selected features

This is a list of all the features that were selected when creating the sample:

### JavaFX 20 Modules

 - javafx-base
 - javafx-graphics
 - javafx-controls
 - javafx-fxml

### Gluon Features

 - Glisten: build platform independent user interfaces
 - Glisten Afterburner: minimalistic dependency injection
 - Attach accelerometer
 - Attach display
 - Attach lifecycle
 - Attach statusbar
 - Attach storage
