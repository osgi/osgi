/**
 * Copyright (c) 2002 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.framework;

import org.osgi.framework.Constants;

/**
 * Data structure for saving package info. Contains package name, current
 * provider, list of possible exports and all bundles importing this package.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
class PkgEntry {
	final String		name;
	final BundleImpl	bundle;
	final VersionNumber	version;
	// Link to pkg entry
	Pkg					pkg	= null;

	/**
	 * Create package entry.
	 */
	PkgEntry(String p, String v, BundleImpl b) {
		this.name = p;
		this.version = new VersionNumber(v);
		this.bundle = b;
	}

	/**
	 * Create package entry.
	 */
	PkgEntry(String p, VersionNumber v, BundleImpl b) {
		this.name = p;
		this.version = v;
		this.bundle = b;
	}

	/**
	 * Create package entry.
	 */
	PkgEntry(PkgEntry pe) {
		this.name = pe.name;
		this.version = pe.version;
		this.bundle = pe.bundle;
	}

	/**
	 * Package name equal.
	 * 
	 * @param other Package entry to compare to.
	 * @return true if equal, otherwise false.
	 */
	boolean packageEqual(PkgEntry other) {
		return name.equals(other.name);
	}

	/**
	 * Version compare object to another PkgEntry.
	 * 
	 * @param obj Version to compare to.
	 * @return Return 0 if equals, negative if this object is less than obj and
	 *         positive if this object is larger then obj.
	 * @exception ClassCastException if object is not a PkgEntry object.
	 */
	public int compareVersion(Object obj) throws ClassCastException {
		PkgEntry o = (PkgEntry) obj;
		return version.compareTo(o.version);
	}

	/**
	 * String describing package name and specification version, if specified.
	 * 
	 * @return String.
	 */
	public String pkgString() {
		if (version.isSpecified()) {
			return name + ";" + Constants.PACKAGE_SPECIFICATION_VERSION + "="
					+ version;
		}
		else {
			return name;
		}
	}

	/**
	 * String describing this object.
	 * 
	 * @return String.
	 */
	public String toString() {
		return pkgString() + "(Bundle#" + bundle.id + ")";
	}

	/**
	 * Check if object is equal to this object.
	 * 
	 * @param obj Package entry to compare to.
	 * @return true if equal, otherwise false.
	 */
	public boolean equals(Object obj) throws ClassCastException {
		PkgEntry o = (PkgEntry) obj;
		return name.equals(o.name) && bundle == o.bundle
				&& version.equals(o.version);
	}

	/**
	 * Hash code for this package entry.
	 * 
	 * @return int value.
	 */
	public int hashCode() {
		return name.hashCode() + bundle.hashCode() + version.hashCode();
	}
}
