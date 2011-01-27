/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.framework.weaving.tbx;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
/**
 * This class is used as to listen for Framework errors
 * issued when there is a Weaving failure
 * 
 * @author IBM
 */
public class ClassLoadErrorListener implements FrameworkListener {
	/** The expected cause of the error */
	private final Exception cause;
	/** The expected bundle that caused the error */
	private final Bundle source;
	/** Whether the correct error has been seen */
	private boolean validated = false;
	/** The list of events we have seen */
	private List events = new ArrayList();
	
	/**
	 * Create a Listener expecting an error with the supplied cause and source
	 * @param cause The exception that caused the error
	 * @param source The bundle that was the source of the error
	 */
	public ClassLoadErrorListener(RuntimeException cause, Bundle source) {
		this.cause = cause;
		this.source = source;
	}

	/**
	 * Receieve and validate the framework event
	 */
	public synchronized void frameworkEvent(FrameworkEvent event) {
		if(events.isEmpty()) {
			validated = (event.getType() == FrameworkEvent.ERROR &&
					event.getThrowable() == cause &&
					event.getBundle() == source);
		} else {
			//There should only be one event sent
			validated = false;
		}
		events.add(event);
	}

	/**
	 * True if one, and only one, valid event was received
	 * @return
	 */
	public boolean wasValidEventSent() {
		return validated;
	}
	
	public String toString() {
		return "Called with events " + events;
	}
}
