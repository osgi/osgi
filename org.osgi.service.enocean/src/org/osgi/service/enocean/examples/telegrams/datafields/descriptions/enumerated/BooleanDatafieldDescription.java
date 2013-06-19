package org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated;

import java.nio.ByteBuffer;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.datafields.EnOceanDatafieldEnum;
import org.osgi.service.enocean.datafields.EnOceanEnumDatafieldDescription;
import org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.ranges.DatafieldEnum_False;
import org.osgi.service.enocean.examples.telegrams.datafields.descriptions.enumerated.ranges.DatafieldEnum_True;


public class BooleanDatafieldDescription implements EnOceanEnumDatafieldDescription {

	private static EnOceanDatafieldEnum[] values = { new DatafieldEnum_False(), new DatafieldEnum_True() };
	
	@Override
	public String getType() {
		return EnOceanEnumDatafieldDescription.TYPE_ENUM;
	}

	@Override
	public EnOceanDatafieldEnum[] getPossibleValues() {
		return values;
	}

	@Override
	public byte[] serialize(Object obj) {
		// FIXME Auto-generated method stub
		return null;
	}

	@Override
	public Object deserialize(byte[] bytes) throws EnOceanException  {
		int rawValue = ByteBuffer.wrap(bytes).getShort();
		if (rawValue != 0 && rawValue != 1) throw new EnOceanException("Wrong datafield value !");
		return values[rawValue];
	}

}
