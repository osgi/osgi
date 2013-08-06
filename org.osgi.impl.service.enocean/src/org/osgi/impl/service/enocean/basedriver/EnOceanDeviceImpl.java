package org.osgi.impl.service.enocean.basedriver;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessage;

public class EnOceanDeviceImpl implements EnOceanDevice {
	private EnOceanBaseDriver	basedriver;
	private Hashtable		services;
	private Properties		props;
	private String			devid;
	private String			devtype;
	private BundleContext	bc;

	public EnOceanDeviceImpl(EnOceanBaseDriver enOceanBaseDriver, Properties props, BundleContext bc) {
		// TODO Auto-generated constructor stub

	}

	public void send(byte[] telegram) throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public void send(EnOceanMessage telegram) throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public void setLearningMode(boolean learnMode) {
		// TODO Auto-generated method stub

	}

	public int getRollingCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setRollingCode(int rollingCode) {
		// TODO Auto-generated method stub

	}

	public byte[] getEncryptionKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEncryptionKey(byte[] key) {
		// TODO Auto-generated method stub

	}

	public EnOceanMessage getLastTelegram() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getLearnedDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getRPC() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
