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

/**
 * Helps in testing event dispatchment in the wire admin
 * 
 * @author Vasil Panushev
 */
public class EventTestProducer implements Producer {
	private final boolean	crash;
	private boolean			ccCrashed;
	private boolean			polledCrashed;

	public EventTestProducer(boolean crash) {
		ccCrashed = false;
		polledCrashed = false;
		this.crash = crash;
	}

	@Override
	public synchronized void consumersConnected(Wire[] wires) {
		if (crash & !ccCrashed) {
			ccCrashed = true;
			throw new RuntimeException("testing");
		}
	}

	@Override
	public synchronized Object polled(Wire wire) {
		if (crash && !polledCrashed) {
			polledCrashed = true;
			throw new RuntimeException("testing");
		}
		return "42";
	}
}
