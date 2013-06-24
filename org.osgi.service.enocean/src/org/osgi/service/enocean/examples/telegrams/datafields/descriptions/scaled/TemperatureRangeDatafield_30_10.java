package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.scaled;

import org.osgi.service.enocean.datafields.EnOceanDatafieldDescription;
import java.nio.ByteBuffer;
import org.osgi.service.enocean.datafields.EnOceanScaledDatafieldDescription;
import org.osgi.service.enocean.examples.Helpers;


public class TemperatureRangeDatafield_30_10 implements EnOceanScaledDatafieldDescription {

	public String getType() {
		return EnOceanDatafieldDescription.TYPE_SCALED;
	}

	public int getRangeStart() {
		return 255;
	}

	public int getRangeStop() {
		return 0;
	}


	public double getScaleStart() {
		return (float) -30.0;
	}

	public double getScaleStop() {
		return (float) 10.0;
	}

	public String getUnit() {
		return "°C";
	}

	public Object deserialize(byte[] bytes) {
		int rawValue = ByteBuffer.wrap(bytes).getShort();
		double q = (rawValue - getRangeStart()) / (getRangeStop() - getRangeStart());
		double r = (getScaleStart() + q * (getScaleStop() - getScaleStart()));
		return new Double(r);
	}

	public byte[] serialize(Object obj) {
		Double value = (Double)obj;
		double q = (value.doubleValue() - getScaleStart()) / (getScaleStop() - getScaleStart());
		double rawValue = (getRangeStart() + q * (getRangeStop() - getRangeStart()));
		return Helpers.shortToByteArray(new Double(rawValue).intValue());
	}

}
