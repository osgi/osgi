/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
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
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.SIUnits;
import org.osgi.service.dal.functions.WakeUp;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code WakeUp} function.
 */
public final class SimulatedWakeUp extends SimulatedFunction implements WakeUp { // NO_UCD

	private static final String		MILLIS					= SIUnits.PREFIX_MILLI + SIUnits.SECOND;
	private static final String[]	MILLIS_ARRAY			= new String[] {MILLIS};
	private static final LevelData	MIN_WAKE_UP_INTERVAL	= new LevelData(
																	System.currentTimeMillis(), null, new BigDecimal(0), MILLIS);
	private static final Map		PROPERTY_METADATA;
	private static final Map		OPERATION_METADATA		= null;

	private final Timer				timer;
	private final Object			lock					= new Object();

	private TimerTask				timerTask;
	private boolean					removed;
	private LevelData				wakeUpInterval			= MIN_WAKE_UP_INTERVAL;

	static {
		Map metadata = new HashMap();
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
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(PROPERTY_WAKE_UP_INTERVAL, propMetadata);

		metadata = new HashMap();
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
	public SimulatedWakeUp(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker, Timer timer) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.timer = timer;
		super.register(
				new String[] {WakeUp.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	public LevelData getWakeUpInterval() {
		synchronized (this.lock) {
			if (this.removed) {
				throw new IllegalStateException("The Wake Up function is removed.");
			}
			return this.wakeUpInterval;
		}
	}

	public void setWakeUpInterval(BigDecimal interval, String unit) {
		if ((null != unit) && (!MILLIS.equals(unit))) {
			throw new IllegalArgumentException("The unit is not supported: " + unit);
		}
		this.setWakeUpInterval(interval, true, true);
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

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
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

		public void run() {
			postEvent(WakeUp.PROPERTY_AWAKE, new BooleanData(System.currentTimeMillis(), null, true));
		}

	}
}
