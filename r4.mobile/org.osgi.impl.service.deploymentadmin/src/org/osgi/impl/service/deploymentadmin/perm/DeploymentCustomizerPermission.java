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
package org.osgi.impl.service.deploymentadmin.perm;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * The <code>DeploymentCustomizerPermission</code> permission gives the right to 
 * Resource Processors to access a bundle's private area.<p>
 * The Resource Processor that has this permission is allowed to access the bundle's 
 * private area by calling the {@link DeploymentSession#getDataFile} method. The 
 * Resource Processor will have <code>FilePermission</code> with "read", "write" and "delete" 
 * actions for the returned {@link java.io.File} and its subdirectories.<p>
 * The actions string is converted to lowercase before processing.
 */
public class DeploymentCustomizerPermission extends Permission {
    
    private static final Vector ACTIONS = new Vector();
    static {
        ACTIONS.add(org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.
                PRIVATEAREA.toLowerCase());
    }
    
    private           String actions;
    private transient Vector actionsVector;
    private transient Representation rep;

    /**
     * Creates a new <code>DeploymentCustomizerPermission</code> object for the given 
     * <code>name</code> and <code>action</code>.<p>
     * The name parameter is a filter string. This filter has the same syntax as an OSGi filter 
     * but only the "name" attribute is allowed. The value of the attribute  
     * is a bundle Symbolic Name that represents a bundle. The only allowed action is the 
     * "privatearea" action. E.g.<p>
     * <pre>
     * 		Permission perm = new DeploymentCustomizerPermission("(name=com.acme.bundle)", "privatearea");
     * </pre>
     * The Resource Processor that has this permission is allowed to access the bundle's 
     * private area by calling the {@link DeploymentSession#getDataFile} method. The 
     * Resource Processor will have <code>FilePermission</code> with "read", "write" and "delete" 
     * actions for the returned {@link java.io.File} and its subdirectories.
     * @param name Symbolic name of the target bundle, must not be null.
     * @param action Action string (only the "privatearea" action is valid), must not be null.
     * @throws IllegalArgumentException if the filter is invalid, the list of actions 
     *         contains unknown operations or one of the parameters is null
     */
    public DeploymentCustomizerPermission(String name, String actions) {
        super(name);
        
        if (null == name || null == actions)
            throw new IllegalArgumentException("Neither of the parameters can be null");
        
        if ("*".equals(actions.trim())) {
        	StringBuffer sb = new StringBuffer();
        	for (Iterator iter = ACTIONS.iterator(); iter.hasNext();) {
				String action = (String) iter.next();
				sb.append(action + ", ");
			}
       		sb.delete(sb.length() - 2, sb.length());
        	this.actions = sb.toString();
        } else
        	this.actions = actions;
        
        rep = new Representation(getName());
        
        check();
    }

    /**
     * Checks two DeploymentCustomizerPermission objects for equality. 
     * Two permission objects are equal if: <p>
     * - their target filters 
     * are equal and<p>
     * - their actions are the same. 
     * @param obj The reference object with which to compare.
     * @return true if the two objects are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentCustomizerPermission))
            return false;
        DeploymentCustomizerPermission other = (DeploymentCustomizerPermission) obj;
        
        return this.implies(other) && other.implies(this);
        
        /*Vector avThis = getActionVector();
        Vector avOther = other.getActionVector();
        boolean eqActions = (avThis.containsAll(avOther) &&
                avOther.containsAll(avThis));
        return getRepresentation().equals(other.getRepresentation()) && eqActions;*/
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
    	return getActionVector().hashCode();
    }

    /**
     * Returns the String representation of the action list.
     * @return Action list of this permission instance. This is a comma-separated 
     * list that reflects the action parameter of the constructor.
     * @see java.security.Permission#getActions()
     */
    public String getActions() {
    	StringBuffer sb = new StringBuffer();
    	for (Iterator it = getActionVector().iterator(); it.hasNext();) {
			String action = (String) it.next();
			sb.append(action + (it.hasNext() ? ", " : ""));
		}
        return sb.toString();
    }

    /**
     * Checks if this DeploymentCustomizerPermission would imply the parameter permission.
     * @param permission Permission to check.
     * @return true if this DeploymentCustomizerPermission object implies the 
     * specified permission.
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission) {
        if (null == permission)
            return false;
        if (!(permission instanceof DeploymentCustomizerPermission))
            return false;
        DeploymentCustomizerPermission other = (DeploymentCustomizerPermission) permission;
        return getRepresentation().match(other.getRepresentation()) && 
        	   getActionVector().containsAll(other.getActionVector());
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentCustomizerPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        return null;
    }
    
    private Vector getActionVector() {
        if (null != actionsVector)
            return actionsVector;
        
        actionsVector = new Vector();
        StringTokenizer t = new StringTokenizer(actions.toUpperCase(), ",");
        while (t.hasMoreTokens()) {
            String action = t.nextToken().trim();
            actionsVector.add(action.toLowerCase());
        }
        Collections.sort(actionsVector);
        return actionsVector;
    }

    private Representation getRepresentation() {
        return rep; 
    }

    private void check() {
        if (!ACTIONS.containsAll(getActionVector()))
            throw new IllegalArgumentException("Illegal action");
    }

}
