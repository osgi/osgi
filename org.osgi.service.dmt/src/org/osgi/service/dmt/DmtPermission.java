/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.dmt;

import java.security.Permission;
import java.security.PermissionCollection;

// TODO implement methods

/**
 * DmtPermission controls access to management objects in the Device
 * Management Tree (DMT). It is intended to control local access to
 * the DMT. DMTPermission target string identifies the management
 * object URI and the action field lists the OMA DM commands that are
 * permitted on the management object. Example:
 *
 * <pre>
 * DMTPermission(&quot;./OSGi/bundles&quot;, &quot;Add,Replace,Get&quot;);
 * </pre>
 *
 * This means that owner of this permission can execute Add, Replace
 * and Get commands on the ./OSGi/bundles management object. It is
 * possible to use wildcards in both the target and the actions
 * field. Wildcard in the target field means that the owner of the
 * permission can access children nodes of the target node. Example
 *
 * <pre>
 * DMTPermission(&quot;./OSGi/bundles/*&quot;, &quot;Get&quot;);
 * </pre>
 *
 * This means that owner of this permission has Get access on every
 * child node of ./OSGi/bundles. The asterix does not necessarily have
 * to follow a '/' character. For example the 
 * <code>&quot;./OSGi/a*&quot;/<code> target matches the 
 * <code>./OSGi/applications</code> subtree.
 * <p>If wildcard is present in the actions
 * field, all legal OMA DM commands are allowed on the designated
 * nodes(s) by the owner of the permission.
 */
public class DmtPermission extends Permission {
	// TODO add static final serialVersionUID
	//### Actions!!
    /**
     * Creates a new DmtPermission object for the specified DMT URI
     * with the specified actions.
     *
     * @param dmturi URI of the management object (or subtree).
     * @param actions OMA DM actions allowed.
     */
    public DmtPermission(String dmturi, String actions) {
        // TODO
        super("DMTPermission " + dmturi + " " + actions);
    }

    /**
     * Checks two DMTPermission objects for equality. Two
     * DMTPermissions are equal if they have the same target and
     * action strings.
     *
     * @return true if the two objects are equal.
     */
    public boolean equals(Object obj) {
        // TODO
        return true;
    }

    /**
     * Returns the String representation of the action list.
     *
     * @return Action list for this permission object.
     */
    public String getActions() {
        // TODO
        return null;
    }

    /**
     * Returns hash code for this permission object. If two
     * DMTPermission objects are equal according to the equals method,
     * then calling the hashCode method on each of the two
     * DMTPermission objects must produce the same integer result.
     *
     * @return hash code for this permission object.
     */
    public int hashCode() {
        // TODO
        return 0;
    }

    /**
     * Checks if this DMTPermission object &quot;implies&quot; the
     * specified permission.
     *
     * @param p Permission to check.
     * @return true if this DMTPermission object implies the specified
     * permission.
     */
    public boolean implies(Permission p) {
        // TODO
        return true;
    }

    /**
     * Returns a new PermissionCollection object for storing
     * DMTPermission objects.
     *
     * @return the new PermissionCollection.
     */
    public PermissionCollection newPermissionCollection() {
        // TODO
        return null;
    }
}

