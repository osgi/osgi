package org.osgi.service.enocean.examples.telegrams.datafields;

import org.osgi.service.enocean.datafields.EnOceanDatafield;
import org.osgi.service.enocean.datafields.EnOceanDatafieldDescription;
import org.osgi.service.enocean.examples.telegrams.datafields.descriptions.scaled.TemperatureRangeDatafield_30_10;


public class TemperatureEnOceanDatafield implements EnOceanDatafield {

	byte[] rawValue = null;
	private static TemperatureRangeDatafield_30_10 description;

	public String getShortcut() {
		return "TMP";
	}

	public int getOffset() {
		return 16;
	}

	public int getSize() {
		return 8;
	}

	public String getName() {
		return "Temperature";
	}

	public EnOceanDatafieldDescription getDescription() {
		return description;
	}

	public byte[] getRawValue() {
		return rawValue;
	}

	public void setRawValue(byte[] rawValue) {
		this.rawValue = rawValue;
	}

	public Object getValue() {
		return (Object)description.deserialize(rawValue);
	}

	public void setValue(Object obj) {
		this.rawValue = description.serialize((Float)obj);
	}
}
