/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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

package org.osgi.service.dmt;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Indicates the callers authority to create DMT sessions in the name of a
 * remote management server. Only protocol adapters communicating with
 * management servers should be granted this permission.
 * <p>
 * <code>DmtPrincipalPermission</code> has no actions or target. The name of
 * the permission is fixed to "DmtPrincipalPermission" for all instances.
 * 
 * @version $Revision$
 */
public class DmtPrincipalPermission extends BasicPermission {
	// TODO add static final serialVersionUID

    /**
     * Creates a new <code>DmtPrincipalPermission</code> object with its name
     * set to "DmtPrincipalPermission".
     */
    public DmtPrincipalPermission() {
        super("DmtPrincipalPermission");
    }

    /**
     * Creates a new <code>DmtPrincipalPermission</code> object for use by the <code>Policy</code>
     * object to instantiate new <code>Permission</code> objects.
     *
     * @param name ignored; always set to "DmtPrincipalPermission"
     * @param actions ignored
     */
    public DmtPrincipalPermission(String name, String actions) {
        this();
    }

    /**
     * Determines if the specified permission is implied by this permission.
     * <p>
     * This method returns <code>true</code> if the specified permission is an
     * instance of <code>DmtPrincipalPermission</code>.
     * 
     * @param p the permission to be checked
     * @return <code>true</code> if the given permission is an instance of
     *         this class; <code>false</code> otherwise.
     */
    public boolean implies(Permission p) {
        return (p instanceof DmtPrincipalPermission);
    }

    /**
     * Determines the equality of two <code>DmtPrincipalPermission</code>
     * objects.
     * <p>
     * Two <code>DmtPrincipalPermission</code> objects are always equal.
     * 
     * @param obj the object being compared for equality with this object
     * @return <code>true</code> if <code>obj</code> is an
     *         <code>DmtPrincipalPermission</code>; <code>false</code>
     *         otherwise.
     */
    public boolean equals(Object obj) {
        return (obj instanceof DmtPrincipalPermission);
    }

    /**
     * Returns a new <code>PermissionCollection</code> object suitable for storing
     * <code>DmtPrincipalPermission</code>s.
     * 
     * @return a new <code>PermissionCollection</code> object
     */
    public PermissionCollection newPermissionCollection() {
        return new DmtPrincipalPermissionCollection();
    }
}

/**
 * Stores a collection of <code>DmtPrincipalPermission</code>s. Because all
 * instances of <code>DmtPrincipalPermission</code> are equal, this collection
 * only stores whether any permissions have been added or not.
 */
final class DmtPrincipalPermissionCollection extends PermissionCollection
{
	// TODO add static final serialVersionUID

    /**
     * True if collection is non-empty.
     *
     * @serial
     */
    private boolean hasElement;

    /**
     * Creates an empty <code>DmtPrincipalPermission</code> object.
     */
    public DmtPrincipalPermissionCollection() {
        hasElement = false;
    }

    /**
     * Adds the specified permission to the
     * <code>DmtPrincipalPermissionCollection</code>. Only instances of
     * <code>DmtPrincipalPermission</code> can be added to this collection.
     * 
     * @param permission the <code>Permission</code> object to add
     * @exception IllegalArgumentException if the permission is not a
     *            <code>DmtPrincipalPermission</code>
     * @exception SecurityException if this permission collection has been
     *            marked read-only
     */

    public void add(Permission permission) {
        if (!(permission instanceof DmtPrincipalPermission))
            throw new IllegalArgumentException("Invalid permission: " +
                                               permission);

        if (isReadOnly())
            throw new SecurityException("Attempt to add a Permission to a " +
                                        "read-only PermissionCollection.");

        hasElement = true;
    }

    /**
     * Determines if the specified set of permissions implies the permissions
     * expressed in the parameter <code>permission</code>.
     * 
     * @param p the <code>Permission</code> object to compare
     * @return <code>true</code> if permission is a proper subset of a
     *         permission in the set; <code>false</code> otherwise
     */

    public boolean implies(Permission p) {
        return(hasElement && (p instanceof DmtPrincipalPermission));
    }

    /**
     * Returns an enumeration of the permissions is this permission collection.
     * 
     * @return an enumeration containing exactly one
     *         <code>DmtPrincipalPermission</code> object if any permissions
     *         have been added to this collection, or an empty enumeration
     *         otherwise
     */
    public Enumeration elements() {
        return new Enumeration() {
            private boolean more = hasElement;
            
            public boolean hasMoreElements() {
                return more;
            }
            
            public Object nextElement()    {
                if (!more)
                    throw new NoSuchElementException();
                
                more = false;
                return new DmtPrincipalPermission();
            }
        };
    }   
}
