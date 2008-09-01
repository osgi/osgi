package org.osgi.test.cases.log;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LogReader implements LogListener {
	final List/* <LogEntry> */log = new ArrayList();

	/**
	 * Add any entry that has 4711 in the message to the log list
	 */
	public void logged(LogEntry entry) {
		if (entry.getMessage().indexOf("<4711>") >= 0) {
			synchronized (log) {
				log.add(entry);
				log.notifyAll();
			}
		}
	}

	public LogEntry getEntry(int timeout) {
		synchronized (log) {
			while (log.size() == 0)
				try {
					log.wait(timeout);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			return (LogEntry) log.remove(0);
		}
	}

	public synchronized int size() {
		return log.size();
	}
}
