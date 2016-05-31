package org.osgi.test.cases.zigbee;

import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.test.cases.zigbee.impl.ZCLFrameImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeDataInputImpl;

public class TestZCLFrame extends ZCLFrameImpl {

	public TestZCLFrame(ZCLHeader header, byte[] fullFrame) {

		super(header);
		data = fullFrame;
		zclHeader = header;
		isEmpty = false;
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return (byte[]) data.clone();
	}

	public ZigBeeDataInput getDataInput() {
		// TODO Auto-generated method stub
		return new ZigBeeDataInputImpl(this);
	}

}
