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

import java.util.*;

import org.osgi.framework.*;


/**
 * Class representing a package.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
class Pkg
{

    final String pkg;

    boolean zombie = false;

    PkgEntry provider = null;

    ArrayList /* PkgEntry */ exporters = new ArrayList(1);

    ArrayList /* PkgEntry */ importers = new ArrayList();

    /**
     * Create package entry.
     */
    Pkg(String pkg) {
	this.pkg = pkg;
    }


    /**
     * Add an exporter entry from this package.
     *
     * @param pe PkgEntry to add.
     */
    synchronized void addExporter(PkgEntry pe) {
	int i = Math.abs(binarySearch(exporters, pe) + 1);
	exporters.add(i, pe);
	pe.pkg = this;
    }


    /**
     * Remove an exporter entry from this package.
     *
     * @param p PkgEntry to remove.
     */
    synchronized boolean removeExporter(PkgEntry p) {
	if (p == provider) {
	    return false;
	}
	for (int i = exporters.size() - 1; i >= 0; i--) {
	    if (p == exporters.get(i)) {
		exporters.remove(i);
		p.pkg = null;
		break;
	    }
	}
	return true;
    }


    /**
     * Add an importer entry to this package.
     *
     * @param pe PkgEntry to add.
     */
    synchronized void addImporter(PkgEntry pe) {
	int i = Math.abs(binarySearch(importers, pe) + 1);
	importers.add(i, pe);
	pe.pkg = this;
    }


    /**
     * Remove an importer entry from this package.
     *
     * @param p PkgEntry to remove.
     */
    synchronized void removeImporter(PkgEntry p) {
	for (int i = importers.size() - 1; i >= 0; i--) {
	    if (p == importers.get(i)) {
		importers.remove(i);
		p.pkg = null;
		break;
	    }
	}
    }


    /**
     * Check if this package has any exporters or importers.
     *
     * @return true if no exporters or importers, otherwise false.
     */
    synchronized boolean isEmpty() {
	return exporters.size() == 0 && importers.size() == 0;
    }

    //
    // Private methods.
    //

    /**
     * Do binary search for a package entry in the list with the same
     * version number add the specifies package entry.
     *
     * @param pl Sorted list of package entries to search.
     * @param p Package entry to search for.
     * @return index of the found entry. If no entry is found, return
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>.  The insertion
     *         point</i> is defined as the point at which the
     *         key would be inserted into the list.
     */
    int binarySearch(List pl, PkgEntry p) {
	int l = 0;
	int u = pl.size()-1;

	while (l <= u) {
	    int m = (l + u)/2;
	    int v = ((PkgEntry)pl.get(m)).compareVersion(p);
	    if (v > 0) {
		l = m + 1;
	    } else if (v < 0) {
		u = m - 1;
	    } else {
		return m;
	    }
	}
	return -(l + 1);
    }

}
