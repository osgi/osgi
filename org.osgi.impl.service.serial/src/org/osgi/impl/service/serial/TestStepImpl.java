package org.osgi.impl.service.serial;

import java.util.Properties;

import org.osgi.service.device.Constants;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.usb.USBSerialDevice;
import org.osgi.test.cases.step.TestStep;

public class TestStepImpl implements TestStep {

    public String[] execute(String command, String[] parameters) {
        if (Commands.ADD.equals(command)) {
            addSerialDevice(parameters);

        } else if (Commands.ADD_USB.equals(command)) {
            addUSBSerialDevice(parameters);

        } else if (Commands.REMOVE.equals(command)) {
            removeSerialDevice(parameters);

        } else {
            throw new IllegalArgumentException("The user message is not supported.");
        }

        return null;
    }

    /**
     * The method that adds the serial device.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0  : serial.comport
     * <li>index 1  : bus.type (permits the null)
     * </ul>
     * Based on an appointed parameter, registers the SerialDevice service.<br>
     *
     * @param parameters The parameter mentioned above
     */
    private void addSerialDevice(String[] parameters) {
        Properties prop = new Properties();
        prop.put(SerialDevice.SERIAL_COMPORT, parameters[0]);
        prop.put(SerialDevice.CURRENT_OWNER, SerialDevice.EMPTY_STRING);
        prop.put(Constants.DEVICE_CATEGORY, SerialDevice.DEVICE_CATEGORY);

        if (parameters[1] != null) {
            prop.put(SerialDevice.BUS_TYPE, parameters[1]);
        }

        SerialDeviceManager.getInstance().addSerialDevice(parameters[0], prop);
    }

    /**
     * The method that adds the serial device.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0  : serial.comport
     * <li>index 1  : usb.bus
     * <li>index 2  : usb.address
     * <li>index 3  : usb.bcdUSB (permits the null)
     * <li>index 4  : usb.bDeviceClass
     * <li>index 5  : usb.bDeviceSubClass
     * <li>index 6  : usb.bDeviceProtocol
     * <li>index 7  : usb.bMaxPacketSize0 (permits the null)
     * <li>index 8  : usb.idVendor
     * <li>index 9  : usb.idProduct
     * <li>index 10 : usb.bcdDevice
     * <li>index 11 : usb.Manufacturer (permits the null)
     * <li>index 12 : DEVICE_DESCRIPTION (permits the null)
     * <li>index 13 : DEVICE_SERIAL (permits the null)
     * <li>index 14 : usb.bNumConfigurations (permits the null)
     * <li>index 15 : usb.bInterfaceNumber
     * <li>index 16 : usb.bAlternateSetting (permits the null)
     * <li>index 17 : usb.bNumEndpoints (permits the null)
     * <li>index 18 : usb.bInterfaceClass
     * <li>index 19 : usb.bInterfaceSubClass
     * <li>index 20 : usb.bInterfaceProtocol
     * <li>index 21 : usb.Interface (permits the null)
     * </ul>
     * Based on an appointed parameter, registers the SerialDevice service.<br>
     *
     * @param parameters The parameter mentioned above
     */
    private void addUSBSerialDevice(String[] parameters) {
        Properties prop = new Properties();
        prop.put(SerialDevice.SERIAL_COMPORT, parameters[0]);
        prop.put(SerialDevice.CURRENT_OWNER, SerialDevice.EMPTY_STRING);
        prop.put(Constants.DEVICE_CATEGORY, SerialDevice.DEVICE_CATEGORY);

        prop.put(SerialDevice.BUS_TYPE, USBSerialDevice.BUS_TYPE_USB);
        prop.put(USBSerialDevice.USB_BUS, new Integer(parameters[1]));
        prop.put(USBSerialDevice.USB_ADDRESS, new Integer(parameters[2]));

        // Device Descriptor's
        if (parameters[3] != null) {
            prop.put(USBSerialDevice.USB_BCDUSB, parameters[3]);
        }
        prop.put(USBSerialDevice.USB_BDEVICECLASS, parameters[4]);
        prop.put(USBSerialDevice.USB_BDEVICESUBCLASS, parameters[5]);
        prop.put(USBSerialDevice.USB_BDEVICEPROTOCOL, parameters[6]);
        if (parameters[7] != null) {
            prop.put(USBSerialDevice.USB_BMAXPACKETSIZE0, new Integer(parameters[7]));
        }
        prop.put(USBSerialDevice.USB_IDVENDOR, parameters[8]);
        prop.put(USBSerialDevice.USB_IDPRODUCT, parameters[9]);
        prop.put(USBSerialDevice.USB_BCDDEVICE, parameters[10]);
        if (parameters[11] != null) {
            prop.put(USBSerialDevice.USB_MANUFACTURER, parameters[11]);
        }
        if (parameters[12] != null) {
            prop.put(Constants.DEVICE_DESCRIPTION, parameters[12]);
        }
        if (parameters[13] != null) {
            prop.put(Constants.DEVICE_SERIAL, parameters[13]);
        }
        if (parameters[14] != null) {
            prop.put(USBSerialDevice.USB_BNUMCONFIGURATIONS, new Integer(parameters[14]));
        }

        // Interface Descriptor's
        prop.put(USBSerialDevice.USB_BINTERFACENUMBER, new Integer(parameters[15]));
        if (parameters[16] != null) {
            prop.put(USBSerialDevice.USB_BALTERNATESETTING, new Integer(parameters[16]));
        }
        if (parameters[17] != null) {
            prop.put(USBSerialDevice.USB_BNUMENDPOINTS, new Integer(parameters[17]));
        }
        prop.put(USBSerialDevice.USB_BINTERFACECLASS, parameters[18]);
        prop.put(USBSerialDevice.USB_BINTERFACESUBCLASS, parameters[19]);
        prop.put(USBSerialDevice.USB_BINTERFACEPROTOCOL, parameters[20]);
        if (parameters[21] != null) {
            prop.put(USBSerialDevice.USB_INTERFACE, parameters[21]);
        }

        SerialDeviceManager.getInstance().addSerialDevice(parameters[0], prop);
    }

    /**
     * The method that removes the serial device.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0  : serial.comport
     * </ul>
     * Based on an appointed parameter, unregisters the SerialDevice service.<br>
     *
     * @param parameters The parameter mentioned above
     */
    private void removeSerialDevice(String[] parameters) {
        SerialDeviceManager.getInstance().removeSerialDevice(parameters[0]);
    }
}
