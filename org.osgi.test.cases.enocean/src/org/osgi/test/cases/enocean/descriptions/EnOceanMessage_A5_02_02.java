package org.osgi.test.cases.enocean.descriptions;


import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessageDescription;
import org.osgi.service.enocean.channels.EnOceanChannel;
import org.osgi.test.cases.enocean.utils.Utils;

public class EnOceanMessage_A5_02_02 implements EnOceanMessageDescription {

	public int getRorg() {
		return 0xA5;
	}

	public int getFunc() {
		return 0x02;
	}

	public int getType() {
		return 0x02;
	}

	EnOceanChannel	temperature	= new EnOceanChannel() {

									private byte	b0;

									public String getChannelId() {
										return "TMP_01";
		}

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

								};

	EnOceanChannel	learn		= new EnOceanChannel() {

									private boolean	isLearn;

									public String getChannelId() {
										return "LRN";
									}

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
								};

	public EnOceanChannel[] deserialize(byte[] data) throws EnOceanException, IllegalArgumentException {
		temperature.setRawValue(Utils.byteToBytes(data[2]));
		byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
		learn.setRawValue(new byte[] {lrnByte});
		return new EnOceanChannel[] {temperature, learn};
	}
}
