/*
 * Copyright (c) OSGi Alliance (2015, 2020). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.serial;

import org.osgi.service.serial.SerialEvent;

public class SerialEventImpl implements SerialEvent {
	private String	comPort;
	private int		type;

	public SerialEventImpl(String comPort, int type) {
		this.comPort = comPort;
		this.type = type;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getComPort() {
		return comPort;
	}
}
