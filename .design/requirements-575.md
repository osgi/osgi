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

* _WebSocket_ - The communication channel based on the WebSocket protocol.
* _WebSocket Handler_ - Server-side handler of a WebSocket frame handler

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

## Use Cases

### UC1: Define and register a WebSocket handler

The code below is a WebSocket handler that we would want to register on the `/ws/work/{targetId}` endpoint.

```java
public class WebSocketHandlerExample {
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
    }
}
```

In a Jakarta environment, we would annotate this class with `@ServerEndpoint("/ws/work/{targetId}")` and register it in a Jakarta WebSocket-ready HTTP server.

In OSGi, we might want to use annotations similar to the ones in the Jarkata Servlet whiteboard specification to provide more control, *e.g.* `@WebSocketWhiteboardServletPattern("/ws/work/{targetId}")`.

## Requirements

* WS-0010: The solution MUST make it possible to register a Jakarta WebSocket handler to a specific endpoint URL containing path parameters.
* WS-0020: The solution MUST follow the Jakarta WebSocket specification regarding the handler life-cycle (one instance per connection) and events handling methods.
* WS-0030: The solution MUST support the unregistration of a WebSocket handler.
