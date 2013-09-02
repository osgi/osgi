package org.osgi.impl.service.enocean.basedriver;

import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanResponseHandler;

public class EnOceanDeviceImpl implements EnOceanDevice {

	private ServiceRegistration	sReg;

	/**
	 * An {@link EnOceanDeviceImpl} creation is directly related to its
	 * registration within the framework.
	 * Such a Device should only be registered after a proper teach-in procedure, so that the RORG, FIUNC and TYPE are already known.  
	 */
	public EnOceanDeviceImpl(BundleContext bc, int chipId, int rorg, int func, int type) {
		Hashtable deviceProps = new Hashtable();
		deviceProps.put(org.osgi.service.enocean.EnOceanDevice.CHIP_ID, new Integer(chipId));
		deviceProps.put(org.osgi.service.enocean.EnOceanDevice.RORG, new Integer(rorg));
		deviceProps.put(org.osgi.service.enocean.EnOceanDevice.FUNC, new Integer(func));
		deviceProps.put(org.osgi.service.enocean.EnOceanDevice.TYPE, new Integer(type));
		sReg = bc.registerService(new String[] {EnOceanDevice.class.getName()},
				this,
				deviceProps);
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

	public void send(byte[] telegram, EnOceanResponseHandler handler) throws EnOceanException {
		// TODO Auto-generated method stub
	}

	public void send(EnOceanMessage telegram, EnOceanResponseHandler handler) throws EnOceanException {
		// TODO Auto-generated method stub
	}

	public EnOceanMessage getLastMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getRPCs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProfileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSecurityLevelFormat() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	public void setProfileName(String profileName) {
		// TODO Auto-generated method stub

	}

	public void getSecurityLevelFormat(int securityLevel) {
		// TODO Auto-generated method stub

	}
	
}
