/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi Alliance). OSGi Alliance
 * is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGi Alliance BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;


/**
 * Version identifier.
 * 
 * Version instances are immutable.
 *  
 * @version $Revision$
 */
public final class Version implements Comparable {

	private final int major;
	private final int minor;
	private final int micro;
	private final String qualifier;

	private static final String SEPARATOR = "."; //$NON-NLS-1$

	/**
	 * The empty version "0.0.0".  Equivalent to calling new Version(0,0,0).
	 */
	public static Version emptyVersion = new Version(0, 0, 0);	

	/**
	 * Creates a version identifier from its components.
	 * 
	 * @param major major component of the version identifier
	 * @param minor minor component of the version identifier
	 * @param micro micro component of the version identifier
	 */
	public Version(int major, int minor, int micro) {
		this(major, minor, micro, null);
	}

	/**
	 * Creates a version identifier from its components.
	 * 
	 * @param major major component of the version identifier
	 * @param minor minor component of the version identifier
	 * @param micro micro component of the version identifier
	 * @param qualifier qualifier component of the version identifier
	 */
	public Version(int major, int minor, int micro, String qualifier) {
		if (major < 0)
			throw new IllegalArgumentException("Negative major"); //$NON-NLS-1$
		if (minor < 0)
			throw new IllegalArgumentException("Negative minor"); //$NON-NLS-1$
		if (micro < 0)
			throw new IllegalArgumentException("Negative micro"); //$NON-NLS-1$
		if (qualifier == null)
			qualifier = ""; //$NON-NLS-1$

		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
	}

	/**
	 * Creates a version identifier from the given string.
	 * 
	 * @param version string representation of the version identifier. 
	 */
	public Version(String version) {
		// TODO Need to implement parser
		throw new UnsupportedOperationException("unimplemented yet!");
	}

	/**
	 * Compare version identifiers for equality. Identifiers are
	 * equal if all of their components are equal.
	 *
	 * @param object an object to compare
	 * @return whehter or not the two objects are equal
	 */
	public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
		if (!(object instanceof Version)) {
			return false;
		}
		Version other = (Version) object;
		return (other.major == major) && 
		       (other.minor == minor) && 
			   (other.micro == micro) && 
			   other.qualifier.equals(qualifier);
	}

	/**
	 * Returns a hash code value for the object. 
	 *
	 * @return an integer which is a hash code value for this object.
	 */
	public int hashCode() {
		// TODO Perhaps a better hash is needed?
		int code = major + minor + micro; 
		if (qualifier.equals("")) //$NON-NLS-1$
			return code;
		else
			return code + qualifier.hashCode();
	}

	/**
	 * Returns the major component of this 
	 * version identifier.
	 *
	 * @return the major component
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * Returns the minor component of this 
	 * version identifier.
	 *
	 * @return the minor component
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Returns the micro component of this 
	 * version identifier.
	 *
	 * @return the micro component
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * Returns the qualifier component of this 
	 * version identifier.
	 *
	 * @return the qualifier component
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * Returns the string representation of this version identifier. 
	 *
	 * @return the string representation of this version identifier
	 */
	public String toString() {
		String base = major + SEPARATOR + minor + SEPARATOR + micro;
		if (qualifier.equals("")) //$NON-NLS-1$
			return base;
		else
			return base + SEPARATOR + qualifier;
	}

	/**
	 * Compares this Version object with the specified Version object for order.  
	 * Returns a negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.<p>
	 *
	 * @param   o the Version object to be compared.
	 * @return  a negative integer, zero, or a positive integer as this object
	 *		is less than, equal to, or greater than the specified Version object.
	 *
	 * @throws ClassCastException if the specified object's type
	 *         is not Version.
	 */
	public int compareTo(Object o) {
		Version other = (Version)o;
		
		int result = major - other.major;
		if (result != 0)
			return result;
		
		result =  minor - other.minor;
		if (result != 0)
			return result;

		result = micro - other.micro;
		if (result != 0)
			return result;

		return qualifier.compareTo(other.qualifier);
	}
}