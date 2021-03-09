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

/**
 * An object represents the Serial port configuration.
 * 
 * @Immutable
 * @author $Id$
 */
public class SerialPortConfiguration {
	private final int	baudRate;
	private final int	dataBits;
	private final int	flowControl;
	private final int	parity;
	private final int	stopBits;

	/**
	 * Creates an instance of the serial port configuration with the specified
	 * Baud rate, Data bits, Flow control, Parity and Stop bits.
	 * 
	 * @param baudRate Baud rate.
	 * @param dataBits Data bits.
	 * @param flowControl Flow control.
	 * @param parity Parity.
	 * @param stopBits Stop bits.
	 */
	public SerialPortConfiguration(int baudRate, int dataBits, int flowControl, int parity, int stopBits) {
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.flowControl = flowControl;
		this.parity = parity;
		this.stopBits = stopBits;
	}

	/**
	 * Creates an instance of the serial port configuration with the specified
	 * Baud rate and the following configuration: Data bits = 8, Flow control =
	 * none, Parity = none, Stop bits = 1.
	 * 
	 * @param baudRate Baud rate.
	 */
	public SerialPortConfiguration(int baudRate) {
		this.baudRate = baudRate;
		this.dataBits = SerialConstants.DATABITS_8;
		this.flowControl = SerialConstants.FLOWCONTROL_NONE;
		this.parity = SerialConstants.PARITY_NONE;
		this.stopBits = SerialConstants.STOPBITS_1;
	}

	/**
	 * Creates an instance of the serial port configuration with the following
	 * configuration: Baud rate = auto, Data bits = 8, Flow control = none,
	 * Parity = none, Stop bits = 1.
	 */
	public SerialPortConfiguration() {
		this.baudRate = SerialConstants.BAUD_AUTO;
		this.dataBits = SerialConstants.DATABITS_8;
		this.flowControl = SerialConstants.FLOWCONTROL_NONE;
		this.parity = SerialConstants.PARITY_NONE;
		this.stopBits = SerialConstants.STOPBITS_1;
	}

	/**
	 * Returns the baud rate.
	 *
	 * @return The baud rate.
	 */
	public int getBaudRate() {
		return baudRate;
	}

	/**
	 * Returns the data bits.
	 *
	 * @return The data bits.
	 */
	public int getDataBits() {
		return dataBits;
	}

	/**
	 * Returns the flow control.
	 *
	 * @return The flow control.
	 */
	public int getFlowControl() {
		return flowControl;
	}

	/**
	 * Returns the parity.
	 *
	 * @return The parity.
	 */
	public int getParity() {
		return parity;
	}

	/**
	 * Returns the stop bits.
	 *
	 * @return The stop bits.
	 */
	public int getStopBits() {
		return stopBits;
	}
}
