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

package org.osgi.test.cases.enocean.devices;

import org.osgi.service.enocean.EnOceanHandler;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanRPC;

/**
 * @author $Id$
 */
public class Device01 extends EnOceanDeviceImpl {

	/**
	 * 
	 */
	public Device01() {
		lastMessage = null;
		encryptionKey = null;
		rollingCode = -1;
		learnMode = false;
	}

	/**
	 * @param message
	 * @param handler
	 */
	public void send(byte[] message, EnOceanHandler handler) {
		// TODO
	}

	/**
	 * @param message
	 * @param handler
	 */
	public void send(EnOceanMessage message, EnOceanHandler handler) {
		send(message.getBytes(), handler);
	}

	public void invoke(EnOceanRPC rpc, EnOceanHandler handler) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	public void setFunc(int func) {
		// TODO Auto-generated method stub

	}

	public void setType(int type) {
		// TODO Auto-generated method stub

	}

	public int getChipId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRorg() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getManufacturer() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSecurityLevelFormat() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void remove() {
		// TODO Auto-generated method stub
	}

}
