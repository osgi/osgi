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
 * Prototype of a 4BS telegram
 * 
 * Teach-in procedure:
 * 
 * - if DB0.3 is 0, then it's a teach-in telegram.
 * 
 * - if DB0.7 is also 0, no manufacturer info.
 * 
 * - if DB0.7 is 1, manufacturer info is present.
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public abstract class Message4BS extends Message {

	public Message4BS(byte[] data) {
		super(data);
	}

	public Message4BS(Message msg) {
		super(msg);
	}

	public boolean isTeachin() {
		return (getData()[3] & 0x08) == 0;
	}

	/**
	 * @return  true if the message teach-in embeds profile & manufacturer info.
	 */
	public boolean hasTeachInInfo() {
		return (getData()[3] & 0x80) != 0;
	}

	/**
	 * @return the FUNC in the case of a teach-in message with information.
	 */
	public int getFunc() {
		return (getData()[0] >> 2) & 0xff;
	}
	
	/**
	 * @return the TYPE in the case of a teach-in message with information.
	 */
	public int getType() {
		byte b0 = getData()[0];
		byte b1 = getData()[1];
		return (((b0 & 0x03) << 5) & 0xff) | ((((b1 >> 3)) & 0xff));
	}
	
	/**
	 * @return the MANUF in the case of a teach-in message with information.
	 */
	public int getManuf() {
		byte b0 = (byte) ((getData()[1]) & 0x07);
		byte b1 = getData()[2];
		return ((b0 & 0xff) << 8) + (b1 & 0xff);
	}

}
