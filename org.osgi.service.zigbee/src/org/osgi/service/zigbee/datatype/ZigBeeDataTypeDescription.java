package org.osgi.service.zigbee.datatype;

/**
 * This interface represents the ZigBee data type abstraction.
 *  
 * @version 1.0
 */
public interface ZigBeeDataTypeDescription {
	/**
	 * @return The data type identifier
	 */
	public short getId();
	
	/**
	 * @return The associated data type name string.
	 */
	public String getName();
	
	/**
	 * @return true, if the data type is analog.
	 */
	public boolean isAnalog();
	
	/**
	 * @return The corresponding Java type class.
	 */
	public Class getJavaDataType();
	
	/**
	 * @param param Object to be serialized using the associated type
	 * @return An array of bytes that represents the serialized value of param
	 */
	public byte[] serialize(Object param);
	
	/**
	 * @param data Array of bytes to be deserialized using associated type
	 * @return An object that represents the deserialized value of data
	 */
	public Object deserialize(byte[] data);
}
