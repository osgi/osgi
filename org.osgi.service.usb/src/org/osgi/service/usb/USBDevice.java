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
	 * MANDATORY property key. Value is "USB".
	 * 
	 * Constant for the value of the service property DEVICE_CATEGORY used for
	 * all USB devices. A USB base driver bundle must set this property key.
	 * 
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	static final String DEVICE_CATEGORY = "USB";

	/**
	 * OPTIONAL Property key. The value is "USBSerial".
	 * 
	 * Constant for the value of the service property DEVICE_CATEGORY used for a
	 * USB device which has a serial communication function such as a USB
	 * dongle. Such a USB base driver bundle must set this property key and
	 * USB.device.comport property.
	 * 
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	static final String DEVICE_CATEGORY_USBSERIAL = "USBSerial";

	/**
	 * OPTIONAL Property key. The value is "MassStorage".
	 * 
	 * Constant for the value of the service property DEVICE_CATEGORY used for a
	 * USB device which is a MassStorage Class in USB Specification such as a
	 * USB storage. Such a USB base driver bundle must set this property key and
	 * USB.device.mountpoint property.
	 * 
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	static final String DEVICE_CATEGORY_MASSSTORAGE = "MassStorage";

	/**
	 * MANDATORY property key. Value is "USB.device.bcdUSB".
	 * 
	 * The value is string of decimal number, 4-digits, converted from the
	 * 4-digit BCD format. Example: "0210"
	 * 
	 */
	static final String USB_RELEASE_NUMBER = "USB.device.bcdUSB";

	/**
	 * MANDATORY property key. Value is "USB.device.bDeviceClass".
	 * 
	 * The value is hexadecimal, 2-digits. Example: "ff"
	 */
	static final String DEVICE_CLASS = "USB.device.bDeviceClass";

	/**
	 * MANDATORY property key. Value is "USB.device.bDeviceSubClass".
	 * 
	 * The value is hexadecimal, 2-digits. Example: "ff"
	 */
	static final String DEVICE_SUBCLASS = "USB.device.bDeviceSubClass";

	/**
	 * MANDATORY property key. Value is "USB.device.bDeviceProtocol".
	 * 
	 * The value is hexadecimal, 2-digits. Example: "ff"
	 */
	static final String DEVICE_PROTOCOL = "USB.device.bDeviceProtocol";

	/**
	 * MANDATORY property key. Value is "USB.device.idVendor".
	 * 
	 * The value is hexadecimal, 4-digits. Example: "0403"
	 */
	static final String VID = "USB.device.idVendor";

	/**
	 * MANDATORY property key. Value is "USB.device.idProduct".
	 * 
	 * The value is hexadecimal, 4-digits. Example: "8372"
	 */
	static final String PID = "USB.device.idProduct";

	/**
	 * MANDATORY property key. Value is "USB.device.bcdDevice".
	 * 
	 * The value is string of decimal number, 4-digits, converted from the
	 * 4-digit BCD format. Example: "0200"
	 */
	static final String RELEASE_NUMBER = "USB.device.bcdDevice";

	/**
	 * OPTIONAL Property key. Value is "iManufacturer".
	 * 
	 * The value is string of indicated in iManufacturer. (The value is not the
	 * index.) Example: "Buffalo Inc."
	 */
	static final String MANUFACTURER = "USB.device.iManufacturer";

	/**
	 * OPTIONAL Property key. Value is "iProduct".
	 * 
	 * The value is string of indicated in iProduct. (The value is not the
	 * index.) Example: "USB2.0 PC Camera"
	 */
	static final String PRODUCT = "USB.device.iProduct";

	/**
	 * OPTIONAL Property key. Value is "USB.device.iSerialNumber".
	 * 
	 * The value is string of indicated in iSerialNumber. (The value is not the
	 * index.) Example: "57B0002600000001"
	 */
	static final String SERIALNUMBER = "USB.device.iSerialNumber";

	/**
	 * MANDATORY property key. Value is "USB.device.interfaceclassess".
	 * 
	 * The property value is an array of String for each interfaces. Each String
	 * value is connected with "_" interface descriptor's bInterfaceClass,
	 * bInterfaceSubClass, and bInterface Protocol.
	 * 
	 * 
	 * If there is no subclass code associated with the class code, does not
	 * connect subclass code and protocol code. If there is no protocol code
	 * associated with the class code, the protocol code is not connected.
	 * 
	 * In addition, if the class code is vendor-specific class, does not connect
	 * subclass code and protocol code. Set only the class code.
	 * 
	 * Example: A example of a USB device that has 2 interfaces. The first class
	 * code is CDC, subclass code is ACM(without protocol code). The second
	 * class code is CDC-Data (no subclass code and protocol code).
	 * 
	 * Value: "CDC_ACM", "CDC-Data"
	 * 
	 */
	static final String USB_CLASS = "USB.device.interfaceclassess";

	/**
	 * MANDATORY property key. Value is "USB.device.bus".
	 * 
	 * Used to identify USB devices with same VID / PID. The value is the string
	 * ID of the USB bus assigned when connecting the USB device. USB bus ID is
	 * Character digit 3 decimal (001-127).
	 * 
	 * The USB bus ID does not change while connecting the USB device.
	 * 
	 * Example: "003" (In the case of the USB bus is "3")
	 */
	static final String USB_BUS = "USB.device.bus";

	/**
	 * MANDATORY property key. Value is "USB.device.address".
	 * 
	 * Used to identify USB devices with same VID / PID. The value is the string
	 * ID of the USB address assigned when connecting the USB device. USB
	 * address is Character digit 3 decimal (001-127).
	 * 
	 * The USB address does not change while connecting the USB device.
	 * 
	 * Example: "002" (In the case of the USB address is "2")
	 */
	static final String USB_ADDR = "USB.device.address";

	/**
	 * OPTIONAL Property key. Value is "USB.device.comport".
	 * 
	 * The USB Device has a serial communication function, set the value that
	 * represents the COM port. If the USB device does not have a serial
	 * communication function, this key and value is not set.
	 * 
	 * The driver can communicate through Java Communications API with this
	 * value. Set this value "portName" of
	 * javax.comm.CommPortIdentifier#getPortIdentifier(String portName). Then
	 * serial communication is possible. If a USB base driver set this property,
	 * USBDevice.DEVICE_CATEGORY_USBSERIAL must be set to DEVICE_CATEGORY.
	 * 
	 * Example: "/dev/ttyUSB0"
	 */
	static final String COM_PORT = "USB.device.comport";

	/**
	 * OPTIONAL Property key. Value is "USB.device.mountpoint".
	 * 
	 * If the USB device is Mass Storage Class, set the value that represents
	 * the mount point (a path to the USB storage) in OS. If the USB device is
	 * not Mass Storage Class, this key and value is not set. The driver can
	 * read and write the USB storage through standard API such as File. If a
	 * USB base driver set this property, USBDevice.DEVICE_CATEGORY_MASSSTORAGE
	 * must be set to DEVICE_CATEGORY.
	 * 
	 * Example: "/mnt/media/usb-storage-01/"
	 */
	static final String MOUNTPOINT = "USB.device.mountpoint";

	/**
	 * Constant for the USB device match scale, indicating a match with the VID
	 * and the PID. Value is 10.
	 */
	int MATCH_MODEL = 10;

	/**
	 * Constant for the USB device match scale, indicating a match with the
	 * DEVICE_CLASS and the DEVICE_SUBCLASS. Value is 5.
	 */
	int MATCH_SUBCLASS = 5;

	/**
	 * Constant for the USB device match scale, indicating a match with the
	 * DEVICE_CLASS. Value is 3.
	 */
	int MATCH_CLASS = 3;

}
