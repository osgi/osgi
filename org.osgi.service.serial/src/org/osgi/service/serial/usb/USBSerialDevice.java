package org.osgi.service.serial.usb;

/**
 * Defines additional SerialDevice service properties for USB-Serial devices.<br>
 */
public interface USBSerialDevice {

    /**
     * The value string of "bus.type" service property.<br>
     */
    String BUS_TYPE_USB = "USB";

    /**
     * The key string of "usb.bus" service property.<br>
     * Used to identify USB devices with same VID / PID.
     * The value is the ID of the USB bus assigned when connecting the USB device.
     * USB bus ID is integer.
     * The USB bus ID does not change while the USB device remains connected.<br>
     * The value is Integer.<br>
     * Example: 3<br>
     */
    String USB_BUS = "usb.bus";

    /**
     * The key string of "usb.address" service property.<br>
     * Used to identify USB devices with same VID / PID.
     * The value is the ID of the USB address assigned when connecting the USB device.
     * USB address is integer (001-127).
     * The USB address does not change while the USB device remains connected.<br>
     * The value is Integer.<br>
     * Example: 2<br>
     */
    String USB_ADDRESS = "usb.address";

    /**
     * Optional.<br>
     * The key string of "usb.bcdUSB" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bcdUSB".<br>
     * The value is String, the 4-digit BCD format.
     * Example: "0210"<br>
     */
    String USB_BCDUSB = "usb.bcdUSB";

    /**
     * The key string of "usb.bDeviceClass" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bDeviceClass".<br>
     * The value is String, hexadecimal, 2-digits.<br>
     * Example: "ff"<br>
     */
    String USB_BDEVICECLASS = "usb.bDeviceClass";

    /**
     * The key string of "usb.bDeviceSubClass" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bDeviceSubClass".<br>
     * The value is String, hexadecimal, 2-digits.<br>
     * Example: "ff"<br>
     */
    String USB_BDEVICESUBCLASS = "usb.bDeviceSubClass";

    /**
     * The key string of "usb.bDeviceProtocol" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bDeviceProtocol".<br>
     * The value is String, hexadecimal, 2-digits.<br>
     * Example: "ff"<br>
     */
    String USB_BDEVICEPROTOCOL = "usb.bDeviceProtocol";

    /**
     * Optional.<br>
     * The key string of "usb.bMaxPacketSize0" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bMaxPacketSize0".<br>
     * The value is Integer.<br>
     */
    String USB_BMAXPACKETSIZE0 = "usb.bMaxPacketSize0";

    /**
     * The key string of "usb.idVendor" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "idVendor".<br>
     * The value is String, hexadecimal, 4-digits.<br>
     * Example: "0403"<br>
     */
    String USB_IDVENDOR = "usb.idVendor";

    /**
     * The key string of "usb.idProduct" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "idProduct".<br>
     * The value is String, hexadecimal, 4-digits.<br>
     * Example: "8372"
     */
    String USB_IDPRODUCT = "usb.idProduct";

    /**
     * The key string of "usb.bcdDevice" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bcdDevice".<br>
     * The value is String, the 4-digit BCD format.<br>
     * Example: "0200"<br>
     */
    String USB_BCDDEVICE = "usb.bcdDevice";

    /**
     * Optional.<br>
     * The key string of "usb.Manufacturer" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "iManufacturer".<br>
     * The value is String of indicated in iManufacturer. (The value is not the index.)<br>
     * Example: "Buffalo Inc."<br>
     */
    String USB_MANUFACTURER = "usb.Manufacturer";

    /**
     * Optional.<br>
     * The key string of "usb.bNumConfigurations" service property.<br>
     * Service properties from USB Device Descriptor.
     * Device Descriptor's Field from USB Spec is "bNumConfigurations".<br>
     * The value is Integer.<br>
     */
    String USB_BNUMCONFIGURATIONS = "usb.bNumConfigurations";

    /**
     * The key string of "usb.bInterfaceNumber" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "bInterfaceNumber".<br>
     * The value is Integer.<br>
     */
    String USB_BINTERFACENUMBER = "usb.bInterfaceNumber";

    /**
     * Optional.<br>
     * The key string of "usb.bAlternateSetting" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "bAlternateSetting".<br>
     * The value is Integer.<br>
     */
    String USB_BALTERNATESETTING = "usb.bAlternateSetting";

    /**
     * Optional.<br>
     * The key string of "usb.bNumEndpoints" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "bNumEndpoints".<br>
     * The value is Integer.<br>
     */
    String USB_BNUMENDPOINTS = "usb.bNumEndpoints";

    /**
     * The key string of "usb.bInterfaceClass" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "bInterfaceClass".<br>
     * The value is String, hexadecimal, 2-digits.<br>
     * Example: "ff"<br>
     */
    String USB_BINTERFACECLASS = "usb.bInterfaceClass";

    /**
     * The key string of "usb.bInterfaceSubClass" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "bInterfaceSubClass".<br>
     * The value is String, hexadecimal, 2-digits.<br>
     * Example: "ff"<br>
     */
    String USB_BINTERFACESUBCLASS = "usb.bInterfaceSubClass";

    /**
     * The key string of "usb.bInterfaceProtocol" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "bInterfaceProtocol".<br>
     * The value is String, hexadecimal, 2-digits.<br>
     * Example: "ff"<br>
     */
    String USB_BINTERFACEPROTOCOL = "usb.bInterfaceProtocol";

    /**
     * Optional.<br>
     * The key string of "usb.iInterface" service property.<br>
     * Service properties from USB Interface Descriptor.
     * Interface Descriptor's Field from USB Spec is "iInterface".<br>
     * The value is String of indicated in iInterface. (The value is not the index.)<br>
     */
    String USB_INTERFACE = "usb.iInterface";

}
