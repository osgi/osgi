/**
 * Copyright (c) 2001 Gatespace AB. All Rights Reserved.
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
import java.security.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;


/**
 * Implementation of the PermissionAdmin service.
 *
 * @see org.osgi.service.permissionadmin.PermissionAdmin
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */

public class PermissionAdminImpl implements PermissionAdmin
{

    final static String SPECIFICATION_VERSION = "1.1";

    final private PermissionInfo[] implicitDefaultPermissions = new PermissionInfo[] {
	new PermissionInfo("java.security.AllPermission", "", "")
    };

    PermissionCollection runtimePermissions;

    private Framework framework;

    private File permFile;

    private long lastPermFile;

    private Hashtable /* String -> PermissionInfo[] */ permissions = new Hashtable();

    private Hashtable /* String -> PermissionCollection */ cache = new Hashtable();

    private PermissionInfo[] explicitDefaultPermissions = null;


    PermissionAdminImpl(Framework fw, File dir)
    {
	framework = fw;
	// Get system default permissions
	PermissionCollection pc = Policy.getPolicy().getPermissions(new CodeSource(null, null));
	// Remove AllPermission
	if (pc != null && pc.implies(new AllPermission())) {
	    runtimePermissions = new Permissions();
	    for (Enumeration e = pc.elements(); e.hasMoreElements();) {
		Permission p = (Permission) e.nextElement();
		if (!(p instanceof AllPermission)) {
		    runtimePermissions.add(p);
		}
	    }
	} else {
	    runtimePermissions = pc;
	}
	permFile = new File(dir, "perms.props");
	load();
    }

    //
    // Interface PermissionAdmin
    //

    /**
     * Gets the permissions assigned to the bundle with the specified
     * location.
     *
     * @param location The location of the bundle whose permissions are to
     * be returned.
     *
     * @return The permissions assigned to the bundle with the specified
     * location, or <tt>null</tt> if that bundle has not been assigned any
     * permissions.
     */
    public PermissionInfo[] getPermissions(String location)
    {
	PermissionInfo[] res = (PermissionInfo[])permissions.get(location);
	return res != null ? (PermissionInfo[])res.clone() : null;
    }

    /**
     * Assigns the specified permissions to the bundle with the specified
     * location.
     *
     * @param location The location of the bundle that will be assigned the
     *                 permissions. 
     * @param perms The permissions to be assigned, or <tt>null</tt>
     * if the specified location is to be removed from the permission table.
     * @exception SecurityException if the caller does not have the
     * <tt>AdminPermission</tt>.
     */
    public synchronized void setPermissions(String location, PermissionInfo[] perms)
    {
	framework.checkAdminPermission();
	PermissionsWrapper w = (PermissionsWrapper) cache.get(location);
	if (w != null) {
	  w.invalidate();
	}
	if (perms != null) {
	    permissions.put(location, perms);
	} else {
	    permissions.remove(location);
	}
	save();
    }


    /**
     * Returns the bundle locations that have permissions assigned to them,
     * that is, bundle locations for which an entry
     * exists in the permission table.
     *
     * @return The locations of bundles that have been assigned any
     * permissions, or <tt>null</tt> if the permission table is empty.
     */
    public String[] getLocations()
    {
	int size = permissions.size();
	if (size == 0) {
	    return null;
	}
	ArrayList res = new ArrayList(size);
	synchronized (permissions) {
	    for (Enumeration e = permissions.keys(); e.hasMoreElements(); ) {
		res.add(e.nextElement());
	    }
	}
	String[] buf = new String[res.size()];
	return (String[])res.toArray(buf);
    }


    /**
     * Gets the default permissions.
     *
     * <p>These are the permissions granted to any bundle that does not
     * have permissions assigned to its location.
     *
     * @return The default permissions, or <tt>null</tt> if default 
     * permissions have not been defined.
     */
    public synchronized PermissionInfo[] getDefaultPermissions()
    {
	return explicitDefaultPermissions == null ? null : (PermissionInfo[])explicitDefaultPermissions.clone();
    }

    public synchronized PermissionInfo[] getImplicitDefaultPermissions()
    {
	return (PermissionInfo[])implicitDefaultPermissions.clone();
    }

    /**
     * Sets the default permissions.
     *
     * <p>These are the permissions granted to any bundle that does not
     * have permissions assigned to its location.
     *
     * @param permissions The default permissions.
     * @exception SecurityException if the caller does not have the
     * <tt>AdminPermission</tt>.
     */
    public synchronized void setDefaultPermissions(PermissionInfo[] permissions)
    {
	framework.checkAdminPermission();
	for (Enumeration e = cache.elements(); e.hasMoreElements();) {
	    ((PermissionsWrapper) e.nextElement()).invalidate();
	}
	explicitDefaultPermissions = permissions;
	save();
    }

    //
    // Package methods
    //

    /**
     * Gets the permissionCollection assigned to the bundle with the specified id.
     * The collection contains the configured permissions for the bundle location
     * plus implict gratnet permissions (i.e FilePermission for the data area).
     *
     * @param bid The bundle id whose permissions are to be returned.
     *
     * @return The permissions assigned to the bundle with the specified
     * location, or the default permissions if that bundle has not been assigned
     * any permissions.
     */
    PermissionCollection getPermissionCollection(Long bid)
    {
	BundleImpl b = framework.bundles.getBundle(bid.longValue());
	if (b != null) {
	    return getPermissionCollection(b);
	} else {
	    return null;
	}
    }


    /**
     * Gets the permissionCollection assigned to the bundle with the specified id.
     * We return a permission wrapper so that we can change it dynamicly.
     *
     * @param bundle The bundle whose permissions are to be returned.
     *
     * @return The permissions assigned to the bundle with the specified
     * location, or the default permissions if that bundle has not been assigned
     * any permissions.
     */
    synchronized PermissionCollection getPermissionCollection(BundleImpl bundle)
    {
	PermissionCollection pc = (PermissionCollection)cache.get(bundle.location);
	if (pc == null) {
	    pc = new PermissionsWrapper(this, bundle);
	    cache.put(bundle.location, pc);
	}
	return pc;
    }

    //
    // Private methods
    //
    
    /**
     * Save permissions.
     *
     */
    private void save()
    {
	final Properties props = new Properties();
	int i = 0;
	for (Iterator e = permissions.entrySet().iterator(); e.hasNext(); i++) {
	    Map.Entry me = (Map.Entry)e.next();
	    PermissionInfo [] pi = (PermissionInfo[])me.getValue();
	    String p = "perm." + i + ".";
	    props.setProperty(p + "location", (String)me.getKey());
	    props.setProperty(p + "size", Integer.toString(pi.length));
	    for (int j = 0; j < pi.length; j++) {
		props.setProperty(p + j, pi[j].toString());
	    }
	}
	if(explicitDefaultPermissions != null) {
		String p = "perm.default.";
		props.setProperty(p + "size", Integer.toString(explicitDefaultPermissions.length));
		for (i = 0; i < explicitDefaultPermissions.length; i++) {
	    		props.setProperty(p + i, explicitDefaultPermissions[i].toString());
		}
	}
	AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    try {
			FileOutputStream fo = new FileOutputStream(permFile);
			props.store(fo, "Permission Info");
			fo.close();
		    } catch (IOException e) {
			// ERROR
		    }
		    return null;
		}
	    });
    }
    
    
    /**
     * Restore all permission data. Only called during start of framework.
     *
     * @param props InputStream of permissions in properties format.
     * @exception Exception If we failed to read data or if data
     *            is corrupted.
     */
    private void load()
    {
	if (permFile.exists()) {
	    try {
		Properties props = new Properties();
		FileInputStream fi = new FileInputStream(permFile);
		props.load(fi);
		fi.close();

		String p = "perm.default.";
		String s = (String)props.remove(p + "size");
		if (s != null) {
		    int size = Integer.parseInt(s);
		    explicitDefaultPermissions = new PermissionInfo[size];
		    for (int i = 0; i < size ; i++) {
			explicitDefaultPermissions[i] = new PermissionInfo((String)props.remove(p + i));
		    }
		    
		}
		while (!props.isEmpty()) {
		    p = (String)props.propertyNames().nextElement();
		    p = p.substring(0, p.lastIndexOf('.') + 1);
		    String loc = (String)props.remove(p + "location");
		    if (loc != null) {
			int size = Integer.parseInt((String)props.remove(p + "size"));
			PermissionInfo [] pi = new PermissionInfo[size];
			for (int i = 0; i < size ; i++) {
			    pi[i] = new PermissionInfo((String)props.remove(p + i));
			}
			permissions.put(loc, pi);
		    } else {
			throw new Exception("Permission file is corrupt, " +
					    "no location for: " + p);
		    }
		}
	    } catch (Exception e) {
		System.err.println("Load of permissions failed: " + e);
	    }
	}
    }
}
