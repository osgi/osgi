
package org.osgi.impl.service.enocean.basedriver.impl;

import java.util.Map;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;
import org.osgi.impl.service.enocean.basedriver.radio.MessageSYS_EX;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.device.Constants;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHandler;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanRPC;

/**
 * EnOcean device implementation.
 */
public class EnOceanDeviceImpl implements EnOceanDevice {

	private static final String	TAG	= EnOceanDeviceImpl.class.getName();
	private BundleContext		bc;
	private ServiceRegistration	sReg;

	private Properties			props;
	private EnOceanMessage		lastMessage;
	private EnOceanBaseDriver	driver;
	private int					chip_id;

	/**
	 * An {@link EnOceanDeviceImpl} creation is directly related to its
	 * registration within the framework. Such a Device should only be
	 * registered after a proper teach-in procedure, so that the RORG, FIUNC and
	 * TYPE are already known.
	 * 
	 * @param bundleContext
	 * @param driver
	 * @param uid
	 * @param rorg
	 */
	public EnOceanDeviceImpl(BundleContext bundleContext, EnOceanBaseDriver driver, int uid, int rorg) {
		this.bc = bundleContext;
		this.driver = driver;
		this.chip_id = uid;
		props = new Properties();
		props.put(Constants.DEVICE_CATEGORY, EnOceanDevice.DEVICE_CATEGORY);
		props.put(EnOceanDevice.CHIP_ID, String.valueOf(uid));
		props.put(EnOceanDevice.RORG, String.valueOf(rorg));
		sReg = this.bc.registerService(EnOceanDevice.class.getName(), this, props);
		Logger.d(TAG, "registering EnOceanDevice : " + Utils.bytesToHexString(Utils.intTo4Bytes(uid)));
		/* Initializations */
		lastMessage = null;
	}

	/**
	 * @return the base driver's properties.
	 */
	public Properties getServiceProperties() {
		return props;
	}

	/**
	 * @param func
	 * @param type
	 * @param manuf
	 */
	public void registerProfile(int func, int type, int manuf) {
		props.put(EnOceanDevice.FUNC, String.valueOf(func));
		props.put(EnOceanDevice.TYPE, String.valueOf(type));
		props.put(EnOceanDevice.MANUFACTURER, String.valueOf(manuf));
		sReg.setProperties(props);
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

	/**
	 * @return the last telegram as an EnOceanMessage.
	 */
	public EnOceanMessage getLastTelegram() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getLearnedDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the RPC.
	 */
	public Map getRPC() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param message
	 * @param handler
	 * @throws EnOceanException
	 */
	public void send(EnOceanMessage message, EnOceanHandler handler) throws EnOceanException {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the las message as an EnOceanMessage.
	 */
	public EnOceanMessage getLastMessage() {
		return lastMessage;
	}

	public Map getRPCs() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the profile name.
	 */
	public String getProfileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSecurityLevelFormat() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param profileName
	 */
	public void setProfileName(String profileName) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param securityLevel
	 */
	public void getSecurityLevelFormat(int securityLevel) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param rorg
	 */
	public void setRorg(int rorg) {
		props.put(EnOceanDevice.RORG, String.valueOf(rorg));
	}

	public void setFunc(int func) {
		props.put(EnOceanDevice.FUNC, String.valueOf(func));
		sReg.setProperties(props);
	}

	public void setType(int type) {
		props.put(EnOceanDevice.TYPE, String.valueOf(type));
		sReg.setProperties(props);
	}

	/**
	 * @param manuf
	 */
	public void setManufacturer(int manuf) {
		props.put(EnOceanDevice.MANUFACTURER, String.valueOf(manuf));
		sReg.setProperties(props);
	}

	public int getChipId() {
		return chip_id;
	}

	public int getRorg() {
		return getIntProperty(EnOceanDevice.RORG);
	}

	public int getFunc() {
		return getIntProperty(EnOceanDevice.FUNC);
	}

	public int getType() {
		return getIntProperty(EnOceanDevice.TYPE);
	}

	public int getManufacturer() {
		return getIntProperty(EnOceanDevice.MANUFACTURER);
	}

	/**
	 * @param msg
	 */
	public void setLastMessage(EnOceanMessage msg) {
		lastMessage = msg;
	}

	/**
	 * Safe function to get an integer property
	 * 
	 * @param key
	 * @return the int-converted property, or -1
	 */
	private int getIntProperty(String key) {
		try {
			String s = (String) props.get(key);
			return Integer.parseInt(s);
		} catch (Exception e) {
			return -1;
		}
	}

	public void invoke(EnOceanRPC rpc, EnOceanHandler handler) throws IllegalArgumentException {
		// Generate the SYS_EX message relative to the RPC
		MessageSYS_EX msg = new MessageSYS_EX(rpc);
		for (int i = 0; i < msg.getSubTelNum(); i++) {
			byte[] telegram = (byte[]) msg.getTelegrams().get(i);
			driver.send(telegram);
		}
	}

	public void remove() {
		try {
			sReg.unregister();
		} catch (IllegalStateException e) {
			Logger.e(TAG, "attempt to unregister a device twice : " + e.getMessage());
		}
	}

}
