/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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


package org.osgi.impl.service.enocean.basedriver.impl;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.channels.EnOceanChannel;

public abstract class EnOceanMessageImpl_4BS extends EnOceanMessageImpl {

	public int getRorg() {
		return 0xA5;
	}

	public byte[] serialize() throws EnOceanException {
		byte[] out = new byte[4];
		for (int i = 0; i < channels.length; i++) {
			EnOceanChannel c = channels[i];
			// TODO: Finish the serialization here
		}
		return null;
	}


}
