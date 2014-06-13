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

package org.osgi.impl.service.enocean.basedriver.radio;

/**
 * Prototype of a RPS telegram
 * 
 */
public class MessageRPS extends Message {

	public MessageRPS(byte[] data) {
		super(data);
	}

	public boolean isTeachin() {
		return true;
	}

	/**
	 * @return true if the message teach-in embeds profile & manufacturer info.
	 */
	public boolean hasTeachInInfo() {
		return true;
	}

	/**
	 * @return a fake FUNC
	 */
	public int teachInFunc() {
		return -1;
	}

	/**
	 * @return a fake TYPE
	 */
	public int teachInType() {
		return -1;
	}

	/**
	 * @return a fake MANUF
	 */
	public int teachInManuf() {
		return -1;
	}
}
