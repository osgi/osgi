package org.osgi.test.cases.jakartaws.junit.handler;

import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.MessageContext;

public class InvalidHandler implements Handler<MessageContext> {

	@Override
	public void close(MessageContext arg0) {

	}

	@Override
	public boolean handleFault(MessageContext arg0) {
		return true;
	}

	@Override
	public boolean handleMessage(MessageContext arg0) {
		return true;
	}

}
