
package org.osgi.test.cases.enocean.descriptions;

import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.test.cases.enocean.channels.FloatChannel1;
import org.osgi.test.cases.enocean.channels.LChannel;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 *
 */
public class EnOceanMessageDescription2 implements EnOceanMessageDescription {

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
	 * @return hardcoded 0x01.
	 */
	public int getType() {
		return 0x01;
	}

	EnOceanChannel	floatValue	= new FloatChannel1();
	EnOceanChannel	l			= new LChannel();

	public EnOceanChannel[] deserialize(byte[] data) throws IllegalArgumentException {

		/* Every message description should ensure this */
		if (data == null) {
			throw new IllegalArgumentException("Input data was NULL");
		}
		if (data.length != 4) {
			throw new IllegalArgumentException("Input data size was wrong");
		}
		byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
		floatValue.setRawValue(Utils.byteToBytes(data[2]));
		l.setRawValue(new byte[] {lrnByte});

		return new EnOceanChannel[] {floatValue, l};
	}

	public byte[] serialize(EnOceanChannel[] channels) throws EnOceanException, EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}
}
