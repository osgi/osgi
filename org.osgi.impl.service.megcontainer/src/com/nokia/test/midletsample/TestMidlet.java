package com.nokia.test.midletsample;

import java.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.osgi.service.log.LogService;
import org.osgi.application.*;
import org.osgi.service.event.*;
import org.osgi.framework.*;
import java.util.*;

public class TestMidlet extends MIDlet implements EventHandler, SynchronousBundleListener,
                                                  ServiceListener, FrameworkListener {
	String                      fileName;
	String                      storedString;
	boolean                     paused;
	static long		              staticFieldChecker	= 0L;
	private LogService	        logService;
	private long		            myStaticFieldChecker;
	private ApplicationContext  myApplicationContext;
	

	public TestMidlet() {
		fileName = null;
		storedString = null;
		paused = false;
		myStaticFieldChecker = 0L;
	}

	public void startApp() throws MIDletStateChangeException {
		staticFieldChecker = myStaticFieldChecker = System.currentTimeMillis();

		myApplicationContext = org.osgi.application.Framework.getApplicationContext( this );
		if( myApplicationContext == null )
			System.err.println( "OAT didn't create the ApplicationContext!" );
		else
			myApplicationContext.registerService( EventHandler.class.getName(), this, null );

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
		if (event.getTopic().equals("com/nokia/megtest/CheckRegistered")) {
			writeResult("REGISTERED SUCCESSFULLY");
		}
		else if (event.getTopic().equals("com/nokia/megtest/LocateService")) {
			LogService logger = (LogService)myApplicationContext.locateService( "log" );
			logger.log( LogService.LOG_INFO, "Service works fine!" );
			writeResult("LOG SERVICE OPERABLE");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/AddBundleListener")) {
			myApplicationContext.addBundleListener( this );
			writeResult("BUNDLE LISTENER ADDED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/RemoveBundleListener")) {
			myApplicationContext.removeBundleListener( this );
			writeResult("BUNDLE LISTENER REMOVED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/AddServiceListener")) {
			myApplicationContext.addServiceListener( this );
			writeResult("SERVICE LISTENER ADDED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/RemoveServiceListener")) {
			myApplicationContext.removeServiceListener( this );
			writeResult("SERVICE LISTENER REMOVED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/AddFrameworkListener")) {
			myApplicationContext.addFrameworkListener( this );
			writeResult("FRAMEWORK LISTENER ADDED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/RemoveFrameworkListener")) {
			myApplicationContext.removeFrameworkListener( this );
			writeResult("FRAMEWORK LISTENER REMOVED");			
		}
		else if (event.getTopic().equals("com/nokia/megtest/CheckStartupParams")) {
			Map startupArgs = myApplicationContext.getStartupParameters();
			
			Iterator iterator = startupArgs.keySet().iterator();
			while( iterator.hasNext() ) {
				String key = (String)iterator.next(); 
				if( !startupArgs.get( key ).equals( getAppProperty(key) ) ) {
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
	}

	public void bundleChanged(BundleEvent event) {
		writeResult("BUNDLE CHANGE RECEIVED");			
	}

	public void serviceChanged(ServiceEvent event) {
		writeResult("SERVICE CHANGE RECEIVED");			
	}

	public void frameworkEvent(FrameworkEvent event) {
		// TODO Auto-generated method stub		
	}
}