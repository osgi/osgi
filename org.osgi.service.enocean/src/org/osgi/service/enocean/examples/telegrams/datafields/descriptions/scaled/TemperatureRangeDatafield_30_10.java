package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.scaled;

import org.osgi.service.enocean.datafields.EnOceanDatafieldDescription;
import java.nio.ByteBuffer;
import org.osgi.service.enocean.datafields.EnOceanScaledDatafieldDescription;
import org.osgi.service.enocean.examples.Helpers;


public class TemperatureRangeDatafield_30_10 implements EnOceanScaledDatafieldDescription {

	@Override
	public String getType() {
		return EnOceanDatafieldDescription.TYPE_SCALED;
	}

	@Override
	public int getRangeStart() {
		return 255;
	}

	@Override
	public int getRangeStop() {
		return 0;
	}

	@Override
	public double getScaleStart() {
		return (float) -30.0;
	}

	@Override
	public double getScaleStop() {
		return (float) 10.0;
	}

	@Override
	public String getUnit() {
		return "°C";
	}

	@Override
	public Object deserialize(byte[] bytes) {
		int rawValue = ByteBuffer.wrap(bytes).getShort();
		double q = (rawValue - getRangeStart()) / (getRangeStop() - getRangeStart());
		return (Object)(getScaleStart() + q * (getScaleStop() - getScaleStart()));
	}

	@Override
	public byte[] serialize(Object obj) {
		double value = (double)obj;
		double q = (value - getScaleStart()) / (getScaleStop() - getScaleStart());
		Double rawValue = (getRangeStart() + q * (getRangeStop() - getRangeStart()));
		return Helpers.shortToByteArray(rawValue.intValue());
	}

}
