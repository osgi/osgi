package org.osgi.impl.service.enocean.basedriver;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.channels.EnOceanChannel;

public class EnOceanTelegramImpl implements EnOceanMessage {
	private EnOceanBaseDriver	basedriver;

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

}
