package org.osgi.service.onem2m.dto;

/**
 * enum expresses oneM2M specification version.
 * <p>
 * This information is introduced after Release 2.0 and oneM2M uses only R2A,
 * R3_0 (as 2a and 3).
 * 
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L450-455">oneM2M
 *      XSD releaseVersion</a>
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
	R3_0,
	/**
	 * Release 4 (reserved for future)
	 */
	R4_0,
	/**
	 * Release 5 (reserved for future)
	 */
	R5_0;

}
