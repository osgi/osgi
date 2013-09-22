package org.osgi.impl.service.enocean.basedriver.impl;

import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.channels.EnOceanChannel;
import org.osgi.service.enocean.channels.EnOceanChannelDescription;

public class EnOceanMessage_A5_02_01 extends EnOceanMessageImpl_4BS {

	public int getFunc() {
		return 0x02;
	}

	public int getType() {
		return 0x01;
	}

	public void deserialize(byte[] bytes) throws EnOceanException, IllegalArgumentException {
		EnOceanChannel temperature = new EnOceanChannel() {

			private byte	b0;

			public void setRawValue(byte[] rawValue) {
				b0 = rawValue[0];
			}

			public int getSize() {
				return 8;
			}

			public byte[] getRawValue() {
				return Utils.byteToBytes(b0);
			}

			public int getOffset() {
				return 16;
			}

			public EnOceanChannelDescription getDescription() {
				return null;
			}
		};

		EnOceanChannel learn = new EnOceanChannel() {

			private boolean	isLearn;

			public void setRawValue(byte[] rawValue) {
				isLearn = rawValue[0] == 0;
			}

			public int getSize() {
				return 1;
			}

			public byte[] getRawValue() {
				if (isLearn) {
					return new byte[] {0x0};
				} else {
					return new byte[] {0x1};
				}
			}

			public int getOffset() {
				return 28;
			}

			public EnOceanChannelDescription getDescription() {
				return null;
			}
		};

		temperature.setRawValue(Utils.byteToBytes(bytes[2]));
		byte lrnByte = (byte) ((bytes[3] >> 3) & 0x01);
		learn.setRawValue(new byte[] {lrnByte});
	}
}
