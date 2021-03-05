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

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class WireAPITestProducerImpl implements Producer {
	private volatile boolean	throwsException;

	@Override
	public Object polled(Wire wire) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			if (throwsException) {
				DefaultTestBundleControl
						.log("Producer.polled called for correct wire. Throwing exception ...");
				throw new RuntimeException("test exception");
			}
			DefaultTestBundleControl
					.log("Producer.polled called for correct wire. Returning '42'");
			return Integer.valueOf("42");
		}
		DefaultTestBundleControl.log("Producer.polled called for incorrect wire "
				+ wire + ". Crashing now!");
		throw new RuntimeException(
				"Wire API test producer polled from incorrect wire!");
	}

	@Override
	public void consumersConnected(Wire[] wires) {
		// empty
	}

	void setThrowsException(boolean state) {
		throwsException = state;
	}
}
