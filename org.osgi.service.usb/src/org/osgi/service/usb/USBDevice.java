package org.osgi.service.usb;

/**
 * Represents a USB device. For each USB device, an object is registered with
 * the framework under the USBDevice interface. A USB base driver must implement
 * this interface.
 * 
 * The values of the USB property names are defined by the USB Implementers
 * Forum, Inc.
 * 
 * The package name is org.osgi.service.usb.
 * 
 */
public interface /* org.osgi.service.usb. */USBDevice {

	/**
	 * MANDATORY property. The value is "USB".
	 * 
	 * Constant for the value of the service property DEVICE_CATEGORY used for
	 * all USB devices. A USB base driver bundle must set this property key.
	 * 
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	static final String DEVICE_CATEGORY = "USB";

	/**
	 * OPTIONAL property key. Value is "USB.device.bcdUSB".
	 * 
	 * The value is String, the 4-digit BCD format. Example: "0210"
	 */
	static final String USB_RELEASE_NUMBER = "USB.device.bcdUSB";

	/**
	 * MANDATORY property key. Value is "USB.device.bDeviceClass".
	 * 
	 * The value is String, hexadecimal, 2-digits. Example: "ff"
	 */
	static final String DEVICE_CLASS = "USB.device.bDeviceClass";

	/**
	 * MANDATORY property key. Value is "USB.device.bDeviceSubClass".
	 * 
	 * The value is String, hexadecimal, 2-digits. Example: "ff"
	 */
	static final String DEVICE_SUBCLASS = "USB.device.bDeviceSubClass";

	/**
	 * MANDATORY property key. Value is "USB.device.bDeviceProtocol".
	 * 
	 * The value is String, hexadecimal, 2-digits. Example: "ff"
	 */
	static final String DEVICE_PROTOCOL = "USB.device.bDeviceProtocol";

	/**
	 * OPTIONAL property key. Value is "USB.device.bMaxPacketSize0".
	 * 
	 * The value is Integer.
	 */
	static final String DEVICE_MAXPACETSIZE0 = "USB.device.bMaxPacketSize0";
	
	/**
	 * MANDATORY property key. Value is "USB.device.idVendor".
	 * 
	 * The value is String, hexadecimal, 4-digits. Example: "0403"
	 */
	static final String VID = "USB.device.idVendor";

	/**
	 * MANDATORY property key. Value is "USB.device.idProduct".
	 * 
	 * The value is String, hexadecimal, 4-digits. Example: "8372"
	 */
	static final String PID = "USB.device.idProduct";

	/**
	 * MANDATORY property key. Value is "USB.device.bcdDevice".
	 * 
	 * The value is String, the 4-digit BCD format. Example: "0200"
	 */
	static final String RELEASE_NUMBER = "USB.device.bcdDevice";

	/**
	 * OPTIONAL property key. Value is "iManufacturer".
	 * 
	 * The value is String of indicated in iManufacturer. (The value is not the
	 * index.) Example: "Buffalo Inc."
	 */
	static final String MANUFACTURER = "USB.device.iManufacturer";

	/**
	 * OPTIONAL property key. Value is "iProduct".
	 * 
	 * The value is String of indicated in iProduct. (The value is not the
	 * index.) Example: "USB2.0 PC Camera"
	 */
	static final String PRODUCT = "USB.device.iProduct";

	/**
	 * OPTIONAL property key. Value is "USB.device.iSerialNumber".
	 * 
	 * The value is String of indicated in iSerialNumber. (The value is not the
	 * index.) Example: "57B0002600000001"
	 */
	static final String SERIALNUMBER = "USB.device.iSerialNumber";

	/**
	 * OPTIONAL property key. Value is "USB.device.bNumConfigurations".
	 * 
	 * The value is Integer.
	 */
	static final String NUM_CONFIGURATIONS = "USB.device.bNumConfigurations";

	/**
	 * MANDATORY property key. Value is "USB.device.bInterfaceNumber".
	 * 
	 * The value is Integer.
	 */
	static final String INTERFACE_NUMBER = "USB.device.bInterfaceNumber";
	
	/**
	 * OPTIONAL property key. Value is "USB.device.bAlternateSetting".
	 * 
	 * The value is Integer.
	 */
	static final String ALTERNATE_SETTING = "USB.device.bAlternateSetting";
	
	/**
	 * OPTIONAL property key. Value is "USB.device.bAlternateSetting".
	 * 
	 * The value is Integer.
	 */
	static final String NUM_ENDPOINTS = "USB.device.bAlternateSetting";
	
	
	/**
	 * MANDATORY property key. Value is "USB.device.bInterfaceClass".
	 * 
	 * The value is String, hexadecimal, 2-digits. Example: "ff"
	 */
	static final String INTERFACE_CLASS = "USB.device.bInterfaceClass";

	/**
	 * MANDATORY property key. Value is "USB.device.bInterfaceSubClass".
	 * 
	 * The value is String, hexadecimal, 2-digits. Example: "ff"
	 */
	static final String INTERFACE_SUBCLASS = "USB.device.bInterfaceSubClass";

	/**
	 * MANDATORY property key. Value is "USB.device.bInterfaceProtocol".
	 * 
	 * The value is String, hexadecimal, 2-digits. Example: "ff"
	 */
	static final String INTERFACE_PROTOCOL = "USB.device.bInterfaceProtocol";
	
	
	/**
	 * OPTIONAL property key. Value is "USB.device.iInterface".
	 * 
	 * The value is String of indicated in iInterface. (The value is not the
	 * index.)
	 */
	static final String INTERFACE_DESCRIPTION = "USB.device.iInterface";

	
	
	/**
	 * MANDATORY property key. Value is "USB.device.bus".
	 * 
	 * The value is Integer. Used to identify USB devices with same VID / PID.
	 * The value is the ID of the USB bus assigned when connecting the USB
	 * device. USB bus ID is integer.
	 * 
	 * The USB bus ID does not change while the USB device remains connected.
	 * 
	 * Example: 3
	 */
	static final String USB_BUS = "USB.device.bus";

	/**
	 * MANDATORY property key. Value is "USB.device.address".
	 * 
	 * The value is Integer. Used to identify USB devices with same VID / PID.
	 * The value is the ID of the USB address assigned when connecting the USB
	 * device. USB address is integer (001-127).
	 * 
	 * The USB address does not change while the USB device remains connected.
	 * 
	 * Example: 2
	 */
	static final String USB_ADDR = "USB.device.address";

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * USB.device.idVendor and USB.device.idProduct. Value is 10.
	 */
	int MATCH_MODEL = 10;

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * USB.device.bDeviceClass, USB.device.bDeviceSubClass and
	 * USB.device.bDeviceProtocol, or a match with bInterfaceClass,
	 * bInterfaceSubClass and bInterfaceProtocol in one of
	 * USB.device.interfaceclasses. Value is 7.
	 */
	int MATCH_PROTOCOL = 7;

	/**
	 * Constant for the USB device match scale, indicating a
	 * matchUSB.device.bDeviceClass and USB.device.bDeviceSubClass, or a match
	 * with bInterfaceClass and bInterfaceSubClass in one of
	 * USB.device.interfaceclasses. Value is 5.
	 */
	int MATCH_SUBCLASS = 5;

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * USB.device.bDeviceClass, or a match with bInterfaceClass in one of
	 * USB.device.interfaceclasses. Value is 3.
	 */
	int MATCH_CLASS = 3;

	/**
	 * OPTIONAL Property. The value is "Serial".
	 * 
	 * Constant for the value of the service property DEVICE_CATEGORY used for a
	 * USB device which has a serial communication function such as a USB
	 * dongle. The USB base driver bundle must set this property key and
	 * serial.comport property. This device category's value may be used
	 * independently of USB. This value is defined because some USB devices
	 * have a serial communication function.
	 * 
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	static final String DEVICE_CATEGORY_SERIAL = "Serial";

	/**
	 * OPTIONAL Property key. Value is "serial.comport".
	 * 
	 * The property value is String. The USB Device has a serial communication
	 * function, set the value that represents the COM port. If the USB device
	 * does not have a serial communication function, this key and value are not
	 * set. The driver can communicate through Java Communications API with this
	 * value. Set this value "portName" of
	 * javax.comm.CommPortIdentifier#getPortIdentifier(String portName). Then
	 * serial communication is possible. Serial.comport value's format must be
	 * equal to the "portName" format. If a USB base driver set this property,
	 * USBDevice.DEVICE_CATEGORY_SERIAL must be set to DEVICE_CATEGORY.
	 * 
	 * Example1: "/dev/ttyUSB0". Example2: "COM5". Example3:
	 * "/dev/tty.usbserial-XXXXXX".
	 * 
	 * 
	 */
	static final String COM_PORT = "serial.comport";

	/**
	 * OPTIONAL Property. The value is "MassStorage".
	 * 
	 * Constant for the value of the service property DEVICE_CATEGORY used for a
	 * USB device which is a MassStorage Class in USB Specification such as a
	 * USB storage. Such a USB base driver bundle must set this property key and
	 * massstorage.mountpoints property while the device is mounted.
	 * 
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	static final String DEVICE_CATEGORY_MASSSTORAGE = "MassStorage";

	/**
	 * OPTIONAL Property key. Value is "massstorage.mountpoints".
	 * 
	 * The property value is String[]. If the USB device is Mass Storage Class,
	 * set the value that represents the mount point (a path to the USB storage)
	 * in OS. If the USB device is not Mass Storage Class, this key and value are
	 * not set. The driver can read and write the USB storage through standard
	 * File API with this value. Set this value "pathname" of
	 * java.io.File(String pathname). Then file access is possible.
	 * Massstorage.mountpoints's format must be equal to the "pathname" format.
	 * If a USB base driver set this property,
	 * USBDevice.DEVICE_CATEGORY_MASSSTORAGE must be set to DEVICE_CATEGORY.
	 * 
	 * Example1: {"/mnt/media/usb-storage-01/"}. Example2: {"D:\\Java"}.
	 */
	static final String MOUNTPOINTS = "massstorage.mountpoints";
}
