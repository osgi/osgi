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
 * @version $Revision$
 */
public class DmtPrincipalPermission extends BasicPermission {
    // TODO add static final serialVersionUID

    /**
     * Creates a new <code>DmtPrincipalPermission</code> object with its name
     * set to the target string
     * @param target Name of the principal
     */
    public DmtPrincipalPermission(String target) {
        super(target);
    }

    /**
     * Creates a new <code>DmtPrincipalPermission</code> object using the
     * 'canonic' two argument constructor.
     *
     * @param target Name of the principal
     * @param actions ignored
     */
    public DmtPrincipalPermission(String target, String actions) {
        this(target);
    }

    // All methods are good for us as implemented by BasicPermission.
}

