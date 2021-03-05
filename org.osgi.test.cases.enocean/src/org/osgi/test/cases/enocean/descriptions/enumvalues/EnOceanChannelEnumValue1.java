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

package org.osgi.test.cases.enocean.descriptions.enumvalues;

import org.osgi.service.enocean.descriptions.EnOceanChannelEnumValue;

/**
 * @author $Id$
 */
public class EnOceanChannelEnumValue1 implements EnOceanChannelEnumValue {

	public int getStart() {
		return 1;
	}

	public int getStop() {
		return 1;
	}

	public String getDescription() {
		return "EnOceanChannelEnumValue1";
	}

}
