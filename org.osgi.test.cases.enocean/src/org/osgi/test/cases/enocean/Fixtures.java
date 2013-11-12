
package org.osgi.test.cases.enocean;

import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.test.cases.enocean.devices.TemperatureSensingDevice;

public final class Fixtures {

	public static final int	HOST_ID			= 0x12345678;
	public static final int	MANUFACTURER	= 0x6ea;
	public static final int	RORG			= 0xA5;
	public static final int		RORG_RPS				= 0xF6;
	public static final int	FUNC			= 0x02;
	public static final int		TYPE_1					= 0x01;
	public static final int		TYPE_2					= 0x02;

	public static final String	STR_HOST_ID				= String.valueOf(HOST_ID);
	public static final String	STR_MANUFACTURER		= String.valueOf(MANUFACTURER);
	public static final String	STR_RORG				= String.valueOf(RORG);
	public static final String	STR_FUNC				= String.valueOf(FUNC);
	public static final String	STR_TYPE_1				= String.valueOf(TYPE_1);
	public static final String	STR_TYPE_2				= String.valueOf(TYPE_2);

	public static final String	SELF_TEST_EVENT_TOPIC	= EnOceanEvent.TOPIC_MSG_RECEIVED;
	public static final String	SELF_TEST_EVENT_KEY		= "degrees";
	public static final String	SELF_TEST_EVENT_VALUE	= "33.04°F";

	public static final float	TEMPERATURE				= -20.0f;
	public static final byte	RAW_TEMPERATURE			= 0x7f;
	public static final String	TMP_CHANNEL_ID			= "TMP_00";
	public static final String	TMP_CHANNEL_TYPE		= EnOceanChannelDescription.TYPE_DATA;
	public static final String	DEVICE_PID				= "my_exported_unique_device";
	public static final Object	STR_RORG_RPS			= String.valueOf(RORG_RPS);

	public static ServiceRegistration registerDevice(BundleContext bc) {
		EnOceanDevice device = new TemperatureSensingDevice();
		Dictionary props = new Properties();
		props.put(EnOceanDevice.ENOCEAN_EXPORT, Boolean.TRUE);
		props.put(Constants.SERVICE_PID, Fixtures.DEVICE_PID);
		props.put(EnOceanDevice.RORG, Fixtures.STR_RORG);
		props.put(EnOceanDevice.FUNC, Fixtures.STR_FUNC);
		props.put(EnOceanDevice.TYPE, Fixtures.STR_TYPE_1);
		props.put(EnOceanDevice.MANUFACTURER, Fixtures.STR_MANUFACTURER);
		return bc.registerService(EnOceanDevice.class.getName(), device, props);
	}

}
