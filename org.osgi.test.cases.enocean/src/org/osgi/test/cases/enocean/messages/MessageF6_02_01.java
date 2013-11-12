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


package org.osgi.test.cases.enocean.messages;

import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.test.cases.enocean.channels.RPSActionChannel_00;
import org.osgi.test.cases.enocean.channels.RPSActionChannel_01;
import org.osgi.test.cases.enocean.channels.RPSEnergyBow_00;
import org.osgi.test.cases.enocean.channels.RPSSecondAction_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_R1_00;

/**
 * Temperature telegram, profile A5-02-01
 * 
 */
public class MessageF6_02_01 extends MessageType_RPS {

	public static final int	FUNC	= 0x02;
	public static final int	TYPE	= 0x01;


	public MessageF6_02_01(int action1, boolean eb, int action2, boolean secondAction) throws IllegalArgumentException {

		EnOceanChannel channelAction1 = new RPSActionChannel_00();
		EnOceanChannel channelEnergyBow = new RPSEnergyBow_00();
		EnOceanChannel channelAction2 = new RPSActionChannel_01();
		EnOceanChannel channelSecondAction = new RPSSecondAction_00();
		
		EnOceanChannelDescription description = new EnOceanChannelDescription_R1_00();
		channelAction1.setRawValue(description.serialize(new Integer(action1)));
		channelAction2.setRawValue(description.serialize(new Integer(action2)));
		if (eb) {
			channelEnergyBow.setRawValue(new byte[] {(byte) 0x01});
		} else {
			channelEnergyBow.setRawValue(new byte[] {(byte) 0x00});
		}
		if (secondAction) {
			channelSecondAction.setRawValue(new byte[] {(byte) 0x01});
		} else {
			channelSecondAction.setRawValue(new byte[] {(byte) 0x00});
		}

		byte b = getShiftedDataByte(channelAction1);
		b = (byte) (b | getShiftedDataByte(channelEnergyBow));
		b = (byte) (b | getShiftedDataByte(channelAction2));
		b = (byte) (b | getShiftedDataByte(channelSecondAction));
		setData(new byte[] {b});
	}

	private byte getShiftedDataByte(EnOceanChannel ch) {
		return (byte) ((ch.getRawValue()[0]) << (7 - ch.getOffset()));
	}
}
