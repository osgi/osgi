# Whiteboard Specification for Jakarta™ XML Web Services

This documents looks into the requirements for a Whiteboard Specification for Jakarta™ XML Web Service specification.

## Introduction

Webservices are an important part in todays maschine-tomaschine communication and the [Jakarta™ XML Web Services](https://jakarta.ee/specifications/xml-web-services/4.0/jakarta-xml-ws-spec-4.0.html) specification offers a vendor neutral way of writing and using such services.
Managing endpoints in a dynamic way with integration to different transports can be a challenging task, this specification aims to make this task much more easier using the famous [Whiteboard Pattern](https://enroute.osgi.org/FAQ/400-patterns.html#whiteboard-pattern)
and allow integration with existing transport specifications.

## Terminology

[Handler](https://jakarta.ee/specifications/xml-web-services/4.0/apidocs/jakarta.xml.ws/jakarta/xml/ws/handler/handler) - is a portion of code responsible for filtering or inspection of messages while they flow through the webservice processing.
Endpoint Implementor - a portion of the code that can be used to register an endpoint with the API

## Preliminary work

An implementation of the proposed specification can be found here:
https://github.com/stbischof/workground/tree/main/ws

## Problem Description

While in a traditional web application the configuration is rather static and known in advance in OSGi items can come and go anytime and code should be aware of this dynamism.
Also registration of listeners must happen before an endpoint is published and only 
weak typing makes it hard to hanlde this righ manually.

As a result, the publishing of Web Services in the current state of the specification goes against the loose coupling concept usually provided by OSGi 
as it requires knowledge about the stakeholders involved, beside that it is a non trivial 
task to integrate with the OSGi Whiteboard Specification for Jakarta™ Servlet as additional 
actions and care must be taken here.

It is therefore the aim of this specification to handle this cases in a transparent manner so the user can focus 
on the actuall work, use common dependency injection techniques like Declarative Services 
to inject dependecies.

## Use Cases

The code below shows an example of a logging handler that wants to intercept incomming 
and outgoing messages, this handler is targeted to any registered Endpoint Implementor:

```java
@Component(service = Handler.class)
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		try {
			smc.getMessage().writeTo(System.out);
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		try {
			smc.getMessage().writeTo(System.err);
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void close(MessageContext messageContext) {
	}
}
```

The code below shows an simple echo webservice Endpoint Implementor, it uses a relative 
URI and is therefore targeted to the http whiteboard service, the `@WebService` / `@WebMethod` is 
a jakarta annotation:

```java
@WebService
@SOAPWhiteboardEndpoint(contextpath = "/echo")
@Component(immediate = true, service = WSEcho.class)
public class WSEcho {

	@WebMethod(operationName = "echo", action = "echo")
	public String echo(@WebParam(name = "textIn") String text) {
		return text;
	}

}
```

## Requirements

- WS-0010: The solution MUST make it possible to register an Endpoint Implementor to a specific endpoint address.
- WS-0030: The solution MUST support the unregistration of a Endpoint Implementors.
- WS-0040: The solution MUST support the registration of different Handler types with an Endpoint.
- WS-0050: The solution MUST support the unregistration of different Handler types with an Endpoint.
- WS-0060: The solution MUST follow the Jakarta™ XML Web Services regarding publishing of endpoints and registering of HandlerChains
