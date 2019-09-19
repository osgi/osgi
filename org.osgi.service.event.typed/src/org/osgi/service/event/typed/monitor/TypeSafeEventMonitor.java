/*
 * Copyright (c) OSGi Alliance (2019). All Rights Reserved.
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
package org.osgi.service.event.typed.monitor;

import java.time.Instant;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.util.pushstream.PushStream;

/**
 * The EventMonitor service can be used to monitor the events that are sent
 * using the EventBus, and that are received from remote EventBus instances
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface TypeSafeEventMonitor {

    /**
	 * Get a stream of events, starting now.
	 *
	 * @return A stream of event data
	 */
	PushStream<MonitorEvent> monitorEvents();

	/**
	 * Get a stream of events, including up to the requested number of
	 * historical data events.
	 *
	 * @param history The requested number of historical events, note that fewer
	 *            than this number of events may be returned if history is
	 *            unavailable, or if insufficient events have been sent.
	 * @return A stream of event data
	 */
	PushStream<MonitorEvent> monitorEvents(int history);

	/**
	 * Get a stream of events, including historical data events prior to the
	 * supplied time
	 *
	 * @param history The requested time after which historical events, should
	 *            be included. Note that events may have been discarded, or
	 *            history unavailable.
	 * @return A stream of event data
	 */
	PushStream<MonitorEvent> monitorEvents(Instant history);

}
