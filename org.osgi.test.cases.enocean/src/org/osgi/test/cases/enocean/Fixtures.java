
package org.osgi.test.cases.enocean;

import org.osgi.service.enocean.channels.EnOceanChannelDescription;

public final class Fixtures {

	public static final int	HOST_ID			= 0x12345678;
	public static final int	MANUFACTURER	= 0x6ea;
	public static final int	RORG			= 0xA5;
	public static final int	FUNC			= 0x02;
	public static final int		TYPE_1					= 0x01;
	public static final int		TYPE_2					= 0x02;

	public static final String	STR_HOST_ID				= String.valueOf(HOST_ID);
	public static final String	STR_MANUFACTURER		= String.valueOf(MANUFACTURER);
	public static final String	STR_RORG				= String.valueOf(RORG);
	public static final String	STR_FUNC				= String.valueOf(FUNC);
	public static final String	STR_TYPE_1				= String.valueOf(TYPE_1);
	public static final String	STR_TYPE_2				= String.valueOf(TYPE_2);

	public static final String	SELF_TEST_EVENT_TOPIC	= "org/osgi/service/enocean/EnOceanEvent/MESSAGE_RECEIVED";
	public static final String	SELF_TEST_EVENT_KEY		= "degrees";
	public static final String	SELF_TEST_EVENT_VALUE	= "33.04°F";

	public static final float	TEMPERATURE				= -20.0f;
	public static final byte	RAW_TEMPERATURE			= 0x7f;
	public static final String	TMP_CHANNEL_ID			= "TMP_00";
	public static final String	TMP_CHANNEL_TYPE		= EnOceanChannelDescription.TYPE_DATA;
}
