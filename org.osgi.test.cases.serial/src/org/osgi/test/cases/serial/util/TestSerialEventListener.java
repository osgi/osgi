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

package org.osgi.test.cases.serial.util;

import org.osgi.service.serial.SerialEvent;
import org.osgi.service.serial.SerialEventListener;

public class TestSerialEventListener implements SerialEventListener {
	private boolean	isReceived	= false;
	private int		receivedType;
	private String	receivedComPort;

	public void notifyEvent(SerialEvent event) {
		this.isReceived = true;
		this.receivedType = event.getType();
		this.receivedComPort = event.getComPort();
	}

	public boolean isReceived() {
		return this.isReceived;
	}

	public int getReceivedType() {
		return this.receivedType;
	}

	public String getReceivedComPort() {
		return this.receivedComPort;
	}
}
