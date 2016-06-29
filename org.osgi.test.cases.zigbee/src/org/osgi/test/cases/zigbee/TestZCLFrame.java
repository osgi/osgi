
package org.osgi.test.cases.zigbee;

import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.test.cases.zigbee.mock.ZCLFrameImpl;
import org.osgi.test.cases.zigbee.mock.ZigBeeDataInputImpl;

public class TestZCLFrame extends ZCLFrameImpl {

	public TestZCLFrame(ZCLHeader header, byte[] fullFrame) {

		super(header);
		data = fullFrame;
		zclHeader = header;
		isEmpty = false;
	}

	public byte[] getBytes() {

		return (byte[]) data.clone();
	}

	public ZigBeeDataInput getDataInput() {

		return new ZigBeeDataInputImpl(this);
	}

}
