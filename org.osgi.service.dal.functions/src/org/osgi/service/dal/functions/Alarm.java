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
import org.osgi.service.dal.functions.data.AlarmData;

/**
 * {@code Alarm} function provides alarm sensor support. There is only one
 * eventable property and no operations.
 * 
 * @see AlarmData
 */
public interface Alarm extends Function {

	/**
	 * Specifies the alarm property name. The property is eventable.
	 * 
	 * @see AlarmData
	 */
	public static final String	PROPERTY_ALARM	= "alarm";
}
