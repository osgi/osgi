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
import org.osgi.test.support.sleep.Sleep;

public class FilteredProducerImpl implements Producer {
	private volatile Wire	wire	= null;

	/**
	 * This method should not be called in the test.
	 *
	 * @return value out of range to indicate the wrong call
	 */
	@Override
	public Object polled(Wire w) {
		return Integer.valueOf(100);
	}

	@Override
	public void consumersConnected(Wire[] wires) {
		if (wires != null)
			this.wire = wires[0];
	}

	void updateWire(int delta) {
		for (int i = 0; i < 10; i += delta) {
			wire.update(Integer.valueOf(i));
		}
	}

	void updateWireDelayed(int delta, long delay) {
		for (int i = 0; i < 10; i += delta) {
			long processTime = System.currentTimeMillis();
			wire.update(Integer.valueOf(i));
			processTime = System.currentTimeMillis() - processTime;
			try {
				Sleep.sleep(delay - processTime);
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	Wire getWire() {
		return wire;
	}
}
