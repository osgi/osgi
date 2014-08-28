package org.osgi.impl.service.serial;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.serial.SerialDevice;

final class SerialDeviceManager {

    private static SerialDeviceManager instance = new SerialDeviceManager();

    private Map propMap = new HashMap();
    private Map regMap = new HashMap();

    private SerialDeviceManager() {
    }

    static SerialDeviceManager getInstance() {
        return instance;
    }

    void addSerialDevice(String comport, Properties prop) {
        SerialDevice service = new SerialDeviceImpl(comport);
        ServiceRegistration reg = Activator.getContext().registerService(SerialDevice.class.getName(), service, prop);

        propMap.put(comport, prop);
        regMap.put(comport, reg);
    }

    void modifySerialDevice(String comport, String currentOwner) {
        Properties prop = (Properties) propMap.get(comport);
        if (currentOwner == null) {
            prop.put(SerialDevice.CURRENT_OWNER, SerialDevice.EMPTY_STRING);
        } else {
            prop.put(SerialDevice.CURRENT_OWNER, currentOwner);
        }

        ServiceRegistration reg = (ServiceRegistration) regMap.get(comport);
        reg.setProperties(prop);
    }

    void removeSerialDevice(String comport) {
        ServiceRegistration reg = (ServiceRegistration) regMap.remove(comport);
        if (reg != null) {
            reg.unregister();
        }

        propMap.remove(comport);
    }
}
