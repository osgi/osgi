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

/**
 * Constants for serial device settings.
 */
public interface Constants {

	/**
	 * Baud rate: Automatic baud rate (if available).
	 */
	int	BAUD_AUTO				= -1;

	/**
	 * Baud rate: 9600.
	 */
	int	BAUD_9600				= 9600;

	/**
	 * Baud rate: 14400.
	 */
	int	BAUD_14400				= 14400;

	/**
	 * Baud rate: 19200.
	 */
	int	BAUD_19200				= 19200;

	/**
	 * Baud rate: 38400.
	 */

	int	BAUD_38400				= 38400;

	/**
	 * Baud rate: 57600.
	 */
	int	BAUD_57600				= 57600;

	/**
	 * Baud rate: 115200.
	 */
	int	BAUD_115200				= 115200;

	/**
	 * Data bits: 5.
	 */
	int	DATABITS_5				= 5;

	/**
	 * Data bits: 6.
	 */
	int	DATABITS_6				= 6;

	/**
	 * Data bits: 7.
	 */
	int	DATABITS_7				= 7;

	/**
	 * Data bits: 8.
	 */
	int	DATABITS_8				= 8;

	/**
	 * Flow control: None.
	 */
	int	FLOWCONTROL_NONE		= 0;

	/**
	 * Flow control: RTS/CTS on input.
	 */
	int	FLOWCONTROL_RTSCTS_IN	= 1;

	/**
	 * Flow control: RTS/CTS on output.
	 */
	int	FLOWCONTROL_RTSCTS_OUT	= 2;

	/**
	 * Flow control: XON/XOFF on input.
	 */
	int	FLOWCONTROL_XONXOFF_IN	= 4;

	/**
	 * Flow control: XON/XOFF on output.
	 */
	int	FLOWCONTROL_XONXOFF_OUT	= 8;

	/**
	 * Parity: None.
	 */
	int	PARITY_NONE				= 0;

	/**
	 * Parity: Odd.
	 */
	int	PARITY_ODD				= 1;

	/**
	 * Parity: Even.
	 */
	int	PARITY_EVEN				= 2;

	/**
	 * Parity: Mark.
	 */
	int	PARITY_MARK				= 3;

	/**
	 * Parity: Space.
	 */
	int	PARITY_SPACE			= 4;

	/**
	 * Stop bits: 1.
	 */
	int	STOPBITS_1				= 1;

	/**
	 * Stop bits: 2.
	 */
	int	STOPBITS_2				= 2;

	/**
	 * Stop bits: 1.5.
	 */
	int	STOPBITS_1_5			= 3;

}
