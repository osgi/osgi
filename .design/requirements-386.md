# Feature Launcher

This documents looks into the requirenments for a Feature Launcher specification.

## Introduction

Compendium R8 introduced the OSGi Feature Service as a mechanism to design large OSGi applications with the ability to create reusable building blocks that consist of multiple bundles, configurations and other artifacts.

The Feature Service focuses on the definition aspect of systems. What is needed is a portable way to launch systems based on these Feature definitions. That is what this requirements document looks at.

## Terminology

* _Feature_ - A Feature contains a number of entities that define an executable system. Features are building blocks which may be assembled into larger systems.
* _Feature Descriptor_ - A JSON document containing a Feating description.
* _Feature Extension_ - A Feature can contain a number of extensions with custom content.

## Problem Description

OSGi Features provide a great way to design an OSGi system. However once you have the system that you need, there is currently no portable way to launch it. The only way to launch such a system is to transform it into a format supported by a proprietary launcher.

What is needed is a launcher that can take a JSON Feature Descriptor, as defined by the Feature Service specification, and directly turn that into a running system.

## Use Cases

### UC1: Launch an application

Harry has obtained an OSGi Feature descriptor from Linda. The Feature descriptor embodies the application Linda has built. Harry would like to use Linda's application so is looking for a tool to launch it.

### UC2: On the fly add in additional feature models

Linda has written an application that she has distributed as an OSGi Feature descriptor. Linda would like to introspect the state of the system through the Felix Web Console, for this she needs to launch her application together with a second Feature descriptor for the web console. She needs the launcher to take both Feature descriptors and turn it into a single running system.

### UC3: Overrides

Harry wants to use Linda's application, but the application opens a socket on his machine on a port that he is using for a different purpose. Harry needs to be able to launch the application as-is, but change the socket port number used.

### UC4: Clashes

Emily is building a bigger application where she needs to embed Linda's application. Emily does this by combining Linda's Feature with her own Feature descriptor, making it a component of the bigger application. However, one bundle used in Linda's application has a newer version that Emily needs. She has defined this newer version in her Feature descriptor. The launcher needs to make it possible for Emily to select the correct version of this bundle, given the fact that its declared twice in both features.

### UC5: Extensions

Bill has a Feature that contains a custom extension to initialize the database for his app. Bill wants the launcher to handle this extension with a custom handler before the feature is launched, so that the database has the required structure when the application accesses it.

## Requirements

* FL-0010: The solution MUST make it possible to create a running system from a JSON Feature descriptor as defined by the Feature Service specification.
* FL-0020: The solution MUST be able to instantiate an OSGi framework for this.
* FL-0030: The solution MAY support working with an existing, already running, OSGi framework to run the Feature.
* FL-0040: The solution MUST provide a mechanism to plug in extension handlers which are executed before the Feature is launched.
* FL-0050: The solution MUST be able to take multiple Feature descriptors to combine these into a single running system.
* FL-0060: The solution MUST provide a mechanism to pass in values for Feature descriptor variables.
* FL-0070: The solution MUST provide a predictable mechanism to merge multiple Features, taking into account that bundles, configurations and custom extensions each have different requirements on how they are merged.
* FL-0080: The solution MUST provide a mechanism to resolve merge conflicts.
* FL-0090: The solution MUST provide a well defined mechanism for handling failures when merging features or launching them.
