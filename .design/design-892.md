# JDK HttpServer Whiteboard Specification Design

This document describes the design for a JDK HttpServer Whiteboard Specification. This specification aims to provide a simpler alternative to the existing Servlet Whiteboard specification by leveraging the built-in `jdk.httpserver` module (`com.sun.net.httpserver`) which has been available in the JDK since Java 21.

**Tracking Issue:** https://github.com/osgi/osgi/issues/892

## Requirements

The JDK HttpServer Whiteboard Specification addresses the following requirements:

1. **R1 - Zero External Dependencies**: The specification must only depend on APIs available in the JDK itself (`jdk.httpserver` module), eliminating the need for external servlet container dependencies.

2. **R2 - Whiteboard Pattern Support**: Services implementing `com.sun.net.httpserver.HttpHandler` must be able to be registered as OSGi services and automatically picked up by a JDK HttpServer Whiteboard implementation.

3. **R3 - Context Path Mapping**: Handlers must be mappable to specific context paths using service properties.

4. **R4 - Multiple Server Support**: The specification must support multiple HTTP server instances, allowing handlers to target specific servers.

5. **R5 - Filter Support**: The specification should support filters that can intercept and process requests before they reach handlers, similar to the `com.sun.net.httpserver.Filter` API.

6. **R6 - HTTPS Support**: The specification must support both HTTP and HTTPS endpoints.

7. **R7 - Runtime Introspection**: A runtime service should provide information about registered handlers and their status.

8. **R8 - Simplicity**: The specification should be simpler to use than the full Jakarta Servlet specification while still being powerful enough for common use cases like REST APIs, health checks, and simple web services.

9. **R9 - Dynamic Registration**: Handlers must be dynamically registered and unregistered as OSGi services come and go.

10. **R10 - Authenticator Support**: The specification should support the JDK's `com.sun.net.httpserver.Authenticator` mechanism for authentication.

## Technical Solution

### Overview

The JDK HttpServer Whiteboard Specification follows the OSGi Whiteboard Pattern, similar to the existing Servlet Whiteboard specification. Bundles register `com.sun.net.httpserver.HttpHandler` services, and a JDK HttpServer Whiteboard implementation picks these services up and registers them with an underlying `com.sun.net.httpserver.HttpServer` instance.

### Core Components

#### 1. HttpHandler Services

Any service registered with the `com.sun.net.httpserver.HttpHandler` interface can be picked up by the whiteboard. The following service properties control the handler's behavior:

| Property | Type | Description |
|----------|------|-------------|
| `osgi.http.jdk.context.path` | `String` | **Required.** The context path for the handler (e.g., "/api/users"). |
| `osgi.http.jdk.context.name` | `String` | Optional name for the context, used for reference. |
| `osgi.http.jdk.target` | `String` | Optional filter to target a specific server instance. |
| `service.ranking` | `Integer` | Standard OSGi service ranking for ordering. |

Example registration using Declarative Services:

```java
@Component(
    service = HttpHandler.class,
    property = {
        "osgi.http.jdk.context.path=/api/hello"
    }
)
public class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "Hello, World!";
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
```

#### 2. Filter Services

Services implementing `com.sun.net.httpserver.Filter` can be registered to intercept requests:

| Property | Type | Description |
|----------|------|-------------|
| `osgi.http.jdk.filter.pattern` | `String` or `String[]` | Context path patterns the filter applies to. |
| `osgi.http.jdk.filter.name` | `String` | Optional name for the filter. |
| `osgi.http.jdk.target` | `String` | Optional filter to target a specific server instance. |
| `service.ranking` | `Integer` | Determines filter ordering. |

#### 3. Authenticator Services

Services implementing `com.sun.net.httpserver.Authenticator` provide authentication:

| Property | Type | Description |
|----------|------|-------------|
| `osgi.http.jdk.authenticator.pattern` | `String` or `String[]` | Context path patterns requiring authentication. |
| `osgi.http.jdk.authenticator.realm` | `String` | The authentication realm. |
| `osgi.http.jdk.target` | `String` | Optional filter to target a specific server instance. |

#### 4. JdkHttpServerRuntime Service

The implementation must register a `JdkHttpServerRuntime` service providing runtime information:

```java
public interface JdkHttpServerRuntime {
    /**
     * Returns the runtime DTO representing the current state.
     */
    JdkHttpServerRuntimeDTO getRuntimeDTO();
}
```

#### 5. Constants

A `JdkHttpWhiteboardConstants` class defines all service property names:

```java
public final class JdkHttpWhiteboardConstants {
    
    // Handler properties
    public static final String JDK_HTTP_CONTEXT_PATH = "osgi.http.jdk.context.path";
    public static final String JDK_HTTP_CONTEXT_NAME = "osgi.http.jdk.context.name";
    
    // Filter properties
    public static final String JDK_HTTP_FILTER_PATTERN = "osgi.http.jdk.filter.pattern";
    public static final String JDK_HTTP_FILTER_NAME = "osgi.http.jdk.filter.name";
    
    // Authenticator properties
    public static final String JDK_HTTP_AUTHENTICATOR_PATTERN = "osgi.http.jdk.authenticator.pattern";
    public static final String JDK_HTTP_AUTHENTICATOR_REALM = "osgi.http.jdk.authenticator.realm";
    
    // Target property
    public static final String JDK_HTTP_WHITEBOARD_TARGET = "osgi.http.jdk.target";
    
    // Runtime properties
    public static final String JDK_HTTP_ENDPOINT = "osgi.http.jdk.endpoint";
    
    // Implementation capability
    public static final String JDK_HTTP_WHITEBOARD_IMPLEMENTATION = "osgi.http.jdk";
    public static final String JDK_HTTP_WHITEBOARD_SPECIFICATION_VERSION = "1.0";
    
    private JdkHttpWhiteboardConstants() {}
}
```

### Runtime Properties

The JDK HttpServer Whiteboard implementation registers a service with the following properties:

| Property | Type | Description |
|----------|------|-------------|
| `osgi.http.jdk.endpoint` | `String[]` | The endpoints where the server is listening (e.g., "http://localhost:8080", "https://localhost:8443"). |

### Capability and Requirement

The implementation provides the following capability:

```
osgi.implementation;
    osgi.implementation="osgi.http.jdk";
    version:Version="1.0";
    uses:="com.sun.net.httpserver,org.osgi.service.jdkhttp.runtime,org.osgi.service.jdkhttp.whiteboard"
```

Bundles requiring this specification can express the requirement:

```
osgi.implementation;
    filter:="(&(osgi.implementation=osgi.http.jdk)(version>=1.0)(!(version>=2.0)))"
```

### Error Handling

When a handler, filter, or authenticator fails to register due to invalid properties or conflicts:

1. A `FailedHandlerDTO`, `FailedFilterDTO`, or `FailedAuthenticatorDTO` is added to the runtime DTO.
2. An appropriate log message is generated.
3. The service is not registered with the HTTP server.

### Lifecycle

1. When an `HttpHandler` service is registered with valid properties, it is added to the appropriate `HttpServer` at the specified context path.
2. When the service is unregistered, the context is removed from the server.
3. If multiple handlers target the same context path, service ranking determines which one is active.

## Data Transfer Objects

The following DTOs are defined for runtime introspection:

### JdkHttpServerRuntimeDTO

```java
public class JdkHttpServerRuntimeDTO extends DTO {
    /**
     * The service id of the JdkHttpServerRuntime service.
     */
    public long serviceId;
    
    /**
     * The endpoints of the HTTP server(s).
     */
    public String[] endpoints;
    
    /**
     * The registered handlers.
     */
    public HandlerDTO[] handlers;
    
    /**
     * The registered filters.
     */
    public FilterDTO[] filters;
    
    /**
     * The registered authenticators.
     */
    public AuthenticatorDTO[] authenticators;
    
    /**
     * The handlers that failed to register.
     */
    public FailedHandlerDTO[] failedHandlers;
    
    /**
     * The filters that failed to register.
     */
    public FailedFilterDTO[] failedFilters;
    
    /**
     * The authenticators that failed to register.
     */
    public FailedAuthenticatorDTO[] failedAuthenticators;
}
```

### HandlerDTO

```java
public class HandlerDTO extends DTO {
    /**
     * The service id of the handler service.
     */
    public long serviceId;
    
    /**
     * The context path of the handler.
     */
    public String contextPath;
    
    /**
     * The name of the context, if specified.
     */
    public String contextName;
}
```

### FilterDTO

```java
public class FilterDTO extends DTO {
    /**
     * The service id of the filter service.
     */
    public long serviceId;
    
    /**
     * The patterns the filter applies to.
     */
    public String[] patterns;
    
    /**
     * The name of the filter, if specified.
     */
    public String filterName;
}
```

### AuthenticatorDTO

```java
public class AuthenticatorDTO extends DTO {
    /**
     * The service id of the authenticator service.
     */
    public long serviceId;
    
    /**
     * The patterns requiring authentication.
     */
    public String[] patterns;
    
    /**
     * The authentication realm.
     */
    public String realm;
}
```

### FailedHandlerDTO

```java
public class FailedHandlerDTO extends HandlerDTO {
    /**
     * The reason for the failure.
     */
    public int failureReason;
    
    // Failure reason constants
    public static final int FAILURE_REASON_UNKNOWN = 0;
    public static final int FAILURE_REASON_INVALID_CONTEXT_PATH = 1;
    public static final int FAILURE_REASON_SHADOWED_BY_OTHER_HANDLER = 2;
    public static final int FAILURE_REASON_EXCEPTION_ON_INIT = 3;
}
```

### FailedFilterDTO

```java
public class FailedFilterDTO extends FilterDTO {
    /**
     * The reason for the failure.
     */
    public int failureReason;
}
```

### FailedAuthenticatorDTO

```java
public class FailedAuthenticatorDTO extends AuthenticatorDTO {
    /**
     * The reason for the failure.
     */
    public int failureReason;
}
```

## Comparison with Servlet Whiteboard

| Feature | Servlet Whiteboard | JDK HttpServer Whiteboard |
|---------|-------------------|---------------------------|
| External Dependencies | Jakarta Servlet API | None (JDK built-in) |
| Complexity | High (full EE spec) | Low (simple handler model) |
| Sessions | Full session support | Manual (if needed) |
| Filters | Servlet Filters | JDK HttpServer Filters |
| Authentication | Container managed | Authenticator services |
| Request/Response | HttpServletRequest/Response | HttpExchange |
| Async Support | Full async servlet support | Executor-based |
| WebSocket | Container support | Separate handling needed |

## Open Questions

1. Should virtual threads (Java 21+) be supported as an option for the HTTP server executor?
2. Should there be integration with the OSGi Configuration Admin for server configuration (port, backlog, etc.)?
3. Should resource serving (static files) be part of this specification or a separate concern?

## Related Specifications

- [OSGi Servlet Whiteboard Specification](https://docs.osgi.org/specification/osgi.cmpn/8.1.0/service.servlet.html)
- [JDK HTTP Server API](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/module-summary.html)
