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

import java.util.Map;

import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanMessage;

/**
 * @author $Id$
 */
public abstract class EnOceanDeviceImpl implements EnOceanDevice {

	/**  */
	protected int[]				learntDevices;
	/**  */
	protected Map<Integer,Integer[]>	availableRpcs;
	/**  */
	protected EnOceanMessage	lastMessage;
	/**  */
	protected byte[]			encryptionKey;
	/**  */
	protected int				rollingCode;
	/**  */
	protected boolean			learnMode;

	public void setLearningMode(boolean learnMode) {
		this.learnMode = learnMode;
	}

	public int getRollingCode() {
		return rollingCode;
	}

	public void setRollingCode(int rollingCode) {
		this.rollingCode = rollingCode;
	}

	public byte[] getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(byte[] key) {
		this.encryptionKey = key;
	}

	/**
	 * @return the last message.
	 */
	public EnOceanMessage getLastMessage() {
		return lastMessage;
	}

	public int[] getLearnedDevices() {
		return learntDevices;
	}

	public Map<Integer,Integer[]> getRPCs() {
		return availableRpcs;
	}

}
