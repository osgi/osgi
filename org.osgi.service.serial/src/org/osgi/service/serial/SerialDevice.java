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

package org.osgi.service.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SerialDevice is a service representing a device performing serial
 * communication.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public interface SerialDevice {
	/**
	 * Constant for the value of the service property {@code DEVICE_CATEGORY}
	 * used for all Serial devices.
	 * 
	 * <p>
	 * A Serial base driver bundle must set this property key.
	 *
	 * @see org.osgi.service.device.Constants#DEVICE_CATEGORY
	 */
	String	DEVICE_CATEGORY	= "Serial";

	/**
	 * Service property for the serial comport.
	 * <p>
	 * Represents the name of the port. The value type is String.
	 * <p>
	 * For example, "/dev/ttyUSB0", "COM5", or "/dev/tty.usbserial-XXXXXX".
	 */
	String	SERIAL_COMPORT	= "serial.comport";

	/**
	 * Returns an input stream.
	 * 
	 * @return An input stream.
	 * @throws IOException if an I/O error occurred.
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns an output stream.
	 * 
	 * @return An output stream.
	 * @throws IOException If an I/O error occurred.
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Gets the Serial port configuration.
	 *
	 * @return The SerialPortConfiguration object containing the configuration.
	 */
	SerialPortConfiguration getConfiguration();

	/**
	 * Sets the Serial port configuration.
	 * 
	 * @param configuration The SerialPortConfiguration object containing the
	 *        configuration.
	 * @throws SerialDeviceException If the parameter is specified incorrectly
	 *         or the parameter is not supported.
	 */
	void setConfiguration(SerialPortConfiguration configuration) throws SerialDeviceException;

	/**
	 * Returns the DTR state.
	 *
	 * @return The DTR state.
	 */
	boolean isDTR();

	/**
	 * Returns the DTS state.
	 *
	 * @return The DTS state.
	 */
	boolean isRTS();

	/**
	 * Returns the DSR state.
	 *
	 * @return The DSR state.
	 */
	boolean isDSR();

	/**
	 * Returns the CTS state.
	 *
	 * @return The CTS state.
	 */
	boolean isCTS();

	/**
	 * Sets the DTR state.
	 *
	 * @param dtr {@code true} for DTR on; {@code false} for DTR for off.
	 * @throws SerialDeviceException If the parameter is not supported.
	 */
	void setDTR(boolean dtr) throws SerialDeviceException;

	/**
	 * Sets the RTS state.
	 *
	 * @param rts {@code true} for RTS on; {@code false} for RTS for off.
	 * @throws SerialDeviceException If the parameter is not supported.
	 */
	void setRTS(boolean rts) throws SerialDeviceException;
}
