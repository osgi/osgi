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
 * Class representing all packages imported and exported.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
class BundlePackages
{

    final BundleImpl bundle;

    private ArrayList /* PkgEntry */ exports = new ArrayList(1);

    private ArrayList /* PkgEntry */ imports = new ArrayList(1);

    private ArrayList /* String */ dImportPatterns = new ArrayList(1);

    private HashMap /* String -> PkgEntry */ okImports = null;

    private String failReason = null;

    /**
     * Create BundlePackages entry.
     */
    BundlePackages(BundleImpl b, String exportStr, String importStr, String dimportStr) {
	this.bundle = b;
	try {
	    Iterator i = Framework.parseEntries(exportStr, true);
	    while (i.hasNext()) {
		Map e = (Map)i.next();
		PkgEntry p = new PkgEntry((String)e.get("key"), 
					  (String)e.get(Constants.PACKAGE_SPECIFICATION_VERSION),
					  bundle);
		exports.add(p);
		imports.add(new PkgEntry(p));
	    }
	} catch (IllegalArgumentException e) {
	    b.framework.listeners.frameworkError(b, e);
	}
	try {
	    Iterator i = Framework.parseEntries(importStr, true);
	wloop:
	    while (i.hasNext()) {
		Map e = (Map)i.next();
		PkgEntry pe = new PkgEntry((String)e.get("key"), 
					   (String)e.get(Constants.PACKAGE_SPECIFICATION_VERSION),
					   bundle);
		for (int x = imports.size() - 1; x >= 0; x--) {
		    PkgEntry ip = (PkgEntry)imports.get(x);
		    if (pe.packageEqual(ip)) {
			if (pe.compareVersion(ip) < 0) {
			    imports.set(x, pe);
			}
			continue wloop;
		    }
		}
		imports.add(pe);
	    }
	} catch (IllegalArgumentException e) {
	    b.framework.listeners.frameworkError(b, e);
	}
	dImportPatterns.add("java.");
	try {
	    Iterator i = Framework.parseEntries(dimportStr, true);
	    while (i.hasNext()) {
		Map e = (Map)i.next();
		String key = (String)e.get("key");
		if (key.equals("*")) {
		    dImportPatterns = null;
		} else if (key.endsWith(".*")) {
		    dImportPatterns.add(key.substring(0, key.length() - 1));
		} else if (key.endsWith(".")) {
		    b.framework.listeners.frameworkError(b, new IllegalArgumentException(
			 Constants.DYNAMICIMPORT_PACKAGE + " entry ends with '.': " + key));
		} else if (key.indexOf("*") != - 1) {
		    b.framework.listeners.frameworkError(b, new IllegalArgumentException(
			 Constants.DYNAMICIMPORT_PACKAGE + " entry contains a '*': " + key));
		} else {
		    dImportPatterns.add(key);
		}
	    }
	} catch (IllegalArgumentException e) {
	    b.framework.listeners.frameworkError(b, e);
	}
    }


    /**
     * Register bundle packages in framework.
     *
     */
    synchronized void registerPackages() {
	bundle.framework.packages.registerPackages(exports.iterator(), imports.iterator());
    }


    /**
     * Unregister bundle packages in framework. If we find exported packages
     * that has been selected as providers don't unregister them unless the
     * parameter force is true. If not all exporters were removed, the don't
     * remove any importers
     *
     * @param force If true force unregistration of package providers.
     * @return True if all packages were succesfully unregistered,
     *         otherwise false.
     */
    synchronized boolean unregisterPackages(boolean force) {
	Iterator i = (okImports != null ? okImports.values() : imports).iterator();
	if (bundle.framework.packages.unregisterPackages(exports.iterator(), i, force)) {
	    okImports = null;
	    return true;
	} else {
	    return false;
	}
    }


    /**
     * Resolve all the bundles packages.
     *
     * @return true if we resolved all packages. If we failed
     *         return false. Reason for fail can be fetched with
     *         getResolveFailReason().
     */
    synchronized boolean resolvePackages() {
	if (bundle.framework.checkPermissions) {
	    String e = checkPackagePermission(exports, PackagePermission.EXPORT);
	    if (e != null) {
		failReason = "missing export permission for package(s): " + e;
		return false;
	    }
	    String i = checkPackagePermission(imports, PackagePermission.IMPORT);
	    if (i != null) {
		failReason = "missing import permission for package(s): " + i;
		return false;
	    }
	}
	List m = bundle.framework.packages.checkResolve(bundle, imports.iterator());
	if (m != null) {
	    StringBuffer r = new StringBuffer("missing package(s) or can not resolve all of the them: ");
	    Iterator mi = m.iterator();
	    r.append(((PkgEntry)mi.next()).pkgString());
	    while (mi.hasNext()) {
		r.append(", ");
		r.append(((PkgEntry)mi.next()).pkgString());
	    }
	    failReason = r.toString();
	    return false;
	} else {
	    failReason = null;
	    okImports = new HashMap();
	    for (Iterator i = imports.iterator(); i.hasNext(); ) {
		PkgEntry pe = (PkgEntry)i.next();
		okImports.put(pe.name, pe);
	    }
	    return true;
	}
    }


    /**
     * If bundle packages has been resolved look for a bundle
     * that provides the requested package.
     * If found, check we import it. If not imported, check
     * if we can dynamicly import the package.
     *
     * @param pkg Package name
     * @return Bundle exporting
     */
    synchronized BundleImpl getProviderBundle(String pkg) {
	if (okImports == null) {
	    return null;
	}
	PkgEntry pe = (PkgEntry)okImports.get(pkg);
	if (pe != null) {
	    Pkg p = pe.pkg;
	    if (p != null) {
		PkgEntry ppe = p.provider;
		if (ppe != null) {
		    return ppe.bundle;
		}
	    }
	    return null;
	}
	boolean match = false;
	if (dImportPatterns == null) {
	    match = true;
	} else {
	    for (Iterator i = dImportPatterns.iterator(); i.hasNext(); ) {
		String ps = (String)i.next();
		if ((ps.endsWith(".") && pkg.startsWith(ps)) || pkg.equals(ps)) {
		    match = true;
		    break;
		}
	    }
	}
	if (match && checkPackagePermission(pkg, PackagePermission.IMPORT)) {
	    pe = new PkgEntry(pkg, (String)null, bundle);
	    PkgEntry ppe = bundle.framework.packages.registerDynamicImport(pe);
	    if (ppe != null) {
		okImports.put(pkg, pe);
		return ppe.bundle;
	    }
	}
	return null;
    }


    /**
     * Get an iterator over all exported packages.
     *
     * @return An Iterator over PkgEntry.
     */
    Iterator getExports() {
	return exports.iterator();
    }


    /**
     * Get an iterator over all staticly imported packages.
     *
     * @return An Iterator over PkgEntry.
     */
    Iterator getImports() {
	return imports.iterator();
    }


    /**
     * Return a string with a reason for why resolve failed.
     *
     * @return A error message string.
     */
    synchronized String getResolveFailReason() {
	return failReason;
    }

    //
    // Private methods
    //

    /**
     * Check that we have right package permission for a list of packages.
     *
     * @param pkgs List over all packages to check. Data is
     *             Map.Entry with a List of package names as key
     *             and a Map of parameters as value.
     * @param perm Package permission action to check against.
     * @return Returns null if we have correct permission for listed package.
     *         Otherwise a string of failed entries.
     */
    private String checkPackagePermission(List pkgs, String perm) {
	String res = null;
	for (Iterator i = pkgs.iterator(); i.hasNext();) {
	    PkgEntry p = (PkgEntry)i.next();
	    if (!checkPackagePermission(p.name, perm)) {
		if (res != null) {
		    res = res + ", " + p.name;
		} else {
		    res = p.name;
		}
	    }
	}
	return res;
    }


    /**
     * Check that we have right package permission for a package.
     *
     * @param pkg Packages to check. 
     * @param perm Package permission action to check against.
     * @return Returns true if we have permission.
     */
    private boolean checkPackagePermission(String pkg, String perm) {
	if (bundle.framework.checkPermissions) {
	    return bundle.framework.permissions.getPermissionCollection(bundle)
		.implies(new PackagePermission(pkg, perm));
	} else {
	    return true;
	}
    }

}
