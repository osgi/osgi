/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.profile;

import java.io.*;
import org.eclipse.osgi.framework.debug.DebugOptions;

public class DefaultProfileLogger implements ProfileLogger {
	private static final String DEFAULTPROFILE_PROP = "osgi.defaultprofile."; //$NON-NLS-1$
	private static final String PROP_FILENAME = DEFAULTPROFILE_PROP + "logfilename"; //$NON-NLS-1$
	private static final String PROP_LOGSYNCHRONOUSLY = DEFAULTPROFILE_PROP + "logsynchronously"; //$NON-NLS-1$
	private static final String PROP_BUFFERSIZE = DEFAULTPROFILE_PROP + "buffersize"; //$NON-NLS-1$

	private static final String DEFAULTPROFILE_OPTION = "org.eclipse.osgi/defaultprofile/"; //$NON-NLS-1$
	private static final String OPTION_FILENAME = DEFAULTPROFILE_OPTION + "logfilename"; //$NON-NLS-1$
	private static final String OPTION_LOGSYNCHRONOUSLY = DEFAULTPROFILE_OPTION + "logsynchronously"; //$NON-NLS-1$
	private static final String OPTION_BUFFERSIZE = DEFAULTPROFILE_OPTION + "buffersize"; //$NON-NLS-1$

	protected boolean logSynchronously = false;
	protected long startTime = 0;
	protected static final int DEFAULT_BUFFER_SIZE = 256;

	protected String launchTimeString = null;
	protected long launchTime = 0;
	protected TimeEntry[] timeLogEntries = null;
	protected int timeEntriesIndex = 0;
	protected StringBuffer timelog = null;

	private int bufferSize = DEFAULT_BUFFER_SIZE;
	private String logFileName = null;
	private File logFile = null;
	private StringBuffer entryReport = new StringBuffer(120);
	private StringBuffer padsb = new StringBuffer(16); // to prevent creating this over and over
	private int indent;

	DefaultProfileLogger() {
		initProps();

		int size = getBufferSize();
		timeLogEntries = new TimeEntry[size];
		timelog = new StringBuffer(4096);
		for (int i = 0; i < size; i++) {
			timeLogEntries[i] = timeEntryFactory();
		}
		timeEntriesIndex = 0;

		String timeString = System.getProperty("eclipse.startTime"); //$NON-NLS-1$
		if (timeString != null) {
			startTime = Long.parseLong(timeString);
		} else {
			startTime = System.currentTimeMillis();
		}

		int index = 0;
		long mainTime = startTime;
		launchTimeString = System.getProperty("launch.startMillis"); //$NON-NLS-1$
		if (launchTimeString != null) {
			startTime = Long.parseLong(launchTimeString);
			logTime(Profile.FLAG_NONE, "DefaultProfileLogger.init()", "launch time initialized", null); //$NON-NLS-1$//$NON-NLS-2$
			timeLogEntries[index++].time = getStartTime();
		}

		logTime(Profile.FLAG_NONE, "DefaultProfileLogger.init()", "start time initialized", null); //$NON-NLS-1$//$NON-NLS-2$
		timeLogEntries[index++].time = mainTime;
	}

	public void initProps() {
		String prop;
		DebugOptions dbgOptions = null;
		// if osgi.debug is not available, don't force DebugOptions
		//  to init as this variable may be set later on where 
		//  DebugOptions will succeed.
		if (System.getProperty("osgi.debug") != null) { //$NON-NLS-1$
			dbgOptions = DebugOptions.getDefault();
			if (dbgOptions != null) {
				logFileName = dbgOptions.getOption(OPTION_FILENAME);
				logSynchronously = dbgOptions.getBooleanOption(OPTION_LOGSYNCHRONOUSLY, false);
				int size = dbgOptions.getIntegerOption(OPTION_BUFFERSIZE, 0);
				if (size > 0)
					bufferSize = size;
			}
		}

		if ((prop = System.getProperty(PROP_FILENAME)) != null) {
			logFileName = prop;
			if (dbgOptions != null)
				dbgOptions.setOption(OPTION_FILENAME, logFileName);
		}
		if ((prop = System.getProperty(PROP_LOGSYNCHRONOUSLY)) != null) {
			logSynchronously = Boolean.valueOf(prop).booleanValue();
			if (dbgOptions != null)
				dbgOptions.setOption(OPTION_LOGSYNCHRONOUSLY, new Boolean(logSynchronously).toString());
		}
		if ((prop = System.getProperty(PROP_BUFFERSIZE)) != null) {
			try {
				int value = Integer.parseInt(prop);
				if (value > 0) {
					bufferSize = value;
					if (dbgOptions != null)
						dbgOptions.setOption(OPTION_BUFFERSIZE, Integer.toString(bufferSize));
				}
			} catch (NumberFormatException e) {
				// do nothing
			}
		}
	}

	public synchronized void logTime(int flag, String id, String msg, String description) {
		if (timeEntriesIndex == timeLogEntries.length) {
			makeLog();
			logTime(Profile.FLAG_NONE, "Profile.logTime()", "log entries rolled", null); //$NON-NLS-1$ //$NON-NLS-2$
		}

		TimeEntry entry = timeLogEntries[timeEntriesIndex++];
		entry.time = getTime();
		entry.id = id;
		entry.msg = msg;
		entry.flag = flag;
		entry.description = description;

		if (logSynchronously) {
			System.out.print(getProfileLog().substring(2));
		}
	}

	public synchronized String getProfileLog() {
		String log;
		log = getProfileLogReport();
		writeToProfileLogFile(log);
		return log;
	}

	protected long getTime() {
		return System.currentTimeMillis();
	}

	protected TimeEntry findCompareEntry(int index, String id, int flag) {
		if (index > 0)
			index--;
		int prev = index;
		if (flag != Profile.FLAG_ENTER) {
			while (index >= 0) {
				TimeEntry entry = timeLogEntries[index];
				if (entry.id.equals(id)) {
					switch (flag) {
						case Profile.FLAG_NONE :
							return entry;
						case Profile.FLAG_EXIT :
							if (entry.flag == Profile.FLAG_ENTER)
								return entry;
							break;
					}
				}
				index--;
			}
		}
		return timeLogEntries[prev];
	}

	protected String entryReport(TimeEntry entry, TimeEntry compareWith) {
		// indent level:
		entryReport.setLength(0);
		if (entry.flag == Profile.FLAG_ENTER)
			indent++;
		long zeroTime = getRelativeTime(getStartTime());

		entryReport.append('-');
		long entryTime = getRelativeTime(entry.time);
		long diff = entryTime - zeroTime;
		entryReport.append(pad(Long.toString(diff), 6));
		entryReport.append(" : "); //$NON-NLS-1$
		diff = entry.time - compareWith.time;
		entryReport.append(pad(Long.toString(diff), 5));
		entryReport.append(pad("", indent * 2)); // indent before displaying the entry.id //$NON-NLS-1$
		entryReport.append("  - "); //$NON-NLS-1$
		entryReport.append(entry.id);
		entryReport.append(" > "); //$NON-NLS-1$
		entryReport.append(entry.msg);
		if (entry.description != null) {
			entryReport.append(" :: "); //$NON-NLS-1$
			entryReport.append(entry.description);
		}
		entryReport.append("\r\n"); //$NON-NLS-1$

		if (entry.flag == Profile.FLAG_EXIT)
			indent -= 1;
		return entryReport.toString();
	}

	protected void makeLog() {
		indent = 0;
		timelog.append("\r\n"); //$NON-NLS-1$
		for (int i = 0; i < timeEntriesIndex; i++) {
			TimeEntry entry = timeLogEntries[i];
			TimeEntry cmpEntry = findCompareEntry(i, entry.id, entry.flag);
			timelog.append(entryReport(entry, cmpEntry));
		}
		timeEntriesIndex = 0;
	}

	protected String pad(String str, int size) {
		padsb.setLength(0);
		int len = str.length();
		int count = size - len;
		for (int i = 0; i < count; i++)
			padsb.append(' ');
		padsb.append(str);
		return padsb.toString();
	}

	protected String getProfileLogReport() {
		if (timelog == null)
			return ""; //$NON-NLS-1$
		makeLog();
		String log = timelog.toString();
		timelog.setLength(0);
		return log;
	}

	protected void writeToProfileLogFile(String log) {
		File profileLog = getProfileLogFile();
		if (profileLog == null)
			return;
		FileWriter fw = null;
		try {
			fw = new FileWriter(profileLog.getAbsolutePath(), true);
			fw.write(log);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					// do nothing
				}
		}
	}

	protected File getProfileLogFile() {
		if (logFile == null)
			if ((logFileName != null) && (logFileName.length() > 0))
				logFile = new File(logFileName);
		return logFile;
	}

	protected long getStartTime() {
		return startTime;
	}

	protected long getRelativeTime(long absoluteTime) {
		return absoluteTime;
	}

	protected int getBufferSize() {
		if (bufferSize < 2)
			return DEFAULT_BUFFER_SIZE;
		return bufferSize;
	}

	protected TimeEntry timeEntryFactory() {
		return new TimeEntry();
	}

	protected class TimeEntry {
		long time;
		String id;
		String msg;
		String description;
		int flag;
	}
}
