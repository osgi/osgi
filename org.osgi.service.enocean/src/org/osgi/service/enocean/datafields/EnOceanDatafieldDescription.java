package org.osgi.service.enocean.datafields;

import org.osgi.service.enocean.EnOceanException;

public interface EnOceanDatafieldDescription {
	
	public final static String TYPE_RAW = "enocean.datafield.description.raw";
	public final static String TYPE_SCALED = "enocean.datafield.description.scaled";
	public final static String TYPE_ENUM = "enocean.datafield.description.enum";
	
	public String getType();
	public byte[] serialize(Object obj) throws EnOceanException;
	public Object deserialize(byte[] bytes) throws EnOceanException;
}
