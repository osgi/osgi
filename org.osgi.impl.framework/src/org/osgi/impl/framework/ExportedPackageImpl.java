/**
 * Copyright (c) 2001, 2002 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.util.*;

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;


/**
 * An exported package.
 *
 * Instances implementing this interface are created by the
 * {@link PackageAdmin} service.
 * <p> Note that the information about an exported package provided by
 * this class is valid only until the next time 
 * <tt>PackageAdmin.refreshPackages()</tt> is
 * called.
 * If an ExportedPackage becomes stale (that is, the package it references
 * has been updated or removed as a result of calling 
 * PackageAdmin.refreshPackages()),
 * its getName() and getSpecificationVersion() continue to return their
 * old values, isRemovalPending() returns true, and getExportingBundle()
 * and getImportingBundles() return null.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class ExportedPackageImpl implements ExportedPackage
{

    final private PkgEntry pkg;

    ExportedPackageImpl(PkgEntry pkg) {
	this.pkg = pkg;
    }


    /**
     * Returns the name of this <tt>ExportedPackage</tt>.
     *
     * @return The name of this <tt>ExportedPackage</tt>.
     */
    public String getName()
    {
	return pkg.name;
    }


    /**
     * Returns the bundle that is exporting this <tt>ExportedPackage</tt>.
     *
     * @return The exporting bundle, or null if this <tt>ExportedPackage</tt>
     *         has become stale.
     */
    public Bundle getExportingBundle()
    {
	if (pkg.bundle.framework.packages.isProvider(pkg)) {
	    return pkg.bundle;
	} else {
	    return null;
	}
    }


    /**
     * Returns the resolved bundles that are currently importing this
     * <tt>ExportedPackage</tt>.
     *
     * <p> The returned array always includes the bundle returned by
     * {@link #getExportingBundle} since an exporter always implicitly
     * imports its exported packages.
     *
     * @return The array of resolved bundles currently importing this
     * <tt>ExportedPackage</tt>, or null if this <tt>ExportedPackage</tt>
     * has become stale.
     */
    public Bundle[] getImportingBundles()
    {
	Packages packages = pkg.bundle.framework.packages;
	synchronized (packages) {
	    if (packages.isProvider(pkg)) {
		Collection imps = packages.getPackageImporters(pkg.name);
		Bundle[] res = new Bundle[imps.size()];
		return (Bundle[])imps.toArray(res);
	    } else {
		return null;
	    }
	}
    }


    /**
     * Returns the specification version of this <tt>ExportedPackage</tt>, as
     * specified in the exporting bundle's manifest file.
     *
     * @return The specification version of this <tt>ExportedPackage</tt>, or
     *         <tt>null</tt> if no version information is available.
     */
    public String getSpecificationVersion()
    {
	return pkg.version.toString();
    }

  
    /**
     * Returns <tt>true</tt> if this <tt>ExportedPackage</tt> has been
     * exported by a bundle that has been updated or uninstalled.
     *
     * @return <tt>true</tt> if this <tt>ExportedPackage</tt> is being
     * exported by a bundle that has been updated or uninstalled;
     * <tt>false</tt> otherwise.
     */
    public boolean isRemovalPending()
    {
	Packages packages = pkg.bundle.framework.packages;
	synchronized (packages) {
	    if (packages.isProvider(pkg)) {
		return packages.isZombiePackage(pkg);
	    } else {
		return true;
	    }
	}
    }

}
