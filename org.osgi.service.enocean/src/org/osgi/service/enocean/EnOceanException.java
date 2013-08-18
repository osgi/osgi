package org.osgi.service.enocean;

/**
 * This class represents the root exception Object for all the code related to EnOcean. 
 *  
 * @version 1.0
 */
public class EnOceanException extends Exception {


	private static final long	serialVersionUID	= -2401739486671107362L;

	/**
	 * SUCCESS status code.
	 */
	public static final short SUCCESS = 0;

	/**
	 * FAILURE status code.
	 */
	public static final short FAILURE = 1;

	/**
	 * Operation is not supported by EnOcean Serial Protocol.
	 */
	public static final short ESP_RET_NOT_SUPPORTED = 2;

	/**
	 * One of the parameters was badly specified or missing.
	 */
	public static final short ESP_RET_WRONG_PARAM = 3;
	
	/**
	 * The operation was denied.
	 */
	public static final short ESP_RET_OPERATION_DENIED = 4;
	
	/**
	 * The provided telegram was invalid.
	 */
	public static final short INVALID_TELEGRAM = 240;
	
	private final int errorCode;
	
	
	/**
	 * Constructor for EnOceanException
	 * 
	 * @param errordesc exception error description
	 */
	public EnOceanException(String errordesc) {
		super(errordesc);
		errorCode = 0;
	}
	
	/**
	 * Constructor for EnOceanException
	 * 
	 * @param errorCode An error code.
	 */
	public EnOceanException(int errorCode, String errordesc) {
		super(errordesc);
		this.errorCode = errorCode;
	}
	
	/**
	 * Constructor for EnOceanException
	 * 
	 * @return An EnOcean error code, defined by the EnOcean Forum working committee or an EnOcean vendor.
	 */
	public int getEnOceanError_Code() {
		return errorCode;
	}
		

}
