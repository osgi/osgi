/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.condpermadmin;

import java.io.FilePermission;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AllPermission;
import java.util.List;

import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

public class AccessControlContextTests extends AbstractPermissionAdminTests {
	private static final PermissionInfo[] READONLY_INFOS = new PermissionInfo[] {new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "read")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final PermissionInfo[] READWRITE_INFOS = new PermissionInfo[] {
	// multiple permission infos
			new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "read"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "write") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	};
	private static final ConditionInfo SIGNER_CONDITION1 = new ConditionInfo("org.osgi.service.condpermadmin.BundleSignerCondition", new String[] {"*;cn=test1,c=US"}); //$NON-NLS-1$//$NON-NLS-2$
	private static final ConditionInfo SIGNER_CONDITION2 = new ConditionInfo("org.osgi.service.condpermadmin.BundleSignerCondition", new String[] {"*;cn=test2,c=US"}); //$NON-NLS-1$//$NON-NLS-2$
	private static final ConditionInfo NOT_SIGNER_CONDITION1 = new ConditionInfo("org.osgi.service.condpermadmin.BundleSignerCondition", new String[] {"*;cn=test1,c=US", "!"}); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

	public void testSingleSignerRowAllow() {
		// test single row with signer condition
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test1,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
	}

	public void testSingleSignerMultiRowDeny() {
		// test with DENY row
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READWRITE_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READWRITE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test1,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
	}

	public void testMultiSignerMultiRowAllow() {
		// test multiple signer conditions
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION2}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
		acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
	}

	public void testMultiSignerGet() {
		// test multiple signer conditions
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1, SIGNER_CONDITION2}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READWRITE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}

		acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test1,c=US", "cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
	}

	public void testDefaultPemissions() {
		// test with empty rows
		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
			acc.checkPermission(new AllPermission());
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
		// set the default permissions
		permAdmin.setDefaultPermissions(READWRITE_INFOS);
		acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
		try {
			acc.checkPermission(new AllPermission());
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
	}

	public void testEmptyConditionRows() {
		// test with empty condition rows
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {SIGNER_CONDITION1}, READWRITE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
	}

	public void testNotSignerRowAllow() {
		// test ! signer condition
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {NOT_SIGNER_CONDITION1}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
		AccessControlContext acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test1,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}

		acc = condPermAdmin.getAccessControlContext(new String[] {"cn=t1,cn=FR;cn=test2,c=US"}); //$NON-NLS-1$
		try {
			acc.checkPermission(new FilePermission("test", "write")); //$NON-NLS-1$ //$NON-NLS-2$
			fail("expecting AccessControlExcetpion"); //$NON-NLS-1$
		} catch (AccessControlException e) {
			// expected
		}
		try {
			acc.checkPermission(new FilePermission("test", "read")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (AccessControlException e) {
			fail("Unexpected AccessControlExcetpion", e); //$NON-NLS-1$
		}
	}
}
