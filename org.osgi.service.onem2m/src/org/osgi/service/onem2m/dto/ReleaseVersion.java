package org.osgi.service.onem2m.dto;

/**
 * Enum expressing oneM2M specification version.
 * 
 * This information is introduced after Release 2.0 and 
 * oneM2M uses only R2A,R3_0 (as 2a and 3).
 */
public enum ReleaseVersion {
	/**
	 * Release 1
	 */
	R1_0,
	/**
	 * Release 1.1
	 */
	R1_1,
	/**
	 * Release 2
	 */
	R2_0,
	/**
	 * Release 2A
	 */
	R2A,
	/**
	 * Release 3
	 */
	R3_0;
}
