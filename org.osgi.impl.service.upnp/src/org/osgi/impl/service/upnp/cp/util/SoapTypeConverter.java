package org.osgi.impl.service.upnp.cp.util;

public class SoapTypeConverter {
	public static TypeConverter	UI1_Converter			= new IntegerConverter(
																SoapTypeConstants.SOAP_TYPE_UI1);
	public static TypeConverter	UI2_Converter			= new IntegerConverter(
																SoapTypeConstants.SOAP_TYPE_UI2);
	public static TypeConverter	UI4_Converter			= new LongConverter(
																SoapTypeConstants.SOAP_TYPE_UI4);
	public static TypeConverter	I1_Converter			= new IntegerConverter(
																SoapTypeConstants.SOAP_TYPE_I1);
	public static TypeConverter	I2_Converter			= new IntegerConverter(
																SoapTypeConstants.SOAP_TYPE_I2);
	public static TypeConverter	I4_Converter			= new IntegerConverter(
																SoapTypeConstants.SOAP_TYPE_I4);
	public static TypeConverter	INT_Converter			= new IntegerConverter(
																SoapTypeConstants.SOAP_TYPE_INT);
	public static TypeConverter	R4_Converter			= new FloatConverter(
																SoapTypeConstants.SOAP_TYPE_R4);
	public static TypeConverter	R8_Converter			= new DoubleConverter(
																SoapTypeConstants.SOAP_TYPE_R8);
	public static TypeConverter	NUMBER_Converter		= new DoubleConverter(
																SoapTypeConstants.SOAP_TYPE_NUMBER);
	public static TypeConverter	FIXED_14_4_Converter	= new Fixed144Converter(
																SoapTypeConstants.SOAP_TYPE_FIXED_14_4);
	public static TypeConverter	FLOAT_Converter			= new FloatConverter(
																SoapTypeConstants.SOAP_TYPE_FLOAT);
	public static TypeConverter	CHAR_Converter			= new CharacterConverter(
																SoapTypeConstants.SOAP_TYPE_CHAR);
	public static TypeConverter	STRING_Converter		= new StringConverter(
																SoapTypeConstants.SOAP_TYPE_STRING);
	public static TypeConverter	DATE_Converter			= new ISO8601Converter(
																SoapTypeConstants.SOAP_TYPE_DATE);
	//    public static TypeConverter DATETIME_Converter = 15;
	//    public static TypeConverter DATETIME_TZ_Converter = 16;
	public static TypeConverter	TIME_Converter			= new LongConverter(
																SoapTypeConstants.SOAP_TYPE_TIME);
	public static TypeConverter	TIME_TZ_Converter		= new LongConverter(
																SoapTypeConstants.SOAP_TYPE_TIME_TZ);
	public static TypeConverter	BOOLEAN_Converter		= new BooleanConverter(
																SoapTypeConstants.SOAP_TYPE_BOOLEAN);
	//    public static TypeConverter BIN_BASE64_Converter = 20;
	public static TypeConverter	BIN_HEX_Converter		= new BinHexConverter(
																SoapTypeConstants.SOAP_TYPE_BIN_HEX);
	public static TypeConverter	URI_Converter			= new StringConverter(
																SoapTypeConstants.SOAP_TYPE_URI);			// it
	// does
	// not
	// verify
	// whether
	// URI
	// is
	// correct
	public static TypeConverter	UUID_Converter			= new StringConverter(
																SoapTypeConstants.SOAP_TYPE_UUID);			// it
	// does
	// not
	// verify
	// whether
	// UUID
	// is
	// correct
}
