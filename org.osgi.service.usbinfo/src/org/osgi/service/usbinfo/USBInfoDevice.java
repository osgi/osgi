/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.usbinfo;

/**
 * Represents a USB device. For each USB device, an object is registered with
 * the framework under the USBInfoDevice interface. A USB information base
 * driver must implement this interface.
 *
 * <p>
 * The values of the USB property names are defined by the USB Implementers
 * Forum, Inc.
 *
 * @ThreadSafe
 * @author $Id$
 */
public interface USBInfoDevice {

	/**
	 * Constant for the value of the service property {@code DEVICE_CATEGORY}
	 * used for all USB devices.
	 * 
	 * <p>
	 * A USB information base driver bundle must set this property key.
	 *
	 * @see org.osgi.service.device.Constants#DEVICE_CATEGORY
	 */
	String	DEVICE_CATEGORY			= "USBInfo";

	/**
	 * Service property for USB Device Descriptor field "bcdUSB".
	 * <p>
	 * The value type is String; the value is in 4-digit BCD format. For
	 * example, "0210". This service property is optional.
	 */
	String	USB_BCDUSB				= "usbinfo.bcdUSB";

	/**
	 * Service property for USB Device Descriptor field "bDeviceClass".
	 * <p>
	 * The value type is String; the value is in 2-digit hexadecimal. For
	 * example, "ff".
	 */
	String	USB_BDEVICECLASS		= "usbinfo.bDeviceClass";

	/**
	 * Service property for USB Device Descriptor field "bDeviceSubClass".
	 * <p>
	 * The value type is String; the value is in 2-digit hexadecimal. For
	 * example, "ff".
	 */
	String	USB_BDEVICESUBCLASS		= "usbinfo.bDeviceSubClass";

	/**
	 * Service property for USB Device Descriptor field "bDeviceProtocol".
	 * <p>
	 * The value type is String; the value is in 2-digit hexadecimal. For
	 * example, "ff".
	 */
	String	USB_BDEVICEPROTOCOL		= "usbinfo.bDeviceProtocol";

	/**
	 * Service property for USB Device Descriptor field "bMaxPacketSize0".
	 * <p>
	 * The value type is Integer. This service property is optional.
	 */
	String	USB_BMAXPACKETSIZE0		= "usbinfo.bMaxPacketSize0";

	/**
	 * Service property for USB Device Descriptor field "idVendor".
	 * <p>
	 * The value type is String; the value is in 4-digit hexadecimal. For
	 * example, "0403".
	 */
	String	USB_IDVENDOR			= "usbinfo.idVendor";

	/**
	 * Service property for USB Device Descriptor field "idProduct".
	 * <p>
	 * The value type is String; the value is in 4-digit hexadecimal. For
	 * example, "8372".
	 */
	String	USB_IDPRODUCT			= "usbinfo.idProduct";

	/**
	 * Service property for USB Device Descriptor field "bcdDevice".
	 * <p>
	 * The value type is String; the value is in 4-digit BCD format. For
	 * example, "0200".
	 */
	String	USB_BCDDEVICE			= "usbinfo.bcdDevice";

	/**
	 * Service property for name referenced by USB Device Descriptor field
	 * "iManufacturer".
	 * <p>
	 * The value type is String. For example, "Buffalo Inc.". This service
	 * property is optional.
	 */
	String	USB_MANUFACTURER		= "usbinfo.Manufacturer";

	/**
	 * Service property for name referenced by USB Device Descriptor field
	 * "iProduct".
	 * <p>
	 * The value type is String. For example, "USB2.0 PC Camera". This service
	 * property is optional.
	 */
	String	USB_PRODUCT				= "usbinfo.Product";

	/**
	 * Service property for name referenced by USB Device Descriptor field
	 * "iSerialNumber".
	 * <p>
	 * The value type is String. For example, "57B0002600000001". This service
	 * property is optional.
	 */
	String	USB_SERIALNUMBER		= "usbinfo.SerialNumber";

	/**
	 * Service property for USB Device Descriptor field "bNumConfigurations".
	 * <p>
	 * The value type is Integer. This service property is optional.
	 */
	String	USB_BNUMCONFIGURATIONS	= "usbinfo.bNumConfigurations";

	/**
	 * Service property for USB Interface Descriptor field "bInterfaceNumber".
	 * <p>
	 * The value type is Integer.
	 */
	String	USB_BINTERFACENUMBER	= "usbinfo.bInterfaceNumber";

	/**
	 * Service property for USB Interface Descriptor field "bAlternateSetting".
	 * <p>
	 * The value type is Integer. This service property is optional.
	 */
	String	USB_BALTERNATESETTING	= "usbinfo.bAlternateSetting";

	/**
	 * Service property for USB Interface Descriptor field "bNumEndpoints".
	 * <p>
	 * The value type is Integer. This service property is optional.
	 */
	String	USB_BNUMENDPOINTS		= "usbinfo.bNumEndpoints";

	/**
	 * Service property for USB Interface Descriptor field "bInterfaceClass".
	 * <p>
	 * The value type is String; the value is in 2-digit hexadecimal. For
	 * example, "ff".
	 */
	String	USB_BINTERFACECLASS		= "usbinfo.bInterfaceClass";

	/**
	 * Service property for USB Interface Descriptor field "bInterfaceSubClass".
	 * <p>
	 * The value type is String; the value is in 2-digit hexadecimal. For
	 * example, "ff".
	 */
	String	USB_BINTERFACESUBCLASS	= "usbinfo.bInterfaceSubClass";

	/**
	 * Service property for USB Interface Descriptor field "bInterfaceProtocol".
	 * <p>
	 * The value type is String; the value is in 2-digit hexadecimal. For
	 * example, "ff".
	 */
	String	USB_BINTERFACEPROTOCOL	= "usbinfo.bInterfaceProtocol";

	/**
	 * Service property for name referenced by USB Interface Descriptor field
	 * "iInterface".
	 * <p>
	 * The value type is String. This service property is optional.
	 */
	String	USB_INTERFACE			= "usbinfo.Interface";

	/**
	 * Service property to identify USB bus.
	 * <p>
	 * Used to identify USB devices with same VID / PID. The value is the ID of
	 * the USB bus assigned when connecting the USB device. The USB bus ID is an
	 * integer and does not change while the USB device remains connected. The
	 * value type is Integer.
	 */
	String	USB_BUS					= "usbinfo.bus";

	/**
	 * Service property to identify USB address.
	 * <p>
	 * Used to identify USB devices with same VID / PID. The value is the ID of
	 * the USB address assigned when connecting the USB device. USB address is
	 * an integer in the range 1-127 and does not change while the USB device
	 * remains connected. The value type is Integer.
	 */
	String	USB_ADDRESS				= "usbinfo.address";

	/**
	 * Device Access match value indicating a match with {@link #USB_IDVENDOR},
	 * {@link #USB_IDPRODUCT}, and {@link #USB_BCDDEVICE}.
	 */
	int		MATCH_VERSION			= 50;

	/**
	 * Device Access match value indicating a match with {@link #USB_IDVENDOR},
	 * and {@link #USB_IDPRODUCT}.
	 */
	int		MATCH_MODEL				= 40;

	/**
	 * Device Access match value indicating a match with
	 * {@link #USB_BDEVICECLASS}, {@link #USB_BDEVICESUBCLASS}, and
	 * {@link #USB_BDEVICEPROTOCOL} or a match with {@link #USB_BINTERFACECLASS}
	 * , {@link #USB_BINTERFACESUBCLASS}, and {@link #USB_BINTERFACEPROTOCOL}.
	 */
	int		MATCH_PROTOCOL			= 30;

	/**
	 * Device Access match value indicating a match with
	 * {@link #USB_BDEVICECLASS}, and {@link #USB_BDEVICESUBCLASS} or a match
	 * with {@link #USB_BINTERFACECLASS}, and {@link #USB_BINTERFACESUBCLASS}.
	 */
	int		MATCH_SUBCLASS			= 20;

	/**
	 * Device Access match value indicating a match with
	 * {@link #USB_BDEVICECLASS} or a match with {@link #USB_BINTERFACECLASS}.
	 */
	int		MATCH_CLASS				= 10;
}
