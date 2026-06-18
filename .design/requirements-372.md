# Requirements for Java SPI Support in the OSGi Framework

- **Issue**: [osgi/osgi#372](https://github.com/osgi/osgi/issues/372)
- **Related**: Service Loader Mediator Specification (Chapter 133)
- **POC Implementations**:
  [Apache Felix](https://github.com/apache/felix-dev/pull/455),
  [Eclipse Equinox](https://github.com/eclipse-equinox/equinox/pull/853)

## Terminology

- **Service Loader** -
  The `java.util.ServiceLoader` API introduced in Java SE 6 for discovering and loading service provider implementations.

- **Service Type** -
  The interface or abstract class that a Service Provider implements or extends.
  Identified by its fully qualified class name.

- **Service Provider** -
  A concrete implementation of a Service Type.
  Declared in a Provider Configuration File.

- **Provider Configuration File** -
  A UTF-8 encoded text resource located at `META-INF/services/<ServiceTypeName>` that lists one or more fully qualified names of Service Provider classes, one per line.
  Lines beginning with `#` are comments.

- **Consumer Bundle** -
  A bundle that uses the `ServiceLoader` API to discover and load Service Providers.

- **Provider Bundle** -
  A bundle that contains one or more Provider Configuration Files and the corresponding Service Provider classes.

- **Wired Bundle** -
  A bundle that is connected to another bundle through the OSGi wiring model, for example via package imports/exports or Require-Bundle.

## Problem Description

Java's `ServiceLoader` API is the standard mechanism for discovering pluggable service implementations on the Java platform.
Libraries and frameworks across the Java ecosystem rely on `ServiceLoader` for extensibility, including JDBC drivers, JPA providers, XML parsers, logging backends, and many others.

In a standard Java environment, `ServiceLoader` works by scanning the class loader hierarchy for `META-INF/services/` resources and loading the declared implementation classes.
This assumes a flat or hierarchical class path where all JARs are mutually visible.

In OSGi, each bundle has its own class loader with controlled visibility.
A bundle's class loader can only find resources within the bundle itself and packages explicitly imported through the wiring model.
As a result, `ServiceLoader` calls within an OSGi bundle cannot discover Provider Configuration Files or load Service Provider classes from other bundles.
The `ServiceLoader` API is effectively broken in OSGi without additional intervention.

### Current Solution: Service Loader Mediator

The existing Service Loader Mediator Specification (Chapter 133) addresses this problem through an extender pattern.
It requires:

1. Provider bundles to declare `osgi.serviceloader` capabilities in their manifest
2. Consumer bundles to require the `osgi.serviceloader.processor` extender capability
3. A mediator bundle to be present that processes these capabilities at runtime

While functional, this approach has significant drawbacks:

- **Metadata overhead** -
  Both provider and consumer bundles require OSGi-specific manifest headers that are not present in standard Java JARs.
  This forces modification of third-party libraries.
- **Extender dependency** -
  The mediator must be deployed and active, adding a runtime dependency and a potential point of failure.
- **Adoption barrier** -
  Library authors who are unaware of OSGi or choose not to support it do not include the required capabilities.
  This is the common case for the vast majority of Java libraries.
- **Maintenance burden** -
  Wrapping or patching third-party JARs to add OSGi metadata is fragile and must be repeated for every library version update.
- **Incomplete integration** -
  Some advanced `ServiceLoader` usage patterns are difficult or impossible to support through the mediator approach.

### Impact

The inability of `ServiceLoader` to work transparently in OSGi is one of the most frequently cited pain points by OSGi adopters.
It causes friction when integrating libraries that rely on `ServiceLoader`, which includes a large and growing portion of the Java ecosystem.
The JDBC, JPA, JAX-RS, JSON-P, JSON-B, and many other Java specifications use `ServiceLoader` for implementation discovery.

## Use Cases

### UC1: JDBC Driver Discovery

A database application bundle imports `java.sql` and calls `DriverManager.getConnection()` or `ServiceLoader.load(Driver.class)`.
Multiple JDBC driver bundles are installed, each containing a `META-INF/services/java.sql.Driver` file.
The application bundle should discover all installed JDBC drivers without OSGi-specific metadata in any bundle.

### UC2: Logging Backend Discovery

A library bundle uses SLF4J, which internally calls `ServiceLoader.load(SLF4JServiceProvider.class)`.
A logging backend bundle (e.g., Logback) is installed with a standard `META-INF/services/` declaration.
SLF4J should discover the logging backend through normal `ServiceLoader` usage.

### UC3: Standard Java XML Processing

An application calls `DocumentBuilderFactory.newInstance()`, which internally uses `ServiceLoader` to discover XML parser implementations.
The framework should provide visibility to XML parser providers in other bundles that export the relevant packages.

### UC4: Mixed Environment

Some provider bundles have OSGi-specific `osgi.serviceloader` capabilities (supporting the Mediator spec) while others only have standard `META-INF/services/` files.
Both styles of provider should be discoverable.
The core SPI mechanism and the Service Loader Mediator should coexist without conflicts.

### UC5: Module Boundary Enforcement

A consumer bundle imports package `com.example.api` which contains a Service Type.
Multiple provider bundles also import `com.example.api` and contain Provider Configuration Files.
A separate bundle that does *not* import `com.example.api` should *not* have its provider configuration files discovered by the consumer.
The framework must respect wiring boundaries.

### UC6: Dynamic Bundle Lifecycle

Provider bundles may be installed, started, stopped, and uninstalled at runtime.
A subsequent `ServiceLoader.load()` call should reflect the current set of resolved and wired provider bundles.
Previously cached `ServiceLoader` instances are not required to update dynamically, but fresh calls must reflect current state.

### UC7: Fragment Bundles

A fragment bundle attached to a host bundle contains a Provider Configuration File.
The framework should treat the fragment's resources as part of the host bundle's content and include them in SPI discovery when the host is wired appropriately.

## Requirements

### R1: Transparent SPI Resource Discovery

The framework must enable `ServiceLoader` to discover Provider Configuration Files from provider bundles when the consumer bundle and the provider bundle are both wired to the same Service Type package.
This must work without requiring any OSGi-specific metadata beyond standard `Import-Package` / `Export-Package` declarations.

### R2: Cross-Bundle Class Loading for SPI Implementations

When `ServiceLoader` attempts to instantiate a Service Provider class declared in a Provider Configuration File from another bundle, the framework must enable loading of that class from the provider bundle that declared it.
This must work even though the consumer bundle does not explicitly import the package containing the Service Provider class or it is even not exported at all.

### R3: Module Boundary Enforcement

SPI discovery must respect the OSGi wiring model.
A provider bundle must only be considered for SPI discovery if it is wired to the same Service Type package as the consumer bundle.
Bundles that do not participate in the wiring for a given Service Type package must not contribute SPI resources or classes.

### R4: No Additional Metadata Requirement

Provider bundles and consumer bundles must not be required to include OSGi-specific manifest headers or capabilities to participate in framework-level SPI discovery.
Standard Java conventions (`META-INF/services/` files and `Import-Package` declarations) must be sufficient.

### R5: Compatibility with Service Loader Mediator

The framework-level SPI support must coexist with the existing Service Loader Mediator Specification (Chapter 133).
Bundles that already use `osgi.serviceloader` capabilities must continue to work correctly.
The relationship between the two mechanisms must be clearly defined to avoid duplicate or conflicting service provider discovery.

### R6: Dynamic Behavior

SPI discovery must reflect the current set of resolved and wired bundles.
When provider bundles are installed, updated, or uninstalled, subsequent SPI discovery operations must reflect these changes.

### R7: Fragment Support

Provider Configuration Files contained in fragment bundles must be discoverable as part of the host bundle's resources, consistent with existing OSGi fragment semantics.

### R8: Performance

The SPI discovery mechanism must not impose significant overhead on normal class loading or resource loading operations that are unrelated to SPI.
Implementations should employ caching or lazy evaluation to minimize repeated work.

### R9: Security

The framework must ensure that SPI discovery respects the OSGi security model.
If a security manager is present, appropriate permission checks must be performed before granting cross-bundle visibility for SPI resources and classes.

### R10: Specification Placement

This specification must be part of the OSGi Core specification as it defines behavior of the framework's module layer class and resource loading mechanisms.

## Open Questions

1. **Scope of wiring**: Should `Require-Bundle` wires be considered in addition to `Import-Package` wires for determining SPI visibility?
   The POC implementations include both.
   This broadens discovery but may be surprising if a bundle pulls in unintended providers.

2. **Stack inspection**: Should the framework verify that `ServiceLoader` is in the call stack before activating SPI aggregation?
   This would limit the mechanism to actual `ServiceLoader` usage and prevent unexpected behavior for normal `getResources("META-INF/services/...")` calls.
   The Equinox POC implements stack inspection; the Felix POC does not.

3. **Framework property to enable/disable**: Should there be a framework launch property to control whether core SPI support is enabled?
   This would allow frameworks to be deployed in strict-modularity mode where the traditional explicit-wiring-only model is preserved.

4. **Interaction with `DynamicImport-Package`**: If a consumer bundle has `DynamicImport-Package: *`, how does this affect SPI discovery scope?
   Should dynamically resolved packages extend the set of considered providers?

5. **`ServiceLoader.load(Class, ClassLoader)` variant**: The `ServiceLoader` API allows specifying a custom class loader.
   How should the framework behave when a non-bundle class loader is passed?

6. **Deprecation of Service Loader Mediator**: Should the Mediator spec (Chapter 133) be deprecated in favor of core SPI support, or should both remain as complementary mechanisms indefinitely?
