package com.nokia.test.midletsample;

import java.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.osgi.service.log.LogService;
import org.osgi.application.*;

public class TestMidlet extends MIDlet {
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
			((LogService)myApplicationContext.locateService( "log" )).
				log( LogService.LOG_INFO, "Service loaded successfully!" );

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
}