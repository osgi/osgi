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

package org.osgi.service.condpermadmin;

import java.security.AccessControlContext;
import java.util.Enumeration;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * This is a framework service that allows ConditionalPermissionInfos to be
 * added to, retrieved from, and removed from the framework.
 * 
 * @version $Revision$
 */
public interface ConditionalPermissionAdmin {
	/**
	 * This is a framework service that allows ConditionalPermissionInfos to be
	 * added to, retrieved from, and removed from the framework.
	 * 
	 * @param conds
	 *            the Conditions that need to be satisfied to enable the
	 *            corresponding Permissions.
	 * @param perms
	 *            the Permissions that are enable when the corresponding
	 *            Conditions are satisfied.
	 * @return the ConditionalPermissionInfo that for the newly added Conditions
	 *         and Permissions.
	 */
	ConditionalPermissionInfo addConditionalPermissionInfo(
			ConditionInfo conds[], PermissionInfo perms[]);

	/**
	 * Returns the ConditionalPermissionInfos that are currently managed by
	 * ConditionalPermissionAdmin. The Enumeration is made up of
	 * ConditionalPermissionInfos. Calling ConditionalPermissionInfo.delete()
	 * will remove the ConditionalPermissionInfo from
	 * ConditionalPermissionAdmin.
	 * 
	 * @return the ConditionalPermissionInfos that are currently managed by
	 *         ConditionalPermissionAdmin. The Enumeration is made up of
	 *         ConditionalPermissionInfos.
	 */
	Enumeration getConditionalPermissionInfos();

	/**
	 * Returns the AccessControlContext that corresponds to the given signers.
	 * 
	 * @param signers
	 *            the signers that will be checked agains BundleSignerCondition.
	 * @return an AccessControlContext that has the Permissions associated with
	 *         the signer.
	 */
	AccessControlContext getAccessControlContext(String signers[]);
}
