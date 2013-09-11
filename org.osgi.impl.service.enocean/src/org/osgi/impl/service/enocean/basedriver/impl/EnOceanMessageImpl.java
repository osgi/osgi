package org.osgi.impl.service.enocean.basedriver.impl;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.channels.EnOceanChannel;

public class EnOceanMessageImpl implements EnOceanMessage {

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setStatus(int status) {
		// TODO Auto-generated method stub

	}

	public byte[] serialize() throws EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object deserialize(byte[] bytes) throws EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}

	public EnOceanChannel[] getDatafields() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSubtelegramCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public EnOceanChannel[] getChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSubTelegramCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRedundancyInfo() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRSSI() {
		// TODO Auto-generated method stub
		return 0;
	}

}
