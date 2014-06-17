
package org.osgi.test.cases.enocean.descriptions;

import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.test.cases.enocean.channels.LearnChannel_4BS;
import org.osgi.test.cases.enocean.channels.TemperatureChannel_01;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 *
 */
public class EnOceanMessageDescription_A5_02_02 implements EnOceanMessageDescription {

	/**
	 * @return hardcoded 0xA5.
	 */
	public int getRorg() {
		return 0xA5;
	}

	/**
	 * @return hardcoded 0x02.
	 */
	public int getFunc() {
		return 0x02;
	}

	/**
	 * @return hardcoded 0x02.
	 */
	public int getType() {
		return 0x02;
	}

	EnOceanChannel	temperature	= new TemperatureChannel_01();
	EnOceanChannel	learn		= new LearnChannel_4BS();

	public EnOceanChannel[] deserialize(byte[] data) throws EnOceanException, EnOceanException {
		temperature.setRawValue(Utils.byteToBytes(data[2]));
		byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
		learn.setRawValue(new byte[] {lrnByte});
		return new EnOceanChannel[] {temperature, learn};
	}

	public byte[] serialize(EnOceanChannel[] channels) throws EnOceanException, EnOceanException {
		// TODO: implement
		return null;
	}
}
