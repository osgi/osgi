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

package org.osgi.service.cm;

import java.security.BasicPermission;
import java.security.Permission;

/**
 * This permission controls access to ConfigurationAdmin. It enables access
 * to pids that may be described using wildcards as explain in BasicPermission.
 * It also controls the type of access allowed. "get" access will allow only
 * queries. "set" access allows configurations to be added, changed, and
 * deleted.
 */
public class ConfigurationPermission extends BasicPermission {
    /**
     * The action string for the <code>GET_ACTION</code> action: value is "get" 
     */
    static public final String GET_ACTION = "get";
    /**
     * The action string for the <code>SET_ACTION</code> action: value is "set" 
     */
    static public final String SET_ACTION = "set";
    boolean canGet = false;
    boolean canSet = false;
    /**
     * This method returns true if the name of <code>perm</code> is implied by this
     * permission according to <code>BasicPermission.implies</code>
     * and any actions of <code>perm</code> are also actions of this
     * permission.
     * 
     * @see java.security.BasicPermission#implies(java.security.Permission)
     */
    public boolean implies(Permission perm) {
        if (!super.implies(perm)) {
            return false;
        }
        ConfigurationPermission cperm = (ConfigurationPermission)perm;
        if ((cperm.canGet && !canGet) || (cperm.canSet && !canSet)) {
            return false;
        }
        return true;
    }
    /**
     * Constructs a <code>ConfigurationPermission</code> with the given
     * name and action. The name describes the pids that can be accessed
     * using this permission and the action indications whether than can
     * be queried and or set.
     * 
     * @param name the pids that can be accessed. Can use wild cards as
     * described in BasicPermission.
     * @param action the type of access allowed: get and/or set.
     * 
     * @see java.security.BasicPermission
     */
    public ConfigurationPermission(String name, String action) {
        super(name, action);
    }
}
