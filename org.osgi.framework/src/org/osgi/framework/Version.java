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

import java.util.*;

/**
 * Version identifier for bundles and packages.
 * 
 * <p>
 * Version instances are immutable.
 * 
 * @version $Revision$
 * @since 1.3
 */

public final class Version implements Comparable {

	private final int			major;
	private final int			minor;
	private final int			micro;
	private final String		qualifier;

	/* cached result of toString */
	private String				string;

	private static final String	SEPARATOR		= ".";					//$NON-NLS-1$

	/**
	 * The empty version "0.0.0". Equivalent to calling
	 * <tt>new Version(0,0,0)</tt>.
	 */
	public static Version		emptyVersion	= new Version(0, 0, 0);

	/**
	 * Creates a version identifier from its components.
	 * 
	 * @param major Major component of the version identifier.
	 * @param minor Minor component of the version identifier.
	 * @param micro Micro component of the version identifier.
	 * @throws IllegalArgumentException If the numerical components are
	 *                   negative.
	 */
	public Version(int major, int minor, int micro) {
		this(major, minor, micro, null);
	}

	/**
	 * Creates a version identifier from its components.
	 * 
	 * @param major Major component of the version identifier.
	 * @param minor Minor component of the version identifier.
	 * @param micro Micro component of the version identifier.
	 * @param qualifier Qualifier component of the version identifier.
	 * @throws IllegalArgumentException If the numerical components are negative
	 *                   or the qualifier string is invalid.
	 */
	public Version(int major, int minor, int micro, String qualifier) {
		if (major < 0) {
			throw new IllegalArgumentException("negative major"); //$NON-NLS-1$
		}
		if (minor < 0) {
			throw new IllegalArgumentException("negative minor"); //$NON-NLS-1$
		}
		if (micro < 0) {
			throw new IllegalArgumentException("negative micro"); //$NON-NLS-1$
		}
		if (qualifier == null) {
			qualifier = ""; //$NON-NLS-1$
		}

		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = validateQualifier(qualifier);
	}

	/**
	 * Creates a version identifier from the given string.
	 * 
	 * @param version String representation of the version identifier.
	 * @throws IllegalArgumentException If the version string is improperly
	 *                   formatted.
	 */
	public Version(String version) {
		int major = 0;
		int minor = 0;
		int micro = 0;
		String qualifier = ""; //$NON-NLS-1$

		try {
			StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
			major = validateNumber(st.nextToken());

			if (st.hasMoreTokens()) {
				st.nextToken(); // consume delimiter
				minor = validateNumber(st.nextToken());

				if (st.hasMoreTokens()) {
					st.nextToken(); // consume delimiter
					micro = validateNumber(st.nextToken());

					if (st.hasMoreTokens()) {
						st.nextToken(); // consume delimiter
						qualifier = validateQualifier(st.nextToken());

						if (st.hasMoreTokens()) {
							throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$
						}
					}
				}
			}
		}
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$
		}

		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
	}

	private static int validateNumber(String number) {
		int num = Integer.parseInt(number);
		if (num < 0) {
			throw new IllegalArgumentException("invalid format"); //$NON-NLS-1$
		}
		return num;
	}

	private static String validateQualifier(String qualifier) {
		int length = qualifier.length();
		for (int i = 0; i < length; i++) {
			char c = qualifier.charAt(i);
			if (!(Character.isLetter(c) || Character.isDigit(c) || (c == '_') || (c == '-'))) {
				throw new IllegalArgumentException("invalid qualifier"); //$NON-NLS-1$
			}
		}
		return qualifier;
	}

	/**
	 * Returns the major component of this version identifier.
	 * 
	 * @return The major component.
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * Returns the minor component of this version identifier.
	 * 
	 * @return The minor component.
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Returns the micro component of this version identifier.
	 * 
	 * @return The micro component.
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * Returns the qualifier component of this version identifier.
	 * 
	 * @return The qualifier component.
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * Returns the string representation of this version identifier.
	 * 
	 * @return The string representation of this version identifier.
	 */
	public String toString() {
		if (string == null) {
			String base = major + SEPARATOR + minor + SEPARATOR + micro;
			if (qualifier.equals("")) { //$NON-NLS-1$
				string = base;
			}
			else {
				string = base + SEPARATOR + qualifier;
			}
		}
		return string;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return An integer which is a hash code value for this object.
	 */
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Compares this <tt>Version</tt> object to another object.
	 * 
	 * @param object The object to compare against this <tt>Version</tt>
	 *                   object.
	 * @return <tt>true</tt> if the object is a <tt>Version</tt> object and
	 *                 has the same major, minor, micro and qualifier to this object;
	 *                 <tt>false</tt> otherwise.
	 */
	public boolean equals(Object object) {
		if (object == this) { //quicktest
			return true;
		}

		if (!(object instanceof Version)) {
			return false;
		}

		Version other = (Version) object;
		return (major == other.major) && (minor == other.minor)
				&& (micro == other.micro) && qualifier.equals(other.qualifier);
	}

	/**
	 * Compares this <tt>Version</tt> object with the specified
	 * <tt>Version</tt> object for order. Returns a negative integer, zero, or
	 * a positive integer if this object is less than, equal to, or greater than
	 * the specified <tt>Version</tt> object.
	 * <p>
	 * 
	 * @param object The <tt>Version</tt> object to be compared.
	 * @return A negative integer, zero, or a positive integer as this object is
	 *                 less than, equal to, or greater than the specified
	 *                 <tt>Version</tt> object.
	 * @throws ClassCastException If the specified object's type is not
	 *                   <tt>Version</tt>.
	 */
	public int compareTo(Object object) {
		if (object == this) { //quicktest
			return 0;
		}

		Version other = (Version) object;

		int result = major - other.major;
		if (result != 0) {
			return result;
		}

		result = minor - other.minor;
		if (result != 0) {
			return result;
		}

		result = micro - other.micro;
		if (result != 0) {
			return result;
		}

		return qualifier.compareTo(other.qualifier);
	}
}