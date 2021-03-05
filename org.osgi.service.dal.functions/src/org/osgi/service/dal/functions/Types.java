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

package org.osgi.service.dal.functions;

/**
 * Shares common constants for all functions defined in this package. The
 * defined function types are mapped as follow:
 * <ul>
 * <li>{@link #LIGHT} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor} and {@link BooleanControl}</li>
 * <li>{@link #TEMPERATURE} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #FLOW} - {@link MultiLevelControl} and {@link MultiLevelSensor}</li>
 * <li>{@link #PRESSURE} - {@link MultiLevelControl}, {@link MultiLevelSensor}
 * and {@link Meter}</li>
 * <li>{@link #HUMIDITY} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #GAS} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor} and {@link Meter}</li>
 * <li>{@link #SMOKE} - {@link MultiLevelControl}, {@link MultiLevelSensor} and
 * {@link BooleanSensor}</li>
 * <li>{@link #DOOR} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor} and {@link BooleanControl}</li>
 * <li>{@link #WINDOW} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor} and {@link BooleanControl}</li>
 * <li>{@link #LIQUID} - {@link MultiLevelControl} and {@link MultiLevelSensor}</li>
 * <li>{@link #POWER} - {@link MultiLevelControl}, {@link MultiLevelSensor},
 * {@link BooleanSensor}, {@link BooleanControl} and {@link Meter}</li>
 * <li>{@link #NOISINESS} - {@link MultiLevelControl} and
 * {@link MultiLevelSensor}</li>
 * <li>{@link #RAIN} - {@link MultiLevelSensor} and {@link BooleanSensor}</li>
 * <li>{@link #CONTACT} - {@link BooleanSensor}</li>
 * <li>{@link #FIRE} - {@link BooleanSensor}</li>
 * <li>{@link #OCCUPANCY} - {@link BooleanSensor}</li>
 * <li>{@link #WATER} - {@link BooleanSensor} and {@link Meter}</li>
 * <li>{@link #MOTION} - {@link BooleanSensor}</li>
 * <li>{@link #HEAT} - {@link Meter}</li>
 * <li>{@link #COLD} - {@link Meter}</li>
 * </ul>
 * The mapping is not mandatory. The function can use custom defined types.
 */
public interface Types { // NO_UCD

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control light devices. Usually, such devices are called dimmable.
	 * {@code MultiLevelControl} minimum value can switch off the device and
	 * {@code MultiLevelControl} maximum value can increase the device light to
	 * the maximum possible value.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * light level.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detected light. {@code true} state means that there is light.
	 * {@code false} state means that there is no light.</li>
	 * <li>
	 * {@code BooleanControl} - indicates that there is a light device control.
	 * {@code true} state means that the light device will be turned on.
	 * {@code false} state means that the light device will be turned off.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	LIGHT		= "light";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control temperature devices. For example, such device can be
	 * thermostat. {@code MultiLevelControl} minimum value is the lowest
	 * supported temperature. {@code MultiLevelControl} maximum value is the
	 * highest supported temperature.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * temperature.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	TEMPERATURE	= "temperature";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the flow level. {@code MultiLevelControl} minimum value is
	 * the minimum supported flow level. {@code MultiLevelControl} maximum value
	 * is the maximum supported flow level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the flow
	 * level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	FLOW		= "flow";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the pressure level. {@code MultiLevelControl} minimum value
	 * is the lowest supported pressure level. {@code MultiLevelControl} maximum
	 * value is the highest supported pressure level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * pressure level.</li>
	 * <li>
	 * {@code Meter} - Indicates that the {@code Meter} measures pressure.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	PRESSURE	= "pressure";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the humidity level. It's typical functionality for HVAC
	 * (heating, ventilation, and air conditioning) devices.
	 * {@code MultiLevelControl} minimum value is the lowest supported humidity
	 * level. {@code MultiLevelControl} maximum value is the highest supported
	 * humidity level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * humidity level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	HUMIDITY	= "humidity";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the gas level. {@code MultiLevelControl} minimum value is the
	 * lowest supported gas level. {@code MultiLevelControl} maximum value is
	 * the highest supported gas level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the gas
	 * level.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} supports
	 * gas detection. {@code true} state means there is gas. {@code false} state
	 * means that there is no gas.</li>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures the gas
	 * consumption.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	GAS			= "gas";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the smoke level. {@code MultiLevelControl} minimum value is
	 * the lowest supported smoke level. {@code MultiLevelControl} maximum value
	 * is the highest supported smoke level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * smoke level.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect smoke. {@code true} state means that there is smoke. {@code false}
	 * state means that there is no smoke.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	SMOKE		= "smoke";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the door position. {@code MultiLevelControl} minimum value
	 * can completely close the door. {@code MultiLevelControl} maximum value
	 * can open the door to the maximum allowed position.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the door
	 * position.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect the door state. {@code true} state means that the door is opened.
	 * {@code false} state means that the door is closed.</li>
	 * <li>
	 * {@code BooleanControl} - indicates that there is a door position control.
	 * {@code true} state means that the door will be opened. {@code false}
	 * state means that the door will be closed.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	DOOR		= "door";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the window position. {@code MultiLevelControl} minimum value
	 * can completely close the window. {@code MultiLevelControl} maximum value
	 * can open the window to the maximum allowed position.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * window position.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * window state. {@code true} state means that the window is opened.
	 * {@code false} state means that the window is closed.</li>
	 * <li>
	 * {@code BooleanControl} - indicates that there is a window position
	 * control. {@code true} state means that the window will be opened.
	 * {@code false} state means that the window will be closed.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	WINDOW		= "window";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the liquid level. {@code MultiLevelControl} minimum value is
	 * the lowest supported liquid level. {@code MultiLevelControl} maximum
	 * value is the highest supported liquid level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * liquid level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	LIQUID		= "liquid";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the power level. {@code MultiLevelControl} minimum value is
	 * the lowest supported power level. {@code MultiLevelControl} maximum value
	 * is the highest supported power level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * power level.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect power/no power. {@code true} state means that there is power.
	 * {@code false} state means that there is no power.</li>
	 * <li>
	 * {@code BooleanControl} - indicates that there is electricity control.
	 * {@code true} state means that the power will be restored. {@code false}
	 * state means that the power will be cut.</li>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures the power
	 * consumption.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	POWER		= "power";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelControl} - indicates that the {@code MultiLevelControl}
	 * can control the noise level. {@code MultiLevelControl} minimum value is
	 * the lowest supported noise level. {@code MultiLevelControl} maximum value
	 * is the highest supported noise level.</li>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the sensor can monitor the
	 * noise level.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	NOISINESS	= "noisiness";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code MultiLevelSensor} - indicates that the {@code MultiLevelSensor}
	 * can monitor the rain rate. It's not applicable to
	 * {@code MultiLevelControl}.</li>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect rain. {@code true} state means that there is rain. {@code false}
	 * state means that there is no rain.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	RAIN		= "rain";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect contact. {@code true} state means that there is contact.
	 * {@code false} state means that there is no contact.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	CONTACT		= "contact";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect fire. {@code true} state means that there is fire. {@code false}
	 * state means that there is no fire.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	FIRE		= "fire";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect presence. {@code true} state means that someone is detected.
	 * {@code false} state means that nobody is detected.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	OCCUPANCY	= "occupancy";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect water leak. {@code true} state means that there is water leak.
	 * {@code false} state means that there is no water leak.</li>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures water
	 * consumption.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	WATER		= "water";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code BooleanSensor} - indicates that the {@code BooleanSensor} can
	 * detect motion. {@code true} state means that there is motion detection.
	 * {@code false} state means that there is no motion detection.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	MOTION		= "motion";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures thermal energy
	 * provided by a source.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	HEAT		= "heat";

	/**
	 * The function type is applicable to:
	 * <ul>
	 * <li>
	 * {@code Meter} - indicates that the {@code Meter} measures thermal energy
	 * provided by a source.</li>
	 * </ul>
	 * This type can be specified as a value of
	 * {@link org.osgi.service.dal.Function#SERVICE_TYPE}.
	 */
	public static final String	COLD		= "cold";
}
