
package org.osgi.test.cases.enocean;

public final class Fixtures {

	public static final int	HOST_ID			= 0x12345678;
	public static final int	MANUFACTURER	= 0x6ea;
	public static final int	RORG			= 0xA5;
	public static final int	FUNC			= 0x02;
	public static final int	TYPE			= 0x01;

	public static final String	SELF_TEST_EVENT_TOPIC	= "org/osgi/service/enocean/EnOceanEvent/temperature_report";
	public static final String	SELF_TEST_EVENT_KEY		= "degrees";
	public static final String	SELF_TEST_EVENT_VALUE	= "33.04°F";
}
