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

package org.osgi.test.cases.enocean.serial;

import org.osgi.test.cases.enocean.utils.ByteSerializable;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 * @author $Id$
 */
public class EspRadioPacket extends EspPacket {

	/**
	 * @param pkt
	 */
	public EspRadioPacket(ByteSerializable pkt) {
		setPacketType(TYPE_RADIO);
		setOptionalLength(7);
		setOptional(new RadioOptions());
		setData(pkt);
	}

	private class RadioOptions implements ByteSerializable {
		private int	subTelNum;		// 1 byte
		private int	destinationId;	// 4 bytes
		private int	dBm;			// 1 byte
		private int	securityLevel;	// 1 byte

		public RadioOptions() {
			// Fill in default values for a SEND
			setSubTelNum(3);
			setDestinationId(0xffffffff);
			setdBm(0xff);
			setSecurityLevel(0);
		}

		public byte[] getBytes() {
			byte[] data = Utils.intTo1Byte(getSubTelNum());
			data = Utils.byteConcat(data, Utils.intTo4Bytes(getDestinationId()));
			data = Utils.byteConcat(data, Utils.intTo1Byte(getdBm()));
			data = Utils.byteConcat(data, Utils.intTo1Byte(getSecurityLevel()));
			return data;
		}

		public int getSubTelNum() {
			return subTelNum;
		}

		public void setSubTelNum(int subTelNum) {
			this.subTelNum = subTelNum;
		}

		public int getDestinationId() {
			return destinationId;
		}

		public void setDestinationId(int destinationId) {
			this.destinationId = destinationId;
		}

		public int getdBm() {
			return dBm;
		}

		public void setdBm(int dBm) {
			this.dBm = dBm;
		}

		public int getSecurityLevel() {
			return securityLevel;
		}

		public void setSecurityLevel(int securityLevel) {
			this.securityLevel = securityLevel;
		}

	}

}
