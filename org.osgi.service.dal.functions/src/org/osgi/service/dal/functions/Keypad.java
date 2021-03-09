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

import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.data.KeypadData;

/**
 * {@code Keypad} function provides support for keypad control. The keypad
 * typically consists of one or more keys/buttons, which can be discerned.
 * Different types of key presses like short and long press can typically also
 * be detected. Each key pressed event is followed by a key released event. It's
 * not possible to have two consecutive key pressed or key released events.
 * There is only one eventable property and no operations.
 * <p>
 * {@code Keypad} can enumerate all supported keys in the key property metadata,
 * {@link PropertyMetadata#getEnumValues(String)}.
 * 
 * @see KeypadData
 */
public interface Keypad extends Function {

	/**
	 * Specifies a property name for a key from the keypad. The property is
	 * eventable.
	 * 
	 * @see KeypadData
	 */
	public static final String	PROPERTY_KEY	= "key";
}
