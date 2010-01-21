/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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
package org.osgi.test.cases.webcontainer.util;

import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.event.Event;

/**
 * @version $Rev$ $Date$
 * this factory is used to record all events 
 *
 */
public class EventFactory {
	

	private static ConcurrentHashMap<String, Event> eventMap = new ConcurrentHashMap<String, Event>();


	/**
	 * register event w/ the factory based on event
	 * @param event
	 */
	public static void registerEvent(Event event) {
		eventMap.putIfAbsent(event.getProperty("bundle.symbolicName") + "_" + event.getTopic(), event);
	}
	/**
	 * get Event based on bundle symbolicname & topic
	 * @return
	 */
	public static Event getEvent(String bundleSymbolicName, String topic) {
		return eventMap.get(bundleSymbolicName + "_" + topic);
	}
	
	/**
	 * clear all events in the factory
	 */
	public static void clearEvents() {
		eventMap = new ConcurrentHashMap<String, Event>();
	}
	
	public static int getEventSize() {
		return eventMap.size();
	}
	
	
}
