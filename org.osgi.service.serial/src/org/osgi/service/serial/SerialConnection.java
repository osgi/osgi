package org.osgi.service.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SerialConnection is an open communications port.
 */
public interface SerialConnection {

	/**
	 * 5 data bit format.
	 */
	int DATABITS_5 = 5;

	/**
	 * 6 data bit format.
	 */
	int DATABITS_6 = 6;

	/**
	 * 7 data bit format.
	 */
	int DATABITS_7 = 7;

	/**
	 * 8 data bit format.
	 */
	int DATABITS_8 = 8;

	/**
	 * Flow control off.
	 */
	int FLOWCONTROL_NONE = 0;

	/**
	 * RTS/CTS flow control on input.
	 */
	int FLOWCONTROL_RTSCTS_IN = 1;

	/**
	 * RTS/CTS flow control on output.
	 */
	int FLOWCONTROL_RTSCTS_OUT = 2;

	/**
	 * XON/XOFF flow control on input.
	 */
	int FLOWCONTROL_XONXOFF_IN = 4;

	/**
	 * XON/XOFF flow control on output.
	 */
	int FLOWCONTROL_XONXOFF_OUT = 8;

	/**
	 * No parity bit.
	 */
	int PARITY_NONE = 0;

	/**
	 * ODD parity scheme. The parity bit is added so there are an odd number of TRUE bits.
	 */
	int PARITY_ODD = 1;

	/**
	 * EVEN parity scheme. The parity bit is added so there are an even number of TRUE bits.
	 */
	int PARITY_EVEN = 2;

	/**
	 * MARK parity scheme.
	 */
	int PARITY_MARK = 3;

	/**
	 * SPACE parity scheme.
	 */
	int PARITY_SPACE = 4;

	/**
	 * Number of STOP bits - 1.
	 */
	int STOPBITS_1 = 1;

	/**
	 * Number of STOP bits - 2.
	 */
	int STOPBITS_2 = 2;

	/**
	 * Number of STOP bits - 1-1/2. Some UARTs permit 1-1/2 STOP bits only with 5 data bit format, but permit 1 or 2 STOP bits with any format.
	 */
	int STOPBITS_1_5 = 3;

	/**
	 * Closes the communications port.
	 * <br>
	 */
	void close();

	/**
	 * Returns an input stream.<br>
	 * This is the only way to receive data from the communications port.<br>
	 * If the port is unidirectional and doesn't support receiving data, then getInputStream returns null.<br>
	 *
	 * @return InputStream object that can be used to read from the port
	 * @throws IOException if an I/O error occurred
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns an output stream.<br>
	 * This is the only way to send data to the communications port. <br>
	 * If the port is unidirectional and doesn't support sending data, then getOutputStream returns null.<br>
	 *
	 * @return OutputStream object that can be used to write to the port
	 * @throws IOException if an I/O error occurred
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Gets the currently configured baud rate.<br>
	 *
	 * @return integer value indicating the baud rate
	 */
	int getBaudRate();

	/**
	 * Gets the currently configured number of data bits.<br>
	 *
	 * @return integer that can be equal to DATABITS_5, DATABITS_6, DATABITS_7, or DATABITS_8
	 */
	int getDataBits();

	/**
	 * Gets the currently configured flow control mode.<br>
	 *
	 * @return an integer bitmask of the modes FLOWCONTROL_NONE, FLOWCONTROL_RTSCTS_IN, FLOWCONTROL_RTSCTS_OUT, FLOWCONTROL_XONXOFF_IN, and FLOWCONTROL_XONXOFF_OUT.
	 */
	int getFlowControlMode();

	/**
	 * Get the currently configured parity setting.<br>
	 *
	 * @return integer that can be equal to PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK or PARITY_SPACE.
	 */
	int getParity();

	/**
	 * Gets the currently defined stop bits. <br>
	 *
	 * @return integer that can be equal to STOPBITS_1, STOPBITS_2, or STOPBITS_1_5
	 */
	int getStopBits();

	/**
	 * Gets the state of the DTR (Data Terminal Ready) bit in the UART, if supported by the underlying implementation. <br>
	 *
	 * @return state of the DTR
	 */
	boolean isDTR();

	/**
	 * Gets the state of the RTS (Request To Send) bit in the UART, if supported by the underlying implementation. <br>
	 *
	 * @return state of the RTS
	 */
	boolean isRTS();

	/**
	 * Sets or clears the DTR (Data Terminal Ready) bit in the UART, if supported by the underlying implementation.<br>
	 *
	 * @param dtr
	 * <ul>
	 * <li>{@code true} set DTR</li>
	 * <li>{@code false} clear DTR</li>
	 * </ul>
	 */
	void setDTR(boolean dtr);

	/**
	 * Sets the flow control mode.<br>
	 *
	 * @param flowcontrol Can be a bitmask combination of<br>
	 * <ul>
	 * <li>FLOWCONTROL_NONE: no flow control</li>
	 * <li>FLOWCONTROL_RTSCTS_IN: RTS/CTS (hardware) flow control for input</li>
	 * <li>FLOWCONTROL_RTSCTS_OUT: RTS/CTS (hardware) flow control for output</li>
	 * <li>FLOWCONTROL_XONXOFF_IN: XON/XOFF (software) flow control for input</li>
	 * <li>FLOWCONTROL_XONXOFF_OUT: XON/XOFF (software) flow control for output</li>
	 * </ul>
	 * @throws UnsupportedCommOperationException if any of the flow control mode was not supported by the underline OS,
	 * or if input and output flow control are set to different values, i.e.
	 * one hardware and one software. The flow control mode will revert to the value before the call was made.
	 */
	void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException;

	/**
	 * Sets or clears the RTS (Request To Send) bit in the UART, if supported by the underlying implementation. <br>
	 *
	 * @param rts
	 * <ul>
	 * <li>{@code true} set RTS</li>
	 * <li>{@code false} clear RTS</li>
	 * </ul>
	 */
	void setRTS(boolean rts);

	/**
	 * Sets serial port parameters. <br>
	 * DEFAULT: 9600 baud, 8 data bits, 1 stop bit, no parity<br>
	 *
	 * @param baudrate  If the baudrate passed in by the application is unsupported by the driver, the driver will throw an UnsupportedCommOperationException<br>
	 * @param dataBits
	 * <ul>
	 * <li>DATABITS_5: 5 bits </li>
	 * <li>DATABITS_6: 6 bits </li>
	 * <li>DATABITS_7: 7 bits </li>
	 * <li>DATABITS_8: 8 bits </li>
	 * </ul>
	 * @param stopBits
	 * <ul>
	 * <li>STOPBITS_1: 1 stop bit </li>
	 * <li>STOPBITS_2: 2 stop bits </li>
	 * <li>STOPBITS_1_5: 1.5 stop bits </li>
	 * </ul>
	 * @param parity
	 * <ul>
	 * <li>PARITY_NONE: no parity</li>
	 * <li>PARITY_ODD: odd parity </li>
	 * <li>PARITY_EVEN: even parity </li>
	 * <li>PARITY_MARK: mark parity </li>
	 * <li>PARITY_SPACE: space parity </li>
	 * </ul>
	 * @throws UnsupportedCommOperationException if any of the above parameters are specified incorrectly.
	 * All four of the parameters will revert to the values before the call was made.
	 */
	void setSerialPortParams(int baudrate, int dataBits, int stopBits, int parity) throws UnsupportedCommOperationException;
}
