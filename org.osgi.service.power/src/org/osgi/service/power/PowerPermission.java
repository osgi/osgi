/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.power;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
* Indicates a bundle's authority to set a new power state to the system
* and/or devices.
* <ul>
* <li>A <code>PowerPermission</code> with the name "system" allows
* a bunlde to set a new power state to the system.
* <li>A <code>PowerPermission</code> with the name "*" allows
* a bunlde to set a new power state to the system and to any devices.
* <li>A <code>PowerPermission</code> with the name "&lt;&lt;ALL DEVICES>>" allows
* a bunlde to set a new power state to any devices.
* <li>A <code>PowerPermission</code> with any other names allows
* a bunlde to set a new power state to the device that has the same id.
* 
* @version $Revision$
*/
public class PowerPermission extends BasicPermission {

	private transient String name;
	private transient boolean system;
	private transient boolean allDevices;

    /**
     * Initializes internal values according to the name entered 
     * for the permission.
     */
    private void init() 
    {
    	if ((name = getName()) == null) 
    		throw new NullPointerException("name can't be null");

    	// Check if the name is set to all devices
    	if (name.equals("<<ALL DEVICES>>")) {
    		allDevices = true;
    		return;
    	}
	
    	// Check if the name is set to system
    	if (name.equalsIgnoreCase("system")) {
    		system = true;
    		return;
    	}
	
    	// Check if the name is set to wildcard
    	if (name.equals("*")) {
    		system = true;
    		allDevices = true;
    	}
    }

	/**
	 * Creates a new PowerPermission with the specified name. 
	 * The name is the id of the system or the device that the user is allowed to change
	 * the power state.
	 * 
	 * There are some special names that can be used:
	 * - The String "system" must be used to allow the user to set the power state of the
	 * system.
	 * - "&lt;&lt;ALL DEVICES>>" can be used to apply the permissions to all devices registered within the
	 * framework
	 * - "*" can be used
	 * - any other String are seen as a device id and the permission is applied only on this device  
	 * 
	 * @param name identification of the system (only "system" is allowed) or the device. 
	 * It can contain "*" or "&lt;&lt;ALL DEVICES>>"
	 */
    public PowerPermission(String name) {
		super(name);
		init();
	}
	
	/**
	 * Creates a new PowerPermission with the specified name. 
	 * The name is the id of the system or the device that the user is allowed to change
	 * the power state.
	 * 
	 * There are some special names that can be used:
	 * - The String "system" must be used to allow the user to set the powert state of the
	 * system.
	 * - "&lt;&lt;ALL DEVICES>>" can be used to apply the permissions to all devices registered within the
	 * framework
	 * - "*" can be used
	 * - any other String are seen as a device id and the permission is applied only on this device  
	 * 
	 * @param name identification of the system or the device. It can contain "*" or "&lt;&lt;ALL DEVICES>>"
	 * @param actions currently unused, should be null
	 */
	public PowerPermission(String name, String actions) {
		super(name, actions);
		init();
	}
	
	/**
	 * Determines if a <code>PowerPermission</code> object "implies" the
	 * specified permission.
	 * 
	 * @param p The target permission to check.
	 * @return <code>true</code> if the specified permission is implied by this
	 * object; <code>false</code> otherwise.
	 */
    public boolean implies(Permission p) {
    	if (p instanceof PowerPermission) {
    		PowerPermission that = (PowerPermission) p;    		
    		
    		return ((this.system = that.system) && 
    				(this.allDevices == that.allDevices) && 
					super.implies(p));
    	}

    	return (false);
    }

    
	/**
	 * Returns a new <code>PermissionCollection</code> object for storing
	 * <code>PowerPermission</code> objects.
	 *
	 * @return A new <code>PermissionCollection</code> object suitable for storing
	 * <code>PowerPermission</code> objects.
	 */
	public PermissionCollection newPermissionCollection() {
		return (new PowerPermissionCollection());
	}
}


/**
 * Stores a set of <code>PowerPermission</code> permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */

final class PowerPermissionCollection extends PermissionCollection {
	
	static final long	serialVersionUID	= 662615640374640621L;
	
	private Hashtable	perms;
	private boolean		all_allowed;
	
	/**
	 * Create a new <code>PowerPermissionCollection</code> object.
	 *
	 */
	public PowerPermissionCollection() {
		perms = new Hashtable();
		all_allowed = false;
	}

	/**
	 * Adds a permission to the <code>PowerPermission</code> objects using the
	 * key for the hash as the name.
	 * 
	 * @param permission The Permission object to add.
	 * 
	 * @exception IllegalArgumentException If the permission is not a
	 *            PowerPermission object.
	 * 
	 * @exception SecurityException If this <code>PowerPermissionCollection</code>
	 *            object has been marked read-only.
	 */	
	public void add(Permission permission) {
		if (!(permission instanceof PowerPermission))
			throw new IllegalArgumentException("invalid permission: " + permission);
		
		if (isReadOnly())
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");

		String name = permission.getName();
		perms.put(name, permission);
		
		if (!all_allowed) {
			if (name.equals("*"))
				all_allowed = true;
		}
	}
	
	/**
	 * Determines if a set of permissions implies the permissions expressed in
	 * <code>permission</code>.
	 * 
	 * @param permission The Permission object to compare.
	 * 
	 * @return <code>true</code> if <code>permission</tt> is a proper subset of a
	 *         permission in the set; <code>false</tt> otherwise.
	 */
	public boolean implies(Permission permission) {
		if (!(permission instanceof PowerPermission))
	   		return false;

		PowerPermission that = (PowerPermission) permission;

		// short circuit if the "*" Permission was added
		if (all_allowed)
		    return true;

		// strategy:
		// Check for full match first. Then work our way up the
		// path looking for matches on a.b..*

		String name = that.getName();

		PowerPermission x = (PowerPermission) perms.get(name);

		if (x != null) {
		    // we have a direct hit!
		    return x.implies(permission);
		}

		// If we have <<ALL DEVICES>> in the set then it implies
		// any string except "system"
		x = (PowerPermission) perms.get("<<ALL DEVICES>>");
		if ((x != null) && !(that.getName().equalsIgnoreCase("system")))
			return true; 
		
		// work our way up the tree...
		int last, offset;

		offset = name.length()-1;

		while ((last = name.lastIndexOf(".", offset)) != -1) {

		    name = name.substring(0, last+1) + "*";
		    //System.out.println("check "+path);
		    x = (PowerPermission) perms.get(name);

		    if (x != null) {
			return x.implies(permission);
		    }
		    offset = last -1;
		}

		// we don't have to check for "*" as it was already checked
		// at the top (all_allowed), so we just return false
		return false;
	}
	
	/**
	 * Returns an enumeration of all <code>PowerPermission</tt> objects in the
	 * container.
	 * 
	 * @return Enumeration of all <code>PowerPermission</code> objects.
	 */
	public Enumeration elements() {
		return (perms.elements());
	}
}	
