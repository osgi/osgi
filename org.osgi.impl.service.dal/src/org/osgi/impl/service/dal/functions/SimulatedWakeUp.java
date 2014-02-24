/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.Units;
import org.osgi.service.dal.functions.WakeUp;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated <code>WakeUp</code> function.
 */
public final class SimulatedWakeUp extends SimulatedFunction implements WakeUp {

	private static final String		MILLIS				= Units.PREFIX_MILLI + Units.SECOND;
	private static final String[]	MILLIS_ARRAY		= new String[] {MILLIS};
	private static final LevelData	MIN_WAKE_UP_INTERVAL	= new LevelData(
																	System.currentTimeMillis(), null, MILLIS, new BigDecimal(0));
	private static final Map	PROPERTY_METADATA;
	private static final Map	OPERATION_METADATA	= null;
	
	private final Object			lock				= new Object();

	private TimerTask			timerTask;
	private Timer					timer;
	private boolean					removed;
	private LevelData				wakeUpInterval			= MIN_WAKE_UP_INTERVAL;

	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.PROPERTY_ACCESS,
				new Integer(
						PropertyMetadata.PROPERTY_ACCESS_READABLE |
								PropertyMetadata.PROPERTY_ACCESS_WRITABLE));
		metadata.put(PropertyMetadata.UNITS, MILLIS_ARRAY);
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // resolution
				null,     // enumValues
				MIN_WAKE_UP_INTERVAL,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(WakeUp.PROPERTY_WAKE_UP_INTERVAL, propMetadata);

		metadata = new HashMap();
		metadata.put(
				PropertyMetadata.PROPERTY_ACCESS,
				new Integer(
						PropertyMetadata.PROPERTY_ACCESS_EVENTABLE));
		propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // resolution
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA.put(WakeUp.PROPERTY_AWAKE, propMetadata);
	}
	
	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context used to register the service.
	 * @param eventAdminTracker The event admin service tracker to post events.
	 * @param timer The timer used for awake notifications.
	 */
	public SimulatedWakeUp(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker, Timer timer) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.timer = timer;
		super.register(WakeUp.class.getName(), addPropertyAndOperationNames(functionProps), bc);
	}

	public LevelData getWakeUpInterval() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		synchronized (this.lock) {
			if (this.removed) {
				throw new IllegalStateException("The Wake Up function is removed.");
			}
			return this.wakeUpInterval;
		}
	}

	public void setWakeUpInterval(BigDecimal interval) throws UnsupportedOperationException, IllegalStateException, DeviceException, IllegalArgumentException {
		final long longInterval = interval.longValue();
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
				this.timer.schedule(this.timerTask, longInterval, longInterval);
			}
			this.wakeUpInterval = new LevelData(System.currentTimeMillis(), null, MILLIS, interval);
		}
	}

	public void setWakeUpInterval(BigDecimal interval, String unit) throws UnsupportedOperationException, IllegalStateException, DeviceException, IllegalArgumentException {
		if ((null != unit) && (!MILLIS.equals(unit))) {
			throw new IllegalArgumentException("The unit is not supported: " + unit);
		}
		this.setWakeUpInterval(interval);
	}

	public void sleep() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		// nothing special to do
	}

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

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				Function.SERVICE_PROPERTY_NAMES,
				new String[] {
						WakeUp.PROPERTY_WAKE_UP_INTERVAL,
						WakeUp.PROPERTY_AWAKE});
		return functionProps;
	}

	private class WakeUpTimeTask extends TimerTask {

		public WakeUpTimeTask() {
			// default constructor
		}

		public void run() {
			postEvent(WakeUp.PROPERTY_AWAKE, new BooleanData(System.currentTimeMillis(), null, true));
		}

	}

}
