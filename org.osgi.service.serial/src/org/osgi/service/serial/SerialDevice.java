package org.osgi.service.serial;

/**
 * SerialDevice is an interface to express a device performing serial communication.
 */
public interface SerialDevice {

    /**
     * The value string of service property, when information is not available.<br>
     */
    String EMPTY_STRING = "";

    /**
     * Constant for the value of the service property {@code DEVICE_CATEGORY} used for all Serial devices.
     * Value is "Serial".
     */
    String DEVICE_CATEGORY = "Serial";

    /**
     * The key string of "serial.comport" service property.<br>
     * Represents the name of the port.<br>
     * The value is String.<br>
     * Example1: "/dev/ttyUSB0"<br>
     * Example2: "COM5"<br>
     * Example3: "/dev/tty.usbserial-XXXXXX"<br>
     */
    String SERIAL_COMPORT = "serial.comport";

    /**
     * The key string of "current.owner" service property.<br>
     * Represents the owner of the port.<br>
     * The value is String.<br>
     * {@link #EMPTY_STRING} if no owner is available.<br>
     */
    String CURRENT_OWNER = "current.owner";

    /**
     * Optional.<br>
     * The key string of "bus.type" service property.<br>
     * Represents underlying technology such as USB-Serial.<br>
     * The value is String.<br>
     */
    String BUS_TYPE = "bus.type";

    /**
     * Opens the communications port.<br>
     * Open obtains exclusive ownership of the port.<br>
     *
     * @param appname Name of application making this call. This name will become the owner of the port. Useful when resolving ownership contention.
     * @param timeout Time in milliseconds to block waiting for port open.
     * @return SerialConnection
     * @throws PortInUseException if the port is in use by some other application that is not willing to relinquish ownership
     */
    SerialConnection open(java.lang.String appname, int timeout) throws PortInUseException;
}
