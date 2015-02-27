/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.service.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SerialDevice is an interface to express a device performing serial
 * communication.
 */
public interface SerialDevice {
	/**
	 * Constant for the value of the service property {@code DEVICE_CATEGORY}
	 * used for all Serial devices. Value is "Serial".
	 */
	String	DEVICE_CATEGORY	= "Serial";

	/**
	 * The key string of "serial.comport" service property.<br>
	 * Represents the name of the port.<br>
	 * The value is String.<br>
	 * Example1: "/dev/ttyUSB0"<br>
	 * Example2: "COM5"<br>
	 * Example3: "/dev/tty.usbserial-XXXXXX"<br>
	 */
	String	SERIAL_COMPORT	= "serial.comport";

	/**
	 * Returns an input stream.<br>
	 * 
	 * @return an input stream
	 * @throws IOException if an I/O error occurred
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns an output stream.<br>
	 * 
	 * @return an output stream
	 * @throws IOException if an I/O error occurred
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Gets the Serial port configuration.<br>
	 *
	 * @return SerialPortConfiguration object represents the configuration
	 */
	SerialPortConfiguration getConfiguration();

	/**
	 * Sets the Serial port configuration.
	 * 
	 * @param configuration SerialPortConfiguration object represents the
	 *        configuration
	 * @throws SerialDeviceException if the parameter is specified incorrectly
	 *         or the parameter is not supported.
	 */
	void setConfiguration(SerialPortConfiguration configuration) throws SerialDeviceException;

	/**
	 * Gets the DTR state.<br>
	 *
	 * @return the DTR state
	 */
	boolean isDTR();

	/**
	 * Gets the DTS state.<br>
	 *
	 * @return the DTS state
	 */
	boolean isRTS();

	/**
	 * Gets the DSR state.<br>
	 *
	 * @return the DSR state
	 */
	boolean isDSR();

	/**
	 * Gets the CTS state.<br>
	 *
	 * @return the CTS state
	 */
	boolean isCTS();

	/**
	 * Sets the DTR state.<br>
	 *
	 * @param dtr <ul>
	 *        <li>{@code true} on DTR</li>
	 *        <li>{@code false} off DTR</li>
	 *        </ul>
	 * @throws SerialDeviceException if the parameter is specified incorrectly
	 *         or the parameter is not supported.
	 */
	void setDTR(boolean dtr) throws SerialDeviceException;

	/**
	 * Sets the RTS state.<br>
	 *
	 * @param rts <ul>
	 *        <li>{@code true} on RTS</li>
	 *        <li>{@code false} off RTS</li>
	 *        </ul>
	 * @throws SerialDeviceException if the parameter is specified incorrectly
	 *         or the parameter is not supported.
	 */
	void setRTS(boolean rts) throws SerialDeviceException;

}
