/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.dmt;

import java.security.BasicPermission;

/**
 * Indicates the callers authority to create DMT sessions in the name
 * of a remote management server. Only protocol adapters communicating
 * with management servers should be granted this permission.
 * <p>
 * <code>DmtPrincipalPermission</code> has a target string which
 * controls the name of the principal on whose behalf the protocol
 * adapter can act. A wildcard is allowed in the target as defined by
 * <code>BasicPermission</code>, for example a &quot;*&quot; means the
 * adapter can create a session in the name of any principal.
 */
public class DmtPrincipalPermission extends BasicPermission {
    // TODO add static final serialVersionUID

    /**
     * Creates a new <code>DmtPrincipalPermission</code> object with its name
     * set to the target string.  Name must be non-null and non-empty.
     * 
     * @param target the name of the principal
     * @throws NullPointerException if <code>name</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>name</code> is empty
     */
    public DmtPrincipalPermission(String target) {
        super(target);
    }

    /**
     * Creates a new <code>DmtPrincipalPermission</code> object using the
     * 'canonic' two argument constructor. In this version this class does not
     * define any actions, the second argument of this constructor must be "*"
     * so that later this class can be extended in a backward compatible way.
     * 
     * @param target the name of the principal
     * @param actions no actions defined, must be "*" for forward compatibility
     * @throws NullPointerException if <code>name</code> or
     *         <code>actions</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>name</code> is empty or
     *         <code>actions</code> is not "*"
     */
    public DmtPrincipalPermission(String target, String actions) {
        this(target);
        
        if(actions == null)
            throw new NullPointerException(
                    "'actions' parameter must not be null.");
        
        if(!actions.equals("*"))
            throw new IllegalArgumentException(
                    "'actions' parameter must be \"*\".");
    }

    // All methods are good for us as implemented by BasicPermission.
}

