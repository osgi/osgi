package org.osgi.service.enocean.examples.telegrams.datafields;

import org.osgi.service.enocean.datafields.EnOceanDatafield;
import org.osgi.service.enocean.datafields.EnOceanDatafieldDescription;
import org.osgi.service.enocean.examples.Helpers;
import org.osgi.service.enocean.examples.telegrams.datafields.descriptions.scaled.TemperatureRangeDatafield_30_10;


public class TemperatureEnOceanDatafield implements EnOceanDatafield {

	byte[] rawValue = null;
	private static TemperatureRangeDatafield_30_10 description;
	
	@Override
	public String getShortcut() {
		return "TMP";
	}

	@Override
	public int getOffset() {
		return 16;
	}

	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public String getName() {
		return "Temperature";
	}

	@Override
	public EnOceanDatafieldDescription getDescription() {
		return description;
	}

	@Override
	public byte[] getRawValue() {
		return rawValue;
	}
	
	@Override
	public void setRawValue(byte[] rawValue) {
		this.rawValue = rawValue;
	}

	@Override
	public Object getValue() {
		return (int)description.deserialize(rawValue);
	}

	@Override
	public void setValue(Object obj) {
		this.rawValue = description.serialize((float)obj);
	}
}
