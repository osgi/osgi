/*
 * ============================================================================
 * (c) Copyright 2004 Nokia This material, including documentation and any
 * related computer programs, is protected by copyright controlled by Nokia and
 * its licensors. All rights are reserved.
 * 
 * These materials have been contributed to the Open Services Gateway Initiative
 * (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms
 * of, the OSGi Member Agreement specifically including, but not limited to, the
 * license rights and warranty disclaimers as set forth in Sections 3.2 and 12.1
 * thereof, and the applicable Statement of Work. All company, brand and product
 * names contained within this document may be trademarks that are the sole
 * property of the respective owners. The above notice must be included on all
 * copies of this document.
 * ============================================================================
 */
package com.nokia.test.megletsample2;

import java.io.*;
import java.util.Map;

import org.osgi.meglet.Meglet;
import org.osgi.service.event.*;
import org.osgi.service.log.LogService;

public class TestApplication2 extends Meglet implements EventHandler {
	String	fileName		= null;
	String	storedString	= null;

	public TestApplication2() {
		super();
	}

	public void start(Map args, InputStream stateStorage) throws Exception {
		if (args != null)
			fileName = (String) args.get("TestResult");
		if (stateStorage != null) {
			ObjectInputStream ois = new ObjectInputStream(stateStorage);
			storedString = (String) ois.readObject();
		}
		if (storedString == null)
			writeResult("START");
		else
			writeResult("RESUME:" + storedString);
	}

	public void stop(OutputStream stateStorage) throws Exception {
		if (stateStorage != null) {
			storedString = "StorageTestString";
			ObjectOutputStream ois = new ObjectOutputStream(stateStorage);
			ois.writeObject(storedString);
			writeResult("SUSPEND:" + storedString);
		}
		else
			writeResult("STOP");
	}

	public void handleEvent(Event event) {
		if (event.getTopic().equals("com/nokia/megtest/ListenerEvent"))
			writeResult("EVENT LISTENED");
		else if (event.getTopic().equals("com/nokia/megtest/EchoEvent")) {
			getEventAdmin().postEvent(
					new Event("com/nokia/megtest/EchoReplyEvent", null));
			writeResult("EVENT ECHOED");
		}
		else if (event.getTopic().equals("com/nokia/megtest/LogEvent")) {
			getLogService().log(LogService.LOG_INFO, "Test message logged!");
			writeResult("EVENT LOGGED");
		}
		else if (event.getTopic().equals("com/nokia/megtest/SubscribeEvent")) {
			String task = (String) event.getProperty("task");
			if (task.equals("subscribe"))
				registerForEvents("com/nokia/megtest/RegisteredEvent", null );
			if (task.equals("unsubscribe"))
				unregisterForEvents("com/nokia/megtest/RegisteredEvent", null );
			writeResult("EVENT SUBSCRIBED");
		}
		else if (event.getTopic().equals("com/nokia/megtest/RegisteredEvent"))
			writeResult("EVENT REGISTERED");
		else if (event.getTopic().equals("com/nokia/megtest/RequestStop")) {
			writeResult("STOP INITIATED");
			requestStop();
		}
		else if (event.getTopic().equals("com/nokia/megtest/RequestSuspend")) {
			writeResult("SUSPEND REQUESTED");
			requestSuspend();
		}
	}
	
	private void writeResult(String result) {
		try {
			if (fileName == null)
				return;
			File file = new File(fileName);
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(result.getBytes());
			stream.close();
		}
		catch (IOException e) {}
	}


	/**
	 * 
	 * @see org.osgi.service.component.ComponentInstance#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 * @see org.osgi.service.component.ComponentInstance#getInstance()
	 */
	public Object getInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}