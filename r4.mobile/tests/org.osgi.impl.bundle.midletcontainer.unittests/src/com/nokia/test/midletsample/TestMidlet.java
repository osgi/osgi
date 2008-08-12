package com.nokia.test.midletsample;

import java.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.osgi.service.log.LogService;
import org.osgi.application.*;
import org.osgi.service.event.*;
import org.osgi.framework.*;
import java.util.*;

public class TestMidlet extends MIDlet implements EventHandler, ApplicationServiceListener {
	String                      fileName;
	String                      storedString;
	boolean                     paused;
	static long		              staticFieldChecker	= 0L;
	private LogService	        logService;
	private long		            myStaticFieldChecker;
	private ApplicationContext  myApplicationContext;
	private LogService          logger = null;	

	public TestMidlet() {
		fileName = null;
		storedString = null;
		paused = false;
		myStaticFieldChecker = 0L;
	}

	private class MyEventHandler implements EventHandler {
		public void handleEvent(Event event) {
			eventHandler( event );
		}		
	}
	
	public void startApp() throws MIDletStateChangeException {
		staticFieldChecker = myStaticFieldChecker = System.currentTimeMillis();

		myApplicationContext = org.osgi.application.Framework.getApplicationContext( this );
		if( myApplicationContext == null )
			System.err.println( "OAT didn't create the ApplicationContext!" );
		else {
			Hashtable props = new Hashtable();
			props.put( EventConstants.EVENT_TOPIC, new String [] {"*"} );
			
			myApplicationContext.registerService( EventHandler.class.getName(), 
					new MyEventHandler(), props );
		}

		if (paused) {
			writeResult("RESUME");
		}
		else {
			fileName = getAppProperty("TestResult");
			writeResult("START");
		}
	}

	public void destroyApp(boolean immediate) throws MIDletStateChangeException {
		if (myStaticFieldChecker == staticFieldChecker) {			
			writeResult("STOP");
		}
		else {
			writeResult("ERROR");
			System.err
					.println("Static field sharing in multiple midlet instances!");
		}
	}

	public void pauseApp() {
		paused = true;
		writeResult("PAUSE");
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
		catch (IOException ioexception) {}
	}

	public void handleEvent(Event event) {
		// never will be called
	}
	
	public void eventHandler(Event event) {
		if (event.getTopic().equals("com/nokia/megtest/CheckRegistered")) {
			writeResult("REGISTERED SUCCESSFULLY");
		}
		else if (event.getTopic().equals("com/nokia/megtest/LocateService")) {
			logger = (LogService)myApplicationContext.locateService( "log" );
			logger.log( LogService.LOG_INFO, "Service works fine!" );
			writeResult("LOG SERVICE OPERABLE");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/CheckProperties")) {
			Map props = myApplicationContext.getServiceProperties( logger );
			if( props == null || props.size() == 0 || props.get( Constants.SERVICE_PID ) == null ||
					props.get( Constants.SERVICE_ID ) == null || props.get( Constants.OBJECTCLASS ) == null )
				writeResult("PROPERTIES FAILURE");
			else				
			  writeResult("PROPERTIES OK");
		}
		else if (event.getTopic().equals("com/nokia/megtest/AddServiceListener")) {
			myApplicationContext.addServiceListener( this, "event" );
			writeResult("SERVICE LISTENER ADDED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/RemoveServiceListener")) {
			myApplicationContext.removeServiceListener( this );
			writeResult("SERVICE LISTENER REMOVED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/CheckStartupParams")) {
			Map startupArgs = myApplicationContext.getStartupParameters();
			
			Iterator iterator = startupArgs.keySet().iterator();
			while( iterator.hasNext() ) {
				String key = (String)iterator.next(); 
				Object value1 = getAppProperty(key);
				Object value2 = startupArgs.get( key );
				if( value1 == null && value2 == null )
					continue;
				if( !value1.equals( value2 ) ) {
					writeResult("STARTUP PARAMETER FAILURE");			
					return;
				}
			}
			writeResult("STARTUP PARAMETERS OK");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/LocateServices")) {
			Object services[] = myApplicationContext.locateServices( "log" );
			for( int k=0; k != services.length; k++ ) {
				LogService logger = (LogService)services[ k ];
				logger.log( LogService.LOG_INFO, "LocateServices works fine!" );
			}
			writeResult("LOCATE SERVICES OK");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/NotifyDestroyed")) {
			notifyDestroyed();
		}
		else if (event.getTopic().equals("com/nokia/megtest/NotifyPaused")) {
			notifyPaused();
		}
		else if (event.getTopic().equals("com/nokia/megtest/ResumeRequest")) {
			resumeRequest();
		}
		else if (event.getTopic().equals("com/nokia/megtest/RegisterMyself")) {
			try {
			  myApplicationContext.registerService( EventHandler.class.getName(), this, null );
				writeResult("REGISTER MYSELF FAILURE");			
			}catch( SecurityException e )
			{
				writeResult("REGISTER MYSELF OK");			
			}
		}
	}

	public void serviceChanged(ApplicationServiceEvent event) {
		writeResult("SERVICE CHANGE RECEIVED");			
	}
}