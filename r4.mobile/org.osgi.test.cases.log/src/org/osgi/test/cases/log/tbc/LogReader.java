package org.osgi.test.cases.log.tbc;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.log.*;

public class LogReader implements LogListener {
	/* The bundle from which the logs should come */
	Bundle				bundle;
	LogReaderService	logReaderService;
	LogService			logService;
	Vector				savedLog	= new Vector();
	boolean				listening	= false;
	String				id;
	String				stopString;
	volatile boolean	waitingToStop;

	public LogReader(LogReaderService logReaderService, LogService logService,
			Bundle bundle, String id) {
		this.logReaderService = logReaderService;
		this.logService = logService;
		this.bundle = bundle;
		this.id = id;
		stopString = "DONEx " + id + " <4711>";
	}

	public void start() {
		if (isListening()) {
			throw new RuntimeException(id + " - can't start a started log");
		}
		toggleListening();
		/*
		 * Make sure everything that is needed by the callback method is
		 * initialized before making this call
		 */
		logReaderService.addLogListener(this);
	}

	public void stop() {
		if (!isListening()) {
			throw new RuntimeException(id + " - can't stop a stopped log");
		}
		/* Log the magic stopString */
		waitingToStop = true;
		logService.log(LogService.LOG_INFO, stopString);
		/* Wait until the magic stopString has arrived */
		synchronized (this) // issue 149: MUST not be synchronized when we log
		// the stopString
		{
			while (waitingToStop) {
				try {
					this.wait();
				}
				catch (InterruptedException e) {
				}
			}
		}
		toggleListening();
		logReaderService.removeLogListener(this);
	}

	/**
	 * Saves log entries that were created by the bundle we are assigned to
	 * listen to.
	 * 
	 * Log messages containing stopmessages are never logged.
	 */
	public void logged(LogEntry entry) {
		try {
			// System.out.print(id + " Called - " +
			// entry.getBundle().getBundleId());
			if (isListening()) {
				// Check if we log or some other bundle
				if ( entry.getMessage().indexOf("4711") == -1 ) {
					System.out.println("Not for us " + entry.getMessage());
					return;
				}
				// System.out.println(" - logging");
				System.out.println("Message: " + entry.getMessage());
				if (bundle.equals(entry.getBundle())) {
					System.out.println(bundle.getBundleId() + " == "
							+ entry.getBundle().getBundleId());
					String message = entry.getMessage();
					/* Is it a stop message? */
					if (message.startsWith("DONE")) {
						/* Is it for me? */
						if (message.equals(stopString)) {
							synchronized (this) // issue 149: MUST not be
							// synchronized when we log the
							// stopString
							{
								waitingToStop = false;
								this.notify();
							}
						}
					}
					else {
						// System.out.println(id + " Will log: " + message);
						savedLog.addElement(logEntryToString(entry));
					}
				}
				else {
					System.out.println(bundle.getBundleId() + " != "
							+ entry.getBundle().getBundleId());
				}
			}
			else {
				// System.out.println(" - not logging");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a vector of Strings containing all logentries collected so far.
	 * If this method is called more than once, it will not return the same
	 * entries more than once (it throws away everything that has been read
	 * earlier).
	 */
	public Vector getLog() {
		if (isListening()) {
			/*
			 * If someone tries to read the results before the log has been
			 * stopped, the the results are unpredictable, which should not be
			 * allowed in a test case
			 */
			throw new RuntimeException(id
					+ " - stop the logging before reading the results");
		}
		Vector returnedLog = savedLog;
		savedLog = new Vector();
		return returnedLog;
	}

	/**
	 * Converts a logentry to a String. The timestamp is not included since this
	 * makes the String uncomparable with an expected outcome.
	 */
	public String logEntryToString(LogEntry entry) {
		String exceptionString = "";
		Throwable e = entry.getException();
		if (e != null) {
			// This was changed to support R3 (section 9.5) in which
			// LogEntry.getException may not return the original exception
			// exceptionString = " " + e.getClass().getName();
			exceptionString = " " + e.toString();
		}
		return levelToString(entry.getLevel()) + " " + entry.getMessage()
				+ exceptionString;
	}

	/**
	 * Converts the integer loglevel to a String representation.
	 */
	public String levelToString(int level) {
		String logLevel = null;
		switch (level) {
			case LogService.LOG_ERROR :
				logLevel = "LOG_ERROR";
				break;
			case LogService.LOG_WARNING :
				logLevel = "LOG_WARNING";
				break;
			case LogService.LOG_INFO :
				logLevel = "LOG_INFO";
				break;
			case LogService.LOG_DEBUG :
				logLevel = "LOG_DEBUG";
				break;
			default :
				logLevel = "UNKNOWN";
				break;
		}
		return logLevel;
	}

	private boolean isListening() {
		return listening;
	}

	private void toggleListening() {
		listening = !listening;
	}
}
