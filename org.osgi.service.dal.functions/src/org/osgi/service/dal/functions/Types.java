/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.dal.functions;


/**
 * Shares common constants for all functions defined in this package. The
 * defined function types are mapped as follow:
 * <ul>
 * <li>{@link #TYPE_LIGHT} - {@link MultiLevelControl}, {@link MultiLevelSensor}, {@link BooleanSensor} and {@link BooleanControl}</li>
 * <li>{@link #TYPE_TEMPERATURE} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #TYPE_FLOW} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #TYPE_PRESSURE} - {@link MultiLevelControl},
 * {@link MultiLevelSensor} and {@link Meter}</li>
 * <li>{@link #TYPE_HUMIDITY} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #TYPE_GAS} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor} and {@link Meter}</li>
 * <li>{@link #TYPE_SMOKE} - {@link MultiLevelControl}, {@link MultiLevelSensor}
 * and {@link BooleanSensor}</li>
 * <li>{@link #TYPE_DOOR} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor} and {@link BooleanControl}</li>
 * <li>{@link #TYPE_WINDOW} - {@link MultiLevelControl},
 * {@link MultiLevelSensor}, {@link BooleanSensor} and {@link BooleanControl}</li>
 * <li>{@link #TYPE_LIQUID} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #TYPE_POWER} - {@link MultiLevelControl}, {@link MultiLevelSensor}, {@link BooleanSensor}, {@link BooleanControl} and {@link Meter}</li>
 * <li>{@link #TYPE_NOISINESS} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #TYPE_RAIN} - {@link MultiLevelSensor} and {@link BooleanSensor}</li>
 * <li>{@link #TYPE_CONTACT} - {@link BooleanSensor}</li>
 * <li>{@link #TYPE_FIRE} - {@link BooleanSensor}</li>
 * <li>{@link #TYPE_OCCUPANCY} - {@link BooleanSensor}</li>
 * <li>{@link #TYPE_WATER} - {@link BooleanSensor} and {@link Meter}</li>
 * <li>{@link #TYPE_MOTION} - {@link BooleanSensor}</li>
 * <li>{@link #TYPE_HEAT} - {@link Meter}</li>
 * <li>{@link #TYPE_COLD} - {@link Meter}</li>
 * </ul>
 * The mapping is not mandatory. The function can use custom defined types.
 */
public interface Types {

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control light devices. Usually, such
	 * devices are called dimmable. {@code MultiLevelControl} minimum value
	 * can switch off the device and {@code MultiLevelControl} maximum
	 * value can increase the device light to the maximum possible value.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * light level.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detected light. {@code true} state means that there is light.
	 * {@code false} state means that there is no light.</li>
	 * <li>
	 * {@code BinaryControl} - indicates that there is a light device
	 * control. {@code true} state means that the light device will be
	 * turned on. {@code false} state means that the light device will be
	 * turned off.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_LIGHT			= "light";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control temperature devices. For
	 * example, such device can be thermostat. {@code MultiLevelControl}
	 * minimum value is the lowest supported temperature.
	 * {@code MultiLevelControl} maximum value is the highest supported
	 * temperature.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * temperature.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_TEMPERATURE	= "temperature";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the flow level.
	 * {@code MultiLevelControl} minimum value is the minimum supported
	 * flow level. {@code MultiLevelControl} maximum value is the maximum
	 * supported flow level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * flow level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_FLOW			= "flow";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the pressure level.
	 * {@code MultiLevelControl} minimum value is the lowest supported
	 * pressure level. {@code MultiLevelControl} maximum value is the
	 * highest supported pressure level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * pressure level.</li>
	 * <li>
	 * {@code Meter} - Indicates that the {@code Meter} measures
	 * pressure.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_PRESSURE		= "pressure";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the humidity level. It's
	 * typical functionality for HVAC (heating, ventilation, and air
	 * conditioning) devices. {@code MultiLevelControl} minimum value is
	 * the lowest supported humidity level. {@code MultiLevelControl}
	 * maximum value is the highest supported humidity level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * humidity level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_HUMIDITY		= "humidity";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the gas level.
	 * {@code MultiLevelControl} minimum value is the lowest supported gas
	 * level. {@code MultiLevelControl} maximum value is the highest
	 * supported gas level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * gas level.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * supports gas detection. {@code true} state means there is gas.
	 * {@code false} state means that there is no gas.</li>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures the
	 * gas consumption.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_GAS			= "gas";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the smoke level.
	 * {@code MultiLevelControl} minimum value is the lowest supported
	 * smoke level. {@code MultiLevelControl} maximum value is the highest
	 * supported smoke level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * smoke level.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect smoke. {@code true} state means that there is smoke.
	 * {@code false} state means that there is no rain.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_SMOKE			= "smoke";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the door position.
	 * {@code MultiLevelControl} minimum value can completely close the
	 * door. {@code MultiLevelControl} maximum value can open the door to
	 * the maximum allowed position.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * door position.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect the door state. {@code true} state means that the door is
	 * opened. {@code false} state means that the door is closed.</li>
	 * <li>
	 * {@code BinaryControl} - indicates that there is a door position
	 * control. {@code true} state means that the door will be opened.
	 * {@code false} state means that the the door will be closed.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_DOOR			= "door";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the window position.
	 * {@code MultiLevelControl} minimum value can completely close the
	 * window. {@code MultiLevelControl} maximum value can open the window
	 * to the maximum allowed position.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * window position.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can window state. {@code true} state means that the window is
	 * opened. {@code false} state means that the window is closed.</li>
	 * <li>
	 * {@code BinaryControl} - indicates that there is a window position
	 * control. {@code true} state means that the window will be opened.
	 * {@code false} state means that the the window will be closed.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_WINDOW			= "window";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the liquid level.
	 * {@code MultiLevelControl} minimum value is the lowest supported
	 * liquid level. {@code MultiLevelControl} maximum value is the highest
	 * supported liquid level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * liquid level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_LIQUID			= "liquid";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the power level.
	 * {@code MultiLevelControl} minimum value is the lowest supported
	 * power level. {@code MultiLevelControl} maximum value is the highest
	 * supported power level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * power level.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect motion. {@code true} state means that there is power
	 * restore. {@code false} state means that there is power cut.</li>
	 * <li>
	 * {@code BinaryControl} - indicates that there is electricity control.
	 * {@code true} state means that the power will be restored.
	 * {@code false} state means that the power will be cut.</li>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures the
	 * power consumption.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_POWER			= "power";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the
	 * {@code MultiLevelControl} can control the noise level.
	 * {@code MultiLevelControl} minimum value is the lowest supported
	 * noise level. {@code MultiLevelControl} maximum value is the highest
	 * supported noise level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * noise level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_NOISINESS		= "noisiness";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the
	 * {@code MultiLevelSensor} can monitor the rain rate. It's not
	 * applicable to {@code MultiLevelControl}.</li>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect rain. {@code true} state means that there is rain.
	 * {@code false} state means that there is no rain.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_RAIN			= "rain";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect contact. {@code true} state means that there is contact.
	 * {@code false} state means that there is no contact.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_CONTACT		= "contact";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect fire. {@code true} state means that there is fire.
	 * {@code false} state means that there is no fire.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_FIRE			= "fire";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect presence. {@code true} state means that someone is
	 * detected. {@code false} state means that nobody is detected.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_OCCUPANCY		= "occupancy";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect water leak. {@code true} state means that there is water
	 * leak. {@code false} state means that there is no water leak.</li>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures water
	 * consumption.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_WATER			= "water";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BinarySensor} - indicates that the {@code BinarySensor}
	 * can detect motion. {@code true} state means that there is motion
	 * detection. {@code false} state means that there is no motion
	 * detection.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_MOTION			= "motion";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures
	 * thermal energy provided by a source.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_HEAT			= "heat";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures
	 * thermal energy provided by a source.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TYPE_COLD			= "cold";

}
