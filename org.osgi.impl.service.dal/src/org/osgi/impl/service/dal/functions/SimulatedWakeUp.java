/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.dal.functions;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.SIUnits;
import org.osgi.service.dal.functions.WakeUp;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code WakeUp} function.
 */
public final class SimulatedWakeUp extends SimulatedFunction implements WakeUp { // NO_UCD

	private static final String		MILLIS					= SIUnits.PREFIX_MILLI + SIUnits.SECOND;
	private static final String[]	MILLIS_ARRAY			= new String[] {MILLIS};
	private static final LevelData	MIN_WAKE_UP_INTERVAL	= new LevelData(
																	System.currentTimeMillis(), null, new BigDecimal(0), MILLIS);
	private static final Map<String,Object>	PROPERTY_METADATA;
	private static final Map<String,Object>	OPERATION_METADATA		= null;

	private final Timer				timer;
	private final Object			lock					= new Object();

	private TimerTask				timerTask;
	private boolean					removed;
	private LevelData				wakeUpInterval			= MIN_WAKE_UP_INTERVAL;

	static {
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(
						PropertyMetadata.ACCESS_READABLE |
								PropertyMetadata.ACCESS_EVENTABLE |
								PropertyMetadata.ACCESS_WRITABLE));
		metadata.put(PropertyMetadata.UNITS, MILLIS_ARRAY);
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // step
				null,     // enumValues
				MIN_WAKE_UP_INTERVAL,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap<>();
		PROPERTY_METADATA.put(PROPERTY_WAKE_UP_INTERVAL, propMetadata);

		metadata = new HashMap<>();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(
						PropertyMetadata.ACCESS_EVENTABLE));
		propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // step
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA.put(PROPERTY_AWAKE, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context used to register the service.
	 * @param eventAdminTracker The event admin service tracker to post events.
	 * @param timer The timer used for awake notifications.
	 */
	public SimulatedWakeUp(Dictionary<String,Object> functionProps,
			BundleContext bc,
			ServiceTracker<EventAdmin,EventAdmin> eventAdminTracker,
			Timer timer) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.timer = timer;
		super.register(
				new String[] {WakeUp.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	@Override
	public LevelData getWakeUpInterval() {
		synchronized (this.lock) {
			if (this.removed) {
				throw new IllegalStateException("The Wake Up function is removed.");
			}
			return this.wakeUpInterval;
		}
	}

	@Override
	public void setWakeUpInterval(BigDecimal interval, String unit) {
		if ((null != unit) && (!MILLIS.equals(unit))) {
			throw new IllegalArgumentException("The unit is not supported: " + unit);
		}
		this.setWakeUpInterval(interval, true, true);
	}

	@Override
	public void remove() {
		synchronized (this.lock) {
			if (null != this.timerTask) {
				this.timerTask.cancel();
				this.timerTask = null;
			}
			this.removed = true;
		}
		super.remove();
	}

	@Override
	public void publishEvent(String propName) {
		if (PROPERTY_AWAKE.equals(propName)) {
			setWakeUpInterval(getWakeUpInterval().getLevel(), false, false);
		} else
			if (PROPERTY_WAKE_UP_INTERVAL.equals(propName)) {
				throw new IllegalArgumentException("The property event can be sent on set for: " + propName);
			} else {
				throw new IllegalArgumentException("The property is not supported: " + propName);
			}
	}

	private void setWakeUpInterval(BigDecimal interval, boolean execDelay, boolean postEvent) {
		long longInterval = interval.longValue();
		if (longInterval < 0) {
			throw new IllegalArgumentException("The interval is negative: " + interval);
		}
		synchronized (this.lock) {
			if (this.removed) {
				throw new IllegalStateException("The Wake Up function is removed.");
			}
			if (null != this.timerTask) {
				this.timerTask.cancel();
				this.timerTask = null;
			}
			if (longInterval > 0) {
				this.timerTask = new WakeUpTimeTask();
				this.timer.schedule(this.timerTask, execDelay ? longInterval : 0, longInterval);
			}
			this.wakeUpInterval = new LevelData(System.currentTimeMillis(), null, interval, MILLIS);
			if (postEvent) {
				super.postEvent(PROPERTY_WAKE_UP_INTERVAL, this.wakeUpInterval);
			}
		}
	}

	private static Dictionary<String,Object> addPropertyAndOperationNames(
			Dictionary<String,Object> functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {
						PROPERTY_WAKE_UP_INTERVAL,
						PROPERTY_AWAKE});
		return functionProps;
	}

	private class WakeUpTimeTask extends TimerTask {

		public WakeUpTimeTask() {
			// default constructor
		}

		@Override
		public void run() {
			postEvent(WakeUp.PROPERTY_AWAKE, new BooleanData(System.currentTimeMillis(), null, true));
		}

	}
}
