package org.osgi.service.enocean.examples.telegrams.datafields;

import org.osgi.service.enocean.datafields.EnOceanDatafield;
import org.osgi.service.enocean.datafields.EnOceanDatafieldDescription;
import org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.BooleanDatafieldDescription;

public class LearnBitDatafield_4BS implements EnOceanDatafield {

	private int value;
	private static BooleanDatafieldDescription description;
	
	@Override
	public String getShortcut() {
		return "LRNB";
	}

	@Override
	public int getOffset() {
		return 28;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String getName() {
		return "LRN bit - 4BS";
	}

	@Override
	public EnOceanDatafieldDescription getDescription() {
		return description;
	}

	@Override
	public Object getValue() {
		return (Object)value;
	}

	public void setValue(Object obj) {
		this.value = (int)obj ;
	}

	@Override
	public byte[] getRawValue() {
		// FIXME Auto-generated method stub
		return null;
	}

	@Override
	public void setRawValue(byte[] rawValue) {
		
	}

}
