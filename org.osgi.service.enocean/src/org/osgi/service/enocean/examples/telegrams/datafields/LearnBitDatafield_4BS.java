package org.osgi.service.enocean.examples.telegrams.datafields;

import org.osgi.service.enocean.datafields.EnOceanDatafield;
import org.osgi.service.enocean.datafields.EnOceanDatafieldDescription;
import org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.BooleanDatafieldDescription;

public class LearnBitDatafield_4BS implements EnOceanDatafield {

	private Integer value;
	private static BooleanDatafieldDescription description;

	public String getShortcut() {
		return "LRNB";
	}

	public int getOffset() {
		return 28;
	}

	public int getSize() {
		return 1;
	}

	public String getName() {
		return "LRN bit - 4BS";
	}

	public EnOceanDatafieldDescription getDescription() {
		return description;
	}

	public Object getValue() {
		return (Object)value;
	}

	public void setValue(Object obj) {
		this.value = (Integer)obj ;
	}

	public byte[] getRawValue() {
		// FIXME Auto-generated method stub
		return null;
	}

	public void setRawValue(byte[] rawValue) {
		
	}

}
