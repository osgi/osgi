# Feature Launcher

This documents looks into the requirements for a Feature Launcher specification.

## Introduction

Compendium R8 introduced the OSGi Feature Service as a mechanism to design large OSGi applications with the ability to create reusable building blocks that consist of multiple bundles, configurations and other artifacts.

The Feature Service focuses on the definition aspect of systems. What is needed is a portable way to launch systems based on these Feature definitions. That is what this requirements document looks at.

## Terminology

* _Feature_ - A Feature contains a number of entities that define an executable system. Features are building blocks which may be assembled into larger systems.
* _Feature Descriptor_ - A JSON document containing a Feature description.
* _Feature Extension_ - A Feature can contain a number of extensions with custom content.

## Problem Description

OSGi Features provide a great way to design an OSGi system. However once you have the system that you need, there is currently no portable way to launch it. The only way to launch such a system is to transform it into a format supported by a proprietary launcher.

What is needed is a launcher that can take a JSON Feature Descriptor, as defined by the Feature Service specification, and directly turn that into a running system.

## Use Cases

### UC1: Launch an application

Harry has obtained an OSGi Feature descriptor from Linda. The Feature descriptor embodies the application Linda has built. Harry would like to use Linda's application so is looking for a tool to launch it.

### UC3: Overrides

Harry wants to use Linda's application, but the application opens a socket on his machine on a port that he is using for a different purpose. Harry needs to be able to launch the application as-is, but change the socket port number used.

### UC5: Extensions

Bill has a Feature that contains a custom extension to initialize the database for his app. Bill wants the launcher to handle this extension with a custom handler before the feature is launched, so that the database has the required structure when the application accesses it.

## Requirements

* FL-0010: The solution MUST make it possible to create a running system from a JSON Feature descriptor as defined by the Feature Service specification.
* FL-0020: The solution MUST be able to instantiate an OSGi framework for this. The solution MUST provide a way to declare which Framework and which JVM to use.
* FL-0030: The solution MUST support working with an existing, already running, OSGi framework to run the Feature.
* FL-0040: The solution MUST provide a mechanism to plug in extension handlers which are executed before the Feature is launched.
* FL-0060: The solution MUST provide a mechanism to pass in values for Feature descriptor variables.
* FL-0100: The solution MUST provide a well defined mechanism where the currently launched features can be inspected.
