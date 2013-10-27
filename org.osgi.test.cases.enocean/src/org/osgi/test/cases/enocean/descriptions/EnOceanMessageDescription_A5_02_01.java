package org.osgi.test.cases.enocean.descriptions;


import org.osgi.service.enocean.IllegalArgumentException;
import org.osgi.service.enocean.EnOceanMessageDescription;
import org.osgi.service.enocean.channels.EnOceanChannel;
import org.osgi.test.cases.enocean.channels.LearnChannel_4BS;
import org.osgi.test.cases.enocean.channels.TemperatureChannel_00;
import org.osgi.test.cases.enocean.utils.Utils;

public class EnOceanMessageDescription_A5_02_01 implements EnOceanMessageDescription {

	public int getRorg() {
		return 0xA5;
	}

	public int getFunc() {
		return 0x02;
	}

	public int getType() {
		return 0x01;
	}

	EnOceanChannel	temperature	= new TemperatureChannel_00();
	EnOceanChannel	learn		= new LearnChannel_4BS();

	public EnOceanChannel[] deserialize(byte[] data) throws IllegalArgumentException, IllegalArgumentException {

		temperature.setRawValue(Utils.byteToBytes(data[2]));
		byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
		learn.setRawValue(new byte[] {lrnByte});

		return new EnOceanChannel[] {temperature, learn};
	}
}
