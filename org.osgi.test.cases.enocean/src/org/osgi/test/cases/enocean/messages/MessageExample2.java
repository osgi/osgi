/*
 * Copyright (c) OSGi Alliance (2014, 2017). All Rights Reserved.
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

package org.osgi.test.cases.enocean.messages;

import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.test.cases.enocean.channels.Channel01;
import org.osgi.test.cases.enocean.channels.Channel02;
import org.osgi.test.cases.enocean.channels.Channel03;
import org.osgi.test.cases.enocean.channels.Channel04;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription1;

/**
 * Example telegram 2
 * 
 * @author $Id$
 */
public class MessageExample2 extends MessageType_1 {

	/**  */
	public static final int	FUNC	= 0x02;
	/**  */
	public static final int	TYPE	= 0x01;

	/**
	 * @param action1
	 * @param eb
	 * @param action2
	 * @param secondAction
	 * @throws IllegalArgumentException
	 */
	public MessageExample2(int action1, boolean eb, int action2, boolean secondAction) throws IllegalArgumentException {

		EnOceanChannel channel01 = new Channel01();
		EnOceanChannel channel02 = new Channel02();
		EnOceanChannel channel03 = new Channel03();
		EnOceanChannel channel04 = new Channel04();

		EnOceanChannelDescription description = new EnOceanChannelDescription1();
		channel01.setRawValue(description.serialize(Integer.valueOf(action1)));
		channel02.setRawValue(description.serialize(Integer.valueOf(action2)));
		if (eb) {
			channel03.setRawValue(new byte[] {(byte) 0x01});
		} else {
			channel03.setRawValue(new byte[] {(byte) 0x00});
		}
		if (secondAction) {
			channel04.setRawValue(new byte[] {(byte) 0x01});
		} else {
			channel04.setRawValue(new byte[] {(byte) 0x00});
		}

		byte b = getShiftedDataByte(channel01);
		b = (byte) (b | getShiftedDataByte(channel03));
		b = (byte) (b | getShiftedDataByte(channel02));
		b = (byte) (b | getShiftedDataByte(channel04));
		setData(new byte[] {b});
	}

	private byte getShiftedDataByte(EnOceanChannel ch) {
		return (byte) ((ch.getRawValue()[0]) << (7 - ch.getOffset()));
	}
}
