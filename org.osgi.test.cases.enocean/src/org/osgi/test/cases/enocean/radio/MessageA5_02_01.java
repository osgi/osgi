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


package org.osgi.test.cases.enocean.radio;






/**
 * Temperature telegram, profile A5-02-01
 * 
 * 
 * @author $Id$
 */
public class MessageA5_02_01 extends MessageType_4BS {

	public static final int	FUNC	= 0x02;
	public static final int	TYPE	= 0x01;

	private final float		domainStart	= -40.0f;
	private final float		domainStop	= 0.0f;
	private final int		rangeStart	= 255;
	private final int		rangeStop	= 0;

	private MessageA5_02_01() {
	}

	/**
	 * Default constructor, generates a teach-in message
	 */
	public static MessageA5_02_01 generateTeachInMsg(int senderId, int manufacturerId) {
		MessageA5_02_01 msg = new MessageA5_02_01();
		byte[] data = { 0x0, 0x0, 0x0, 0x0 };
		data[0] = (byte) (((FUNC << 2) | (TYPE >> 5)));
		data[1] = (byte) (((TYPE << 3) & 0x7c) | ((manufacturerId >> 8) & 0x07));
		data[2] = (byte) (manufacturerId & 0xff);
		data[3] = (byte) 0x80; // teach-in with information
		msg.setData(data);
		msg.setSenderId(senderId);
		return msg;
	}

	public MessageA5_02_01(float temperature) throws IllegalArgumentException {
		if ((temperature < domainStart) || (temperature > domainStop)) {
			throw new IllegalArgumentException("incorrect supplied value");
		}
		byte[] data = {0x0, 0x0, 0x0, 0x0};
		data[2] = (byte) ((rangeStart / domainStart) * temperature);
		data[3] = 0x08; // deactivate teach-in
		setData(data);
	}
}
