package org.osgi.impl.service.serial;

import org.osgi.service.serial.PortInUseException;
import org.osgi.service.serial.SerialConnection;
import org.osgi.service.serial.SerialDevice;

public class SerialDeviceImpl implements SerialDevice {

    private String comport;
    private Object obj = new Object();
    private String currentOwner = null;

    public SerialDeviceImpl(String comport) {
        this.comport = comport;
    }

    public SerialConnection open(String appname, int timeout) throws PortInUseException {

        synchronized (obj) {
            while (currentOwner != null) {
                try {
                    obj.wait(timeout);

                    if (currentOwner != null) {
                        throw new PortInUseException(currentOwner);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            currentOwner = appname;
            SerialDeviceManager.getInstance().modifySerialDevice(comport, currentOwner);

            return new SerialConnectionImpl(this);
        }
    }

    void close() {
        synchronized (obj) {
            if (currentOwner != null) {
                currentOwner = null;
                SerialDeviceManager.getInstance().modifySerialDevice(comport, currentOwner);

                obj.notify();
            }
        }
    }
}
