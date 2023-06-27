# Jakarta WebSocket Whiteboard

This documents looks into the requirements for a Jakarta WebSocket whiteboard specification.

## Introduction

The OSGi Compendium R6 introduced the HTTP Whiteboard specification, upgraded in R8.1 to the Whiteboard specification for Jakarta Servlet.
It provides a light and convenient way of using servlets in a an OSGi environment through the use of the Whiteboard Pattern.

WebSocket is communication protocol designed to work over the HTTP protocol.
It enables bidirectional exchange of messages between the web client and the server.
Like an HTTP servlet, a WebSocket handler is bound to a URL.

The conversion from an HTTP connection to a WebSocket stream is done by sending a `GET` HTTP request with a set of headers requesting a connection upgrade.
The server will respond with a *101 Switching protocols* response.

The WebSocket protocol is defined by [RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455) and a Java specification is defined in Jakarta EE.
This document considers the specification will rely on [Jakarta WebSocket 2.1](https://jakarta.ee/specifications/websocket/2.1/), from Jakarta EE 10.

HTTP server implementations can provide support for WebSocket, but their configuration is complex in the context of the OSGi Whiteboard specification for Jakarta Servlet.

A Jakarta Websocket Whiteboard would ease the deployment of WebSocket handlers in an OSGi HTTP environment.
This document describes the requirements for such a specification.

## Terminology

* *WebSocket* - The communication channel based on the WebSocket protocol.
* *WebSocket frame* - Fragment of a WebSocket message as transferred on the WebSocket.
* *WebSocket message* - Data or control message transferred over the WebSocket. A message can be transmitted via one or multiple frames. A data message can be text or binary. A control message can be a ping, pong or close message.
* *WebSocket Container* - The server-side, implementation-specific, container of server WebSocket Endpoints.
* *WebSocket Session* - Description of a single client-server WebSocket connection.
* *WebSocket Endpoint* - Handler of a WebSocket session lifecycle: open, close, error. The endpoint registers frame listeners when the session is opened. Can be used either on client or server side.
* *WebSocket (Message) Handler* - Handler notified of WebSocket message reception. Can be used either on client or server side.

## Problem Description

Registering a WebSocket handler can currently be done by using a custom configuration of an HTTP servlet.
The problem is that such configuration depends on the underlying HTTP server implementation.

For example, the Eclipse Jetty server requires registering components before initializing an HTTP servlet to allow it to handle the WebSocket protocol.
This requires to write code specific to Jetty in an OSGi HTTP whiteboard environment.
There is also an issue between the Jetty WebSocket server components and the servlet context given to it by the Apache Felix HTTP Jetty implementation.

As a result, the registration of WebSocket in the current state of the specification goes against the loose coupling concept usually provided by OSGi.

What is needed is a WebSocket whiteboard specification moving specific configuration to the HTTP/WebSocket whiteboard implementation.

Reusing parts, if not all, of the Jakarta WebSocket specification will ease the development of WebSocket handlers and avoid redefining existing APIs.
It would also allow an easy reuse of existing Jakarta EE WebSocket handlers.

## Note on *Partial* vs. *Whole* messages handlers

The Jakarta WebSocket specification allows two kinds of message handlers which are described as:
* *Whole* message handlers are notified by the container on arrival of a complete message (all frames recombined).
* *Partial* message handlers are notified by the implementation when it is ready to deliver frames of a whole message.

As it is explicitly stated in the JavaDoc part of the specification, the *partial* message handler is implementation-dependent.
As a result, the OSGi WebSocket Whiteboard specification should reuse the `MessageHandler.Whole` type but ignore the `MessageHandler.Partial` one and the methods that accept it as argument.

## Use Cases

### UC1: Define and register a WebSocket server endpoint using annotations

The code below is a WebSocket handler that we would want to register on the `/ws/work/{targetId}` endpoint.

The OSGi WebSocket Whiteboard implementation is responsible to register an instance of this service as WebSocket endpoint and message handler, either directly or using a proxy endpoint class.

```java
@Component(service = WebSocketEndpointExample.class, scope = ServiceScope.PROTOTYPE)
@WebSocketWhiteboardServletPattern("/ws/work/{targetId}") // See "WebSocket pattern description"
public class WebSocketEndpointExample {
    @OnOpen
    public void onOpen(
        @PathParam("targetId") String targetId,
        Session session) {
        // WebSocket stream is opened
    }

    @OnClose
    public void onClose(
        @PathParam("targetId") String targetId,
        Session session,
        CloseReason reason) {
        // WebSocket stream is closed
    }

    @OnError
    public void onError(
        @PathParam("targetId") String targetId,
        Session session,
        Throwable error) {
        // Error handling the WebSocket stream
    }

    @OnMessage
    public void onMessage(
        @PathParam("targetId") String targetId,
        String message,
        Session session) {
        // Process incoming message
		// The "message" argument can also be of type Reader, Decoder.Text
		// or Decoder.TextStream.
		// See @OnMessage documentation for more details.
    }

	@OnMessage
    public void onMessage(
        @PathParam("targetId") String targetId,
        byte[] message,
        Session session) {
        // Process binary message
		// The "message" argument can also be of type ByteBuffer, InputStream,
		// Decoder.Binary or Decoder.BinaryStream.
		// See @OnMessage documentation for more details.
    }

    @OnMessage
    public void onPong(
        @PathParam("targetId") String targetId,
        PongMessage message,
        Session session) {
        // Process pong message
    }
}
```

#### WebSocket pattern description

In a Jakarta environment, we would annotate this class with `@ServerEndpoint("/ws/work/{targetId}")` and register it in a Jakarta WebSocket-ready HTTP server.

In OSGi, we might want to use annotations similar to the ones in the Jarkata Servlet whiteboard specification to provide more control, *e.g.* `@WebSocketWhiteboardServletPattern("/ws/work/{targetId}")`.

#### WebSocket subprotocols

The [WebSocket specification (RFC 6455)](https://datatracker.ietf.org/doc/html/rfc6455#section-1.9) indicates that clients can request, during the handshake, can give a list of subprotocols names they expect, in order of preference.
If a list of subprotocols is specified, the server must use and return the subprotocol it selected from that list.

RFC 6455 doesn't explicitly specifies what happens if the server doesn't support any of the client subprotocols.

The list of subprotocols supported by the OSGi WebSocket Whiteboard server endpoint can be given using a service property of type `string[]`.
A class annotation, *e.g.* `WebSocketWhiteboardSubprotocols`, could be used to ease the declaration of such list.

#### Service scope

The Jakarta WebSocket specification expects one instance of WebSocket handler per session.
As a result, it is expected by the OSGi WebSocket Whiteboard specification to consume services with the `PROTOTYPE` service scope, to obtain a new instance of the service per session.

Implementations using the `SINGLETON` service scope must manage thread safety and session handling by themselves.
The `BUNDLE` service scope should be considered as a `SINGLETON`.

### UC2: Define and register a WebSocket server endpoint using the `Endpoint` class

This is the equivalent of [UC1](#uc1-define-and-register-a-websocket-server-endpoint-using-annotations) but extending a class instead of using annotations.

The Jakarta WebSocket specification provides the `Endpoint` class, which represents an object that can handle WebSocket conversations, both server-side or client-side.


```java
@Component(service = Endpoint.class, scope = ServiceScope.PROTOTYPE)
@WebSocketWhiteboardServletPattern("/ws/work")
public class WebSocketExtendsEndpointExample extends Endpoint {
	public void onOpen(Session session, EndpointConfig config) {
		// **Mandatory implementation**
		// WebSocket stream is opened
	}

    public void onClose(Session session, CloseReason closeReason) {
		// *Optional implementation*
        // WebSocket stream is closed
    }

    public void onError(Session session, Throwable error) {
		// *Optional implementation*
        // Error handling
    }
}
```

The OSGi WebSocket Whiteboard implementation is responsible to register an instance of this service as WebSocket endpoint, either directly or using a proxy endpoint class.

The message handler registration is done by the endpoint in the `onOpen` method.

Like the handler of [UC1](#uc1-define-and-register-a-websocket-server-endpoint-using-annotations), it is expected that the endpoint service has a `PROTOTYPE` scope.

#### Lack of URL parameters

Extending the `Endpoint` class forbids to have extra parameters to the handling methods.
As a result, it is not possible to have access to the URL parameters defined in the servlet pattern in that use case.

### UC3: Define and register a WebSocket server message handler using an interface

The Jakarta WebSocket specification provides the `MessageHandler.Whole<T>` interface to describe a message handler.
It defines a single method `void onMessage(T message)` that will be called when the message has been fully received.

The valid types for `T` are:
* For text messages:
  * `String`
  * `Reader`
  * `Decoder.Text`
  * `Decoder.TextStream`
* For binary messages:
  * `ByteBuffer`
  * `byte[]`
  * `InputStream`
  * `Decoder.Binary`
  * `Decoder.BinaryStream`
* For pong messages:
  * `PongMessage`

This interface lacks the `Session` argument, which is required to send data back to the peer.
As a result, it might be preferred to define an OSGi specific interfaces that adds this argument and indicates the explicit kind of handled WebSocket frame.
The OSGi WebSocket Whiteboard implementation will have to give the `Session` instance it obtains in the proxy message handler it registered.

```java
public interface WebSocketMessageHandler {
	public interface Text {
		public void onMessage(Session session, String message);
	}

	public interface Binary {
		public void onMessage(Session session, ByteBuffer message);
	}

	public interface Pong {
		public void onMessage(Session session, PongMessage message);
	}
}
```

The OSGi WebSocket Whiteboard implementation is responsible to register an instance of this service using a basic WebSocket endpoint.
It is expected that the message handler service has a `PROTOTYPE` scope.

Here is an example of defining a message handler:

```java
@Component(service = WebSocketMessageHandler.Text.class, scope = ServiceScope.PROTOTYPE)
@WebSocketWhiteboardServletPattern("/ws/work")
public class WebSocketMessageHandler implements WebSocketMessageHandler.Text {
    public void onMessage(Session session, String message) {
        // Process incoming message
    }
}
```

#### Lack of URL parameters

Extending the `Endpoint` class forbids to have extra parameters to the handling methods.
As a result, it is not possible to have access to the URL parameters defined in the servlet pattern in that use case.

### UC4: WebSocket client

The Jakarta WebSocket specification defines the `ContainerProvider` Java service which allows to create new WebSocket container instances.
The `WebSocketContainer` instance then allows to connect to a WebSocket server endpoint URI, a configuration and a client class, either an annotated-based or an `Endpoint` class.

The OSGi WebSocket Whiteboard could provide the Java Service as an OSGi service, using the same `ContainerProvider` interface.

## Requirements

* WS-0010: The solution MUST make it possible to register a Jakarta WebSocket handler to a specific endpoint URL containing path parameters.
* WS-0020: The solution MUST follow the Jakarta WebSocket specification regarding the handler life-cycle (one instance per connection) and events handling methods.
* WS-0030: The solution MUST support the unregistration of a WebSocket handler.
* WS-0040: The solution MUST not provide implementation specific behavior.

## References

* [RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455)
* [Jakarta WebSocket 2.1](https://jakarta.ee/specifications/websocket/2.1/)
* [Jakarta WebSocket 2.1 Javadoc](https://jakarta.ee/specifications/websocket/2.1/apidocs/server/index.html)
* [OSGi Whiteboard Specification for Jakarta™ Servlet](https://docs.osgi.org/specification/osgi.cmpn/8.1.0/service.servlet.html)
