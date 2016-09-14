
package org.osgi.test.cases.zigbee.config.file;

import java.math.BigInteger;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

public class ZigBeeHostConfig extends ZigBeeNodeConfig {

	public ZigBeeHostConfig(String hostPid, int panId, int channel, int securityLevel, BigInteger IEEEAddress, ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc,
			String userdescription) {
		super(IEEEAddress, null, nodeDesc, powerDesc, userdescription);
	}
}
