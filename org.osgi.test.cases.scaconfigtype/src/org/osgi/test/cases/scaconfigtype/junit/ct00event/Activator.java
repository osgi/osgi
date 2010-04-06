/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.test.cases.scaconfigtype.junit.ct00event;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.scaconfigtype.common.TestEventHandler;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class Activator implements BundleActivator {
	BundleContext       context;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
    	Hashtable<String, Object> props = new Hashtable<String, Object>();
    	props.put(EventConstants.EVENT_TOPIC, new String[]{
				"org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION",
				"org/osgi/service/remoteserviceadmin/IMPORT_UNREGISTRATION",
				"org/osgi/service/remoteserviceadmin/IMPORT_ERROR"});
		TestEventHandler eventHandler = new TestEventHandlerImpl();
		
		context.registerService( new String[] {
			EventHandler.class.getName(),
			TestEventHandler.class.getName()
		}, eventHandler, props);
		
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

	public static class TestEventHandlerImpl implements TestEventHandler {
		private LinkedList<Event> eventlist = new LinkedList<Event>();
		private Semaphore sem = new Semaphore(0);


		/**
		 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
		 */
		public void handleEvent(Event event) {
			eventlist.add(event);
			sem.signal();
		}
		
		public Object getNextEvent() {
			try {
				sem.waitForSignal(60000); // wait max 1min for async notification
			} catch (InterruptedException e1) {
				return null;
			}
			
			try {
				return eventlist.removeFirst();
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		
		public int getEventCount() {
			return eventlist.size();
		}
	}
}
