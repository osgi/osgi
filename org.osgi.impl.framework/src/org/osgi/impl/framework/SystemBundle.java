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

import java.io.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.service.url.URLStreamHandlerService;

/**
 * Implementation of the System Bundle object.
 *
 * @see org.osgi.framework.Bundle
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class SystemBundle extends BundleImpl
{
    /**
     * Property name for telling which packages framework exports.
     */
    private final static String SYSPKG = "org.osgi.framework.system.packages";
  
    private HeaderDictionary headers = null;

    /**
     * Construct a the System Bundle handle.
     *
     * @param fw Framework for this bundle.
     */
    SystemBundle(Framework fw)
    {
	super(fw, 0, Constants.SYSTEM_BUNDLE_LOCATION, fw.getClass().getProtectionDomain());
	state = STARTING;
        startLevel = 0;
        isPersistentlyStarted = true;
	StringBuffer sp = new StringBuffer(System.getProperty(SYSPKG, ""));
	if (sp.length() > 0) {
	    sp.append(",");
	}
	// Set up org.osgi.framework package
	String name = Bundle.class.getName();
	name = name.substring(0, name.lastIndexOf('.'));
	sp.append(name + ";" + Constants.PACKAGE_SPECIFICATION_VERSION +
		  "=" + Framework.SPECIFICATION_VERSION);
	// Set up packageadmin package
	name = PackageAdmin.class.getName();
	name = name.substring(0, name.lastIndexOf('.'));
	sp.append("," + name + ";" + Constants.PACKAGE_SPECIFICATION_VERSION +
		  "=" +  PackageAdminImpl.SPECIFICATION_VERSION);
	// Set up permissionadmin package
	name = PermissionAdmin.class.getName();
	name = name.substring(0, name.lastIndexOf('.'));
	sp.append("," + name + ";" + Constants.PACKAGE_SPECIFICATION_VERSION +
		  "=" +  PermissionAdminImpl.SPECIFICATION_VERSION);
	// Set up startlevel package
	name = StartLevel.class.getName();
	name = name.substring(0, name.lastIndexOf('.'));
	sp.append("," + name + ";" + Constants.PACKAGE_SPECIFICATION_VERSION +
		  "=" +  StartLevelImpl.SPECIFICATION_VERSION);
  // Setup url handler package
  name = URLStreamHandlerService.class.getName();
  name = name.substring(0, name.lastIndexOf('.'));
  sp.append("," + name + ";" + Constants.PACKAGE_SPECIFICATION_VERSION +
    "=" +  URLHandler.SPECIFICATION_VERSION);
	bpkgs = new BundlePackages(this, sp.toString(), null, null);
	bpkgs.registerPackages();
	bpkgs.resolvePackages();
    }

    //
    // Bundle interface
    //

    /**
     * Start this bundle.
     *
     * @see org.osgi.framework.Bundle#start
     */
    synchronized public void start()
    {
	framework.checkAdminPermission();
    }


    /**
     * Stop this bundle.
     *
     * @see org.osgi.framework.Bundle#stop
     */
    synchronized public void stop() throws BundleException
    {
	framework.checkAdminPermission();
	Main.shutdown();
    }


    /**
     * Update this bundle.
     *
     * @see org.osgi.framework.Bundle#update
     */
    synchronized public void update(InputStream in) throws BundleException
    {
	framework.checkAdminPermission();
	Main.restart();
    }


    /**
     * Uninstall this bundle.
     *
     * @see org.osgi.framework.Bundle#uninstall
     */
    synchronized public void uninstall() throws BundleException
    {
	framework.checkAdminPermission();
	throw new BundleException("uninstall of System bundle is not allowed");
    }
    
    
    /**
     * Get header data. Simulate EXPORT-PACKAGE.
     *
     * @see org.osgi.framework.Bundle#getHeaders
     */
    public Dictionary getHeaders()
    {
	framework.checkAdminPermission();
	if (headers == null) {
	    headers = new HeaderDictionary();
	    headers.put(Constants.BUNDLE_NAME, Constants.SYSTEM_BUNDLE_LOCATION);
	    headers.put(Constants.EXPORT_PACKAGE, framework.packages.systemPackages());
	}
	return new HeaderDictionary(headers);
    }

    //
    // Package method
    //

    /**
     * Get class loader for this bundle.
     *
     * @return Framework classloader.
     */
    ClassLoader getClassLoader()
    {
	return getClass().getClassLoader();
    }


    /**
     * Set system bundle state to active
     */
    void systemActive()
    {
	state = ACTIVE;
    }


    /**
     * Set system bundle state to stopping
     */
    void systemShuttingdown()
    {
	state = STOPPING;
    }

}
