/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;

/**
 * An exported package.
 *
 * Instances implementing this interface are created by the
 * Package Admin service.
 *
 * <p>The information about an exported package provided by
 * this object is valid only until the next time
 * <tt>PackageAdmin.refreshPackages()</tt> is
 * called.
 * If an <tt>ExportedPackage</tt> object becomes stale (that is, the package it references
 * has been updated or removed as a result of calling
 * <tt>PackageAdmin.refreshPackages()</tt>),
 * its <tt>getName()</tt> and <tt>getSpecificationVersion()</tt> continue to return their
 * old values, <tt>isRemovalPending()</tt> returns <tt>true</tt>, and <tt>getExportingBundle()</tt>
 * and <tt>getImportingBundles()</tt> return <tt>null</tt>.
 *
 * @version $Revision$
 */
public interface ExportedPackage {

    /**
     * Returns the name of the package associated with this <tt>ExportedPackage</tt> object.
     *
     * @return The name of this <tt>ExportedPackage</tt> object.
     */
    public String getName();

    /**
     * Returns the bundle exporting the package associated with this <tt>ExportedPackage</tt> object.
     *
     * @return The exporting bundle, or <tt>null</tt> if this <tt>ExportedPackage</tt> object
     *         has become stale.
     */
    public Bundle getExportingBundle();

    /**
     * Returns the resolved bundles that are currently importing the package
     * associated with this <tt>ExportedPackage</tt> object.
     *
     * <p> The returned array always includes the bundle returned by
     * {@link #getExportingBundle}since an exporter always implicitly
     * imports its exported packages.
     *
     * @return The array of resolved bundles currently importing the package
     * associated with this <tt>ExportedPackage</tt> object, or <tt>null</tt> if this <tt>ExportedPackage</tt>
     * object has become stale.
     */
    public Bundle[] getImportingBundles();

    /**
     * Returns the specification version of this <tt>ExportedPackage</tt>, as
     * specified in the exporting bundle's manifest file.
     *
     * @return The specification version of this <tt>ExportedPackage</tt> object, or
     *         <tt>null</tt> if no version information is available.
     */
    public String getSpecificationVersion();

    /**
     * Returns <tt>true</tt> if the package associated with this <tt>ExportedPackage</tt> object has been
     * exported by a bundle that has been updated or uninstalled.
     *
     * @return <tt>true</tt> if the associated package is being
     * exported by a bundle that has been updated or uninstalled, or if this
     * <tt>ExportedPackage</tt> object has become stale; <tt>false</tt> otherwise.
     */
    public boolean isRemovalPending();
}
