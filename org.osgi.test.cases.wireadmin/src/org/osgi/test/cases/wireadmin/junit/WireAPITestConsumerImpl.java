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
package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class WireAPITestConsumerImpl implements Consumer {
	private volatile Object	value	= null;

	@Override
	public void updated(Wire wire, Object v) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			DefaultTestBundleControl.log("Consumer.updated received value " + v
					+ " from correct wire");
			this.value = v;
			return;
		}
		DefaultTestBundleControl.log("Consumer.updated received value " + v
				+ " from incorrect wire " + wire);
		this.value = null;
	}

	@Override
	public void producersConnected(Wire[] wires) {
		// empty
	}

	Object getValue() {
		return value;
	}
}
