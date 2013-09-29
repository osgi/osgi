package org.osgi.test.cases.enocean.descriptions;


import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.channels.EnOceanChannel;
import org.osgi.service.enocean.channels.EnOceanChannelDescription;
import org.osgi.test.cases.enocean.utils.Utils;

public class EnOceanMessage_A5_02_01 implements EnOceanMessage {

	private EnOceanChannel[]	channels;
	private int					senderId;
	private int					status;
	private int					subTelegramCount;
	private int					redundancyInfo;
	private int					rssi;

	public int getRorg() {
		return 0xA5;
	}

	public int getFunc() {
		return 0x02;
	}

	public int getType() {
		return 0x01;
	}

	public byte[] serialize() throws EnOceanException {
		byte[] out = new byte[4];
		for (int i = 0; i < channels.length; i++) {
			EnOceanChannel c = channels[i];
			// TODO: Finish the serialization here
		}
		return null;
	}

	public void deserialize(byte[] bytes) throws EnOceanException, IllegalArgumentException {

		if (bytes[0] != (byte) 0xA5) {
			throw new IllegalArgumentException("bytes could not be decoded into a " + this.getClass().getName() + " instance");
		}
		
		byte[] data = Utils.byteRange(bytes, 1, 4);
		senderId = Utils.bytes2intLE(bytes, 5, 4);
		status = Utils.bytes2intLE(bytes, 9, 1);
		
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

		temperature.setRawValue(Utils.byteToBytes(data[2]));
		byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
		learn.setRawValue(new byte[] {lrnByte});

		this.channels = new EnOceanChannel[] {temperature, learn};
	}

	public int getSenderId() {
		return senderId;
	}

	public int getDestinationId() {
		return -1;
	}

	public EnOceanChannel[] getChannels() {
		return channels;
	}

	public int getStatus() {
		return status;
	}

	public int getSubTelegramCount() {
		return subTelegramCount;
	}

	public int getRedundancyInfo() {
		return redundancyInfo;
	}

	public int getRSSI() {
		return rssi;
	}
}
