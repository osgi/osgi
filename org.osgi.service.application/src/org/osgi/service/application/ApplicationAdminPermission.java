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

package org.osgi.service.application;

import java.security.BasicPermission;

/**
 * This class implements permissions for the Application Manager
 * 
 * @modelguid {7356FDC7-F93F-4976-AF3F-9BD019851354}
 */
public class ApplicationAdminPermission extends BasicPermission {
	
	/** @modelguid {ECDFA049-AC8D-4A83-BBE8-983885D497C7} */
    public static final String LAUNCH      = "launch";
	/** @modelguid {D5651EE6-1030-4534-9DEA-7FC37C23DD1A} */
    public static final String SCHEDULE    = "schedule";
	/** @modelguid {910201A6-85DF-4998-BF3E-1756958D9962} */
    public static final String MANIPULATE  = "manipulate";
	/** @modelguid {2ACA0AF5-686F-4944-A8FA-A74F46E3CC7F} */
    public static final String ENUMERATE   = "enumerate";
	/** @modelguid {EC19473B-3DA4-4C9D-9F35-5C6DBC47F2BA} */
    public static final String GETLOCK     = "getlock";
	/** @modelguid {EC19473B-3DA4-4C9D-9F35-5C6DBC47F2BB} */
    public static final String SETLOCK     = "setlock";
    
    /** Constructs a ApplicationManagerPermission.
     * @param actions - read and write
     * @modelguid {C7A16B53-A240-498C-9F59-7F22FE2E94BB}
     */
    public ApplicationAdminPermission(String actions) {
        super(actions);          
    }
    
    /** Constructs a ApplicationManagerPermission.
     * @param name - name of the permission 
     * @param actions - read and write
     * @modelguid {BE4BF220-EBCF-4303-A5DD-39CFDFE660BA}
     */
    public ApplicationAdminPermission(String pid, String actions) {
        super(pid, actions);          
    }
}
