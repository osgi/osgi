/*
 * Copyright (c) OSGi Alliance (2015, 2020). All Rights Reserved.
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

package org.osgi.impl.service.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.osgi.service.serial.SerialConstants;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialDeviceException;
import org.osgi.service.serial.SerialPortConfiguration;

public class SerialDeviceImpl implements SerialDevice {
	private InputStream				is;
	private OutputStream			os;

	private boolean					dtr				= false;
	private boolean					rts				= false;

	private SerialPortConfiguration	configuration	= new SerialPortConfiguration(9600);

	public SerialDeviceImpl(String comport) {
		this.is = new ByteArrayInputStream("InputStream".getBytes());
		this.os = new ByteArrayOutputStream();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (is == null) {
			throw new IOException("This connection has been closed already.");
		}
		return is;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (os == null) {
			throw new IOException("This connection has been closed already.");
		}
		return os;
	}

	@Override
	public SerialPortConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(SerialPortConfiguration configuration) throws SerialDeviceException {
		int baudRate = configuration.getBaudRate();
		int dataBits = configuration.getDataBits();
		int flowControl = configuration.getFlowControl();
		int parity = configuration.getParity();
		int stopBits = configuration.getStopBits();

		if (baudRate < SerialConstants.BAUD_AUTO) {
			throw new SerialDeviceException(SerialDeviceException.UNSUPPORTED_OPERATION, "The baudRate is invalid.");
		}
		if (dataBits != SerialConstants.DATABITS_5
				&& dataBits != SerialConstants.DATABITS_6
				&& dataBits != SerialConstants.DATABITS_7
				&& dataBits != SerialConstants.DATABITS_8) {
			throw new SerialDeviceException(SerialDeviceException.UNSUPPORTED_OPERATION, "The dataBits is invalid.");
		}
		if (flowControl != SerialConstants.FLOWCONTROL_NONE
				&& flowControl != SerialConstants.FLOWCONTROL_RTSCTS_IN
				&& flowControl != SerialConstants.FLOWCONTROL_RTSCTS_OUT
				&& flowControl != SerialConstants.FLOWCONTROL_XONXOFF_IN
				&& flowControl != SerialConstants.FLOWCONTROL_XONXOFF_OUT) {
			throw new SerialDeviceException(SerialDeviceException.UNSUPPORTED_OPERATION, "The flowControl is invalid.");
		}
		if (stopBits != SerialConstants.STOPBITS_1
				&& stopBits != SerialConstants.STOPBITS_1_5
				&& stopBits != SerialConstants.STOPBITS_2) {
			throw new SerialDeviceException(SerialDeviceException.UNSUPPORTED_OPERATION, "The stopBits is invalid.");
		}
		if (parity != SerialConstants.PARITY_NONE
				&& parity != SerialConstants.PARITY_ODD
				&& parity != SerialConstants.PARITY_EVEN
				&& parity != SerialConstants.PARITY_MARK
				&& parity != SerialConstants.PARITY_SPACE) {
			throw new SerialDeviceException(SerialDeviceException.UNSUPPORTED_OPERATION, "The parity is invalid.");
		}
		this.configuration = configuration;
	}

	@Override
	public boolean isDTR() {
		return dtr;
	}

	@Override
	public boolean isRTS() {
		return rts;
	}

	@Override
	public boolean isDSR() {
		return false;
	}

	@Override
	public boolean isCTS() {
		return false;
	}

	@Override
	public void setDTR(boolean dtr) throws SerialDeviceException {
		this.dtr = dtr;
	}

	@Override
	public void setRTS(boolean rts) throws SerialDeviceException {
		this.rts = rts;
	}
}
