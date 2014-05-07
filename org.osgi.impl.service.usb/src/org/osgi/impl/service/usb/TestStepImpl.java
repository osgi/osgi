package org.osgi.impl.service.usb;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import org.osgi.service.usb.USBDevice;
import org.osgi.test.cases.step.TestStep;

public class TestStepImpl implements TestStep {

    public String[] execute(String command, String[] parameters) {

        if (Commands.REGISTER_DEVICE.equals(command)) {

            long id = registerNewDevice(parameters);
            return new String[]{Long.toString(id)};

        } else if (Commands.UNREGISTER_DEVICE.equals(command)) {

            unregisterNewDevice(parameters);
            return null;

        } else {
            throw new IllegalArgumentException("The user message is not supported.");
        }
    }

    /**
     * A method of USBDevice service registration.
     * <br>
     * Parameters are the below.<br>
     * <ul>
     * <li>index 0  :"USB.device.bcdUSB"
     * <li>index 1  :"USB.device.bDeviceClass"
     * <li>index 2  :"USB.device.bDeviceSubClass"
     * <li>index 3  :"USB.device.bDeviceProtocol"
     * <li>index 4  :"USB.device.idVendor"
     * <li>index 5  :"USB.device.idProduct"
     * <li>index 6  :"USB.device.bcdDevice"
     * <li>index 7  :"USB.device.iManufacturer"
     * <li>index 8  :"USB.device.iProduct"
     * <li>index 9 :"USB.device.iSerialNumber"
     * <li>index 10 :"USB.device.interfaceclasses"(Separator ",")
     * <li>index 11 :"USB.device.bus"
     * <li>index 12 :"USB.device.address"
     * <li>index 13 :"USB.device.comport"
     * <li>index 14 :"USB.device.mountpoints"
     * </ul>
     *
     */
    private long registerNewDevice(String[] parameters) {

        Dictionary prop = new Properties();

        // 5.1.2   Device Access Category
        List category = new ArrayList();
        category.add(USBDevice.DEVICE_CATEGORY);
        if (parameters[13] != null) {
            category.add(USBDevice.DEVICE_CATEGORY_SERIAL);
        }
        if (parameters[14] != null) {
            category.add(USBDevice.DEVICE_CATEGORY_MASSSTORAGE);
        }
        prop.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, (String[])category.toArray(new String[0]));

        // 5.1.3   Service properties from USB Specification
        prop.put(USBDevice.USB_RELEASE_NUMBER, Integer.decode(parameters[0]));
        prop.put(USBDevice.DEVICE_CLASS, Integer.decode(parameters[1]));
        prop.put(USBDevice.DEVICE_SUBCLASS, Integer.decode(parameters[2]));
        prop.put(USBDevice.DEVICE_PROTOCOL, Integer.decode(parameters[3]));
        prop.put(USBDevice.VID, Integer.decode(parameters[4]));
        prop.put(USBDevice.PID, Integer.decode(parameters[5]));
        prop.put(USBDevice.RELEASE_NUMBER, Integer.decode(parameters[6]));
        if (parameters[7] != null) {
            prop.put(USBDevice.MANUFACTURER, parameters[7]);
        }
        if (parameters[8] != null) {
            prop.put(USBDevice.PRODUCT, parameters[8]);
        }
        if (parameters[9] != null) {
            prop.put(USBDevice.SERIALNUMBER, parameters[9]);
        }
        prop.put(USBDevice.USB_CLASS, USBUtil.toIntArray(parameters[10]));

        // 5.1.4   Other Service properties
        prop.put(USBDevice.USB_BUS, Integer.valueOf(parameters[11]));
        prop.put(USBDevice.USB_ADDR, Integer.valueOf(parameters[12]));

        // 5.2.2   Optional Device Access Category
        if (parameters[13] != null) {
            prop.put(USBDevice.COM_PORT, parameters[13]);
        }

        // 5.3.3   Optional Service properties
        if (parameters[14] != null) {
            prop.put(USBDevice.MOUNTPOINTS, USBUtil.toStringArray(parameters[14]));
        }

        return USBTracker.getInstance().addUsb(prop);
    }

    private void unregisterNewDevice(String[] parameters) {

        USBTracker.getInstance().removedUsb(Long.parseLong(parameters[0]));
    }


}
