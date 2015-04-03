/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.usbinfo;

/**
 * Represents a USB device. For each USB device, an object is registered with
 * the framework under the USBInfoDevice interface. A USB information base
 * driver must implement this interface.
 *
 * The values of the USB property names are defined by the USB Implementers
 * Forum, Inc.
 *
 * The package name is org.osgi.service.usbinfo.
 *
 */
public interface /* org.osgi.service.usbinfo. */USBInfoDevice {

	/**
	 * MANDATORY property. The value is "USBInfo".
	 *
	 * Constant for the value of the service property DEVICE_CATEGORY used for
	 * all USB devices. A USB information base driver bundle must set this
	 * property key.
	 *
	 * See Also org.osgi.service.device.Constants.DEVICE_CATEGORY
	 */
	String	DEVICE_CATEGORY			= "USBInfo";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.bcdUSB" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bcdUSB".<br>
	 * The value is String, the 4-digit BCD format. Example: "0210"<br>
	 */
	String	USB_BCDUSB				= "usbinfo.bcdUSB";

	/**
	 * The key string of "usbinfo.bDeviceClass" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bDeviceClass".<br>
	 * The value is String, hexadecimal, 2-digits.<br>
	 * Example: "ff"<br>
	 */
	String	USB_BDEVICECLASS		= "usbinfo.bDeviceClass";

	/**
	 * The key string of "usbinfo.bDeviceSubClass" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bDeviceSubClass".<br>
	 * The value is String, hexadecimal, 2-digits.<br>
	 * Example: "ff"<br>
	 */
	String	USB_BDEVICESUBCLASS		= "usbinfo.bDeviceSubClass";

	/**
	 * The key string of "usbinfo.bDeviceProtocol" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bDeviceProtocol".<br>
	 * The value is String, hexadecimal, 2-digits.<br>
	 * Example: "ff"<br>
	 */
	String	USB_BDEVICEPROTOCOL		= "usbinfo.bDeviceProtocol";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.bMaxPacketSize0" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bMaxPacketSize0".<br>
	 * The value is Integer.<br>
	 */
	String	USB_BMAXPACKETSIZE0		= "usbinfo.bMaxPacketSize0";

	/**
	 * The key string of "usbinfo.idVendor" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "idVendor".<br>
	 * The value is String, hexadecimal, 4-digits.<br>
	 * Example: "0403"<br>
	 */
	String	USB_IDVENDOR			= "usbinfo.idVendor";

	/**
	 * The key string of "usbinfo.idProduct" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "idProduct".<br>
	 * The value is String, hexadecimal, 4-digits.<br>
	 * Example: "8372"
	 */
	String	USB_IDPRODUCT			= "usbinfo.idProduct";

	/**
	 * The key string of "usbinfo.bcdDevice" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bcdDevice".<br>
	 * The value is String, the 4-digit BCD format.<br>
	 * Example: "0200"<br>
	 */
	String	USB_BCDDEVICE			= "usbinfo.bcdDevice";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.Manufacturer" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "iManufacturer".<br>
	 * The value is String of indicated in iManufacturer. (The value is not the
	 * index.)<br>
	 * Example: "Buffalo Inc."<br>
	 */
	String	USB_MANUFACTURER		= "usbinfo.Manufacturer";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.Product" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "iProduct".<br>
	 * The value is String of indicated in iProduct. (The value is not the
	 * index.)<br>
	 * Example: "USB2.0 PC Camera"<br>
	 */
	String	USB_PRODUCT				= "usbinfo.Product";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.SerialNumber" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "iSerialNumber".<br>
	 * The value is String of indicated in iSerialNumber. (The value is not the
	 * index.)<br>
	 * Example: "57B0002600000001"<br>
	 */
	String	USB_SERIALNUMBER		= "usbinfo.SerialNumber";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.bNumConfigurations" service property.<br>
	 * Service properties from USB Device Descriptor. Device Descriptor's Field
	 * from USB Spec is "bNumConfigurations".<br>
	 * The value is Integer.<br>
	 */
	String	USB_BNUMCONFIGURATIONS	= "usbinfo.bNumConfigurations";

	/**
	 * The key string of "usbinfo.bInterfaceNumber" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "bInterfaceNumber".<br>
	 * The value is Integer.<br>
	 */
	String	USB_BINTERFACENUMBER	= "usbinfo.bInterfaceNumber";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.bAlternateSetting" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "bAlternateSetting".<br>
	 * The value is Integer.<br>
	 */
	String	USB_BALTERNATESETTING	= "usbinfo.bAlternateSetting";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.bNumEndpoints" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "bNumEndpoints".<br>
	 * The value is Integer.<br>
	 */
	String	USB_BNUMENDPOINTS		= "usbinfo.bNumEndpoints";

	/**
	 * The key string of "usbinfo.bInterfaceClass" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "bInterfaceClass".<br>
	 * The value is String, hexadecimal, 2-digits.<br>
	 * Example: "ff"<br>
	 */
	String	USB_BINTERFACECLASS		= "usbinfo.bInterfaceClass";

	/**
	 * The key string of "usbinfo.bInterfaceSubClass" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "bInterfaceSubClass".<br>
	 * The value is String, hexadecimal, 2-digits.<br>
	 * Example: "ff"<br>
	 */
	String	USB_BINTERFACESUBCLASS	= "usbinfo.bInterfaceSubClass";

	/**
	 * The key string of "usbinfo.bInterfaceProtocol" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "bInterfaceProtocol".<br>
	 * The value is String, hexadecimal, 2-digits.<br>
	 * Example: "ff"<br>
	 */
	String	USB_BINTERFACEPROTOCOL	= "usbinfo.bInterfaceProtocol";

	/**
	 * Optional.<br>
	 * The key string of "usbinfo.Interface" service property.<br>
	 * Service properties from USB Interface Descriptor. Interface Descriptor's
	 * Field from USB Spec is "iInterface".<br>
	 * The value is String of indicated in iInterface. (The value is not the
	 * index.)<br>
	 */
	String	USB_INTERFACE			= "usbinfo.Interface";

	/**
	 * The key string of "usbinfo.bus" service property.<br>
	 * Used to identify USB devices with same VID / PID. The value is the ID of
	 * the USB bus assigned when connecting the USB device. USB bus ID is
	 * integer. The USB bus ID does not change while the USB device remains
	 * connected.<br>
	 * The value is Integer.<br>
	 * Example: 3<br>
	 */
	String	USB_BUS					= "usbinfo.bus";

	/**
	 * The key string of "usbinfo.address" service property.<br>
	 * Used to identify USB devices with same VID / PID. The value is the ID of
	 * the USB address assigned when connecting the USB device. USB address is
	 * integer (001-127). The USB address does not change while the USB device
	 * remains connected.<br>
	 * The value is Integer.<br>
	 * Example: 2<br>
	 */
	String	USB_ADDRESS				= "usbinfo.address";

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * usbinfo.idVendor, usbinfo.idProduct and usbinfo.bcdDevice. Value is 50.
	 */
	int		MATCH_VERSION			= 50;

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * usbinfo.idVendor and usbinfo.idProduct. Value is 40.
	 */
	int		MATCH_MODEL				= 40;

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * usbinfo.bDeviceClass, usbinfo.bDeviceSubClass and
	 * usbinfo.bDeviceProtocol, or a match with bInterfaceClass,
	 * bInterfaceSubClass and bInterfaceProtocol in one of
	 * usbinfo.interfaceclasses. Value is 30.
	 */
	int		MATCH_PROTOCOL			= 30;

	/**
	 * Constant for the USB device match scale, indicating a
	 * matchusbinfo.bDeviceClass and usbinfo.bDeviceSubClass, or a match with
	 * bInterfaceClass and bInterfaceSubClass in one of
	 * usbinfo.interfaceclasses. Value is 20.
	 */
	int		MATCH_SUBCLASS			= 20;

	/**
	 * Constant for the USB device match scale, indicating a match with
	 * usbinfo.bDeviceClass, or a match with bInterfaceClass in one of
	 * usbinfo.interfaceclasses. Value is 10.
	 */
	int		MATCH_CLASS				= 10;
}
