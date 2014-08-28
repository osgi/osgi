package org.osgi.impl.service.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.osgi.service.serial.SerialConnection;
import org.osgi.service.serial.UnsupportedCommOperationException;

public class SerialConnectionImpl implements SerialConnection {

    private SerialDeviceImpl serialDevice;
    private InputStream is;
    private OutputStream os;

    private boolean dtr = false;
    private boolean rts = false;
    private int flowcontrol = FLOWCONTROL_NONE;
    private int baudrate = 9600;
    private int dataBits = DATABITS_8;
    private int stopBits = STOPBITS_1;
    private int parity = PARITY_NONE;

    public SerialConnectionImpl(SerialDeviceImpl serialDevice) {

        this.serialDevice = serialDevice;
        this.is = new ByteArrayInputStream("InputStream".getBytes());
        this.os = new ByteArrayOutputStream();
    }

    public void close() {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is = null;
        }

        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            os = null;
        }

        serialDevice.close();
    }

    public InputStream getInputStream() throws IOException {
        if (is == null) {
            throw new IOException("This connection has been closed already.");
        }
        return is;
    }

    public OutputStream getOutputStream() throws IOException {
        if (os == null) {
            throw new IOException("This connection has been closed already.");
        }
        return os;
    }

    public int getBaudRate() {
        return baudrate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public int getFlowControlMode() {
        return flowcontrol;
    }

    public int getParity() {
        return parity;
    }

    public int getStopBits() {
        return stopBits;
    }

    public boolean isDTR() {
        return dtr;
    }

    public boolean isRTS() {
        return rts;
    }

    public void setDTR(boolean dtr) {
        this.dtr = dtr;
    }

    public void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException {
        if (flowcontrol != FLOWCONTROL_NONE
                && flowcontrol != FLOWCONTROL_RTSCTS_IN
                && flowcontrol != FLOWCONTROL_RTSCTS_OUT
                && flowcontrol != FLOWCONTROL_XONXOFF_IN
                && flowcontrol != FLOWCONTROL_XONXOFF_OUT) {
            throw new UnsupportedCommOperationException("The flowcontrol is invalid.");
        }

        this.flowcontrol = flowcontrol;
    }

    public void setRTS(boolean rts) {
        this.rts = rts;
    }

    public void setSerialPortParams(int baudrate, int dataBits, int stopBits, int parity) throws UnsupportedCommOperationException {
        if (baudrate <= 0) {
            throw new UnsupportedCommOperationException();
        }

        if (dataBits != DATABITS_5
                && dataBits != DATABITS_6
                && dataBits != DATABITS_7
                && dataBits != DATABITS_8) {
            throw new UnsupportedCommOperationException("The dataBits is invalid.");
        }

        if (stopBits != STOPBITS_1
                && stopBits != STOPBITS_1_5
                && stopBits != STOPBITS_2) {
            throw new UnsupportedCommOperationException("The stopBits is invalid.");
        }

        if (parity != PARITY_NONE
                && parity != PARITY_ODD
                && parity != PARITY_EVEN
                && parity != PARITY_MARK
                && parity != PARITY_SPACE) {
            throw new UnsupportedCommOperationException("The parity is invalid.");
        }

        this.baudrate = baudrate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }
}
