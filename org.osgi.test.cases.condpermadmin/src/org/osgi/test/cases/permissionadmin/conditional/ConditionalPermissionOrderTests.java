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

package org.osgi.test.cases.permissionadmin.conditional;

import java.io.FilePermission;
import java.net.SocketPermission;
import java.security.AllPermission;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServicePermission;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissionadmin.conditional.tbc.ConditionalDomTBCService;
import org.osgi.test.cases.permissionadmin.conditional.tbc.ConditionalPermTBCService;
import org.osgi.test.cases.permissionadmin.conditional.tbc.ConditionalTBCService;
import org.osgi.test.cases.permissionadmin.conditional.testcond.TestPostPonedCondition;


public class ConditionalPermissionOrderTests extends AbstractPermissionAdminTests {
	private static final PermissionInfo[] RUNTIME_INFOS = new PermissionInfo[] {new PermissionInfo("java.lang.RuntimePermission", "exitVM", null)}; //$NON-NLS-1$ //$NON-NLS-2$
	private static final PermissionInfo[] SOCKET_INFOS = new PermissionInfo[] {new PermissionInfo("java.net.SocketPermission", "localhost", "accept")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final PermissionInfo[] READONLY_INFOS = new PermissionInfo[] {new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "read")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final PermissionInfo[] READWRITE_INFOS = new PermissionInfo[] {
	// multiple permission infos
			new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "read"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "write") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	};
	private static final PermissionInfo[] SERVICE_INFOS = new PermissionInfo[] {new PermissionInfo(ServicePermission.class.getName(), "*", "get,register")}; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final ConditionInfo[] ALLLOCATION_CONDS = new ConditionInfo[] {new ConditionInfo("org.osgi.service.condpermadmin.BundleLocationCondition", new String[] {"*"})}; //$NON-NLS-1$ //$NON-NLS-2$
	private static final ConditionInfo POST_MUT_SAT = new ConditionInfo("org.osgi.test.cases.permissionadmin.conditional.testcond.TestPostPonedCondition", new String[] {"POST_MUT_SAT", "true", "true", "true"}); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	private static final ConditionInfo POST_MUT_UNSAT = new ConditionInfo("org.osgi.test.cases.permissionadmin.conditional.testcond.TestPostPonedCondition", new String[] {"POST_MUT_UNSAT", "true", "true", "false"}); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private Bundle tb1;
	private Bundle tb2;
	private Bundle tb3;
	private ConditionalTBCService	tbc;
	private ConditionalPermTBCService	permTBC;
	private ConditionalDomTBCService	domTBC;
	private FilePermission fileWrite;
	private FilePermission fileRead;
	protected void setUp() throws Exception {
		super.setUp();
	    tb1 = installBundle("tb1.jar", true);
	    tb2 = installBundle("tb2.jar", true);
	    tb3 = installBundle("tb3.jar", true);
	    installBundle("testcond.jar", false);

	    tbc = (ConditionalTBCService)getService(ConditionalTBCService.class.getName());
	    permTBC = (ConditionalPermTBCService)getService(ConditionalPermTBCService.class.getName());
	    domTBC = (ConditionalDomTBCService)getService(ConditionalDomTBCService.class.getName());

	    fileWrite = new FilePermission(getContext().getDataFile("test").getAbsolutePath(), "write");
	    fileRead = new FilePermission(getContext().getDataFile("test").getAbsolutePath(), "read");
	}

	protected void tearDown() throws Exception {
		tb1.uninstall();
		tb2.uninstall();
		tb3.uninstall();

		tbc = null;
		permTBC = null;
		domTBC = null;

		super.tearDown();
	}

	private void testPermission(ConditionalTBCService permCheck,
			Permission p, boolean succeed) {
		try {
			permCheck.checkPermission(p);
			if (!succeed)
				fail("Should have failed permission check: " + p);
		} catch (SecurityException e) {
			if (succeed) {
				fail("Failed to grant permission: " + p, e);
			}
		}
	}

	private void testStackPermission(ConditionalTBCService permCheck,
			Permission p, boolean succeed) {
		try {
			permCheck.checkStack2(p);
			if (!succeed)
				fail("Should have failed permission check: " + p);
		} catch (SecurityException e) {
			if (succeed) {
				fail("Failed to grant permission: " + p, e);
			}
		}
	}

	private ConditionInfo[] getLocationConditions(String location, boolean not) {
		String[] args = not ? new String[] {location, "!"} : new String[] {location}; //$NON-NLS-1$
		return new ConditionInfo[] {new ConditionInfo("org.osgi.service.condpermadmin.BundleLocationCondition", args)}; //$NON-NLS-1$
	}

	public void testUpdate01() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		assertTrue("table is not empty", rows.isEmpty()); //$NON-NLS-1$
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
	}

	public void testUpdate02() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		ConditionalPermissionInfo info = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW);
		rows.add(info);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new FilePermission("test", "read"), true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.clear();
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), true);
	}

	public void testUpdate03() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		ConditionalPermissionInfo info1 = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READWRITE_INFOS, ConditionalPermissionInfo.DENY);
		ConditionalPermissionInfo info2 = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW);
		rows.add(info1);
		rows.add(info2);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), true);
	}

	public void testUpdate04() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		ConditionalPermissionInfo info1 = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READWRITE_INFOS, ConditionalPermissionInfo.DENY);
		ConditionalPermissionInfo info2 = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW);
		rows.add(info1);
		rows.add(info2);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		ConditionalPermissionUpdate update1 = condPermAdmin.newConditionalPermissionUpdate();
		List rows1 = update1.getConditionalPermissionInfos();
		rows1.remove(0);

		ConditionalPermissionUpdate update2 = condPermAdmin.newConditionalPermissionUpdate();
		List rows2 = update2.getConditionalPermissionInfos();
		rows2.remove(0);
		assertTrue("failed to commit", update2.commit()); //$NON-NLS-1$

		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);

		assertFalse("succeeded commit", update1.commit()); //$NON-NLS-1$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);

		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$
		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), true);
	}

	public void testLocationPermission() {
		permAdmin.setDefaultPermissions(READONLY_INFOS);
		permAdmin.setPermissions(tb1.getLocation(), READWRITE_INFOS);

		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		ConditionalPermissionInfo condPermInfo = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, SOCKET_INFOS, ConditionalPermissionInfo.ALLOW);
		rows.add(condPermInfo);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);
		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$

		permAdmin.setPermissions(tb1.getLocation(), null);

		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);
		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), false);
		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$

		permAdmin.setDefaultPermissions(null);
		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new AllPermission(), true);
		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public void testNotLocationCondition01() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		ConditionalPermissionInfo condPermInfo = condPermAdmin.newConditionalPermissionInfo(null, getLocationConditions("xxx", true), SOCKET_INFOS, ConditionalPermissionInfo.ALLOW); //$NON-NLS-1$
		rows.add(condPermInfo);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new AllPermission(), false);
		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new AllPermission(), true);
		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testNotLocationCondition02() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		ConditionalPermissionInfo condPermInfo = condPermAdmin.newConditionalPermissionInfo(null, getLocationConditions(tb1.getLocation(), true), SOCKET_INFOS, ConditionalPermissionInfo.ALLOW);
		rows.add(condPermInfo);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new AllPermission(), false);
		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new AllPermission(), true);
		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testMultipleLocationConditions01() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();

		ConditionalPermissionInfo condPermInfo1 = condPermAdmin.newConditionalPermissionInfo(null, getLocationConditions("xxx", false), SOCKET_INFOS, ConditionalPermissionInfo.ALLOW); //$NON-NLS-1$
		ConditionalPermissionInfo condPermInfo2 = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW);
		rows.add(condPermInfo1);
		rows.add(condPermInfo2);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testMultipleLocationConditions02() {
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();

		ConditionalPermissionInfo condPermInfo1 = condPermAdmin.newConditionalPermissionInfo(null, getLocationConditions("xxx", false), SOCKET_INFOS, ConditionalPermissionInfo.ALLOW); //$NON-NLS-1$
		ConditionalPermissionInfo condPermInfo2 = condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW);
		ConditionalPermissionInfo condPermInfo3 = condPermAdmin.newConditionalPermissionInfo(null, getLocationConditions(tb1.getLocation(), false), RUNTIME_INFOS, ConditionalPermissionInfo.ALLOW);

		rows.add(condPermInfo1);
		rows.add(condPermInfo2);
		rows.add(condPermInfo3);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new RuntimePermission("exitVM", null), true); //$NON-NLS-1$
		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new RuntimePermission("exitVM", null), true); //$NON-NLS-1$
		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new RuntimePermission("exitVM", null), true); //$NON-NLS-1$
		testPermission(tbc, fileWrite, false); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$

		testPermission(tbc, new SocketPermission("localhost", "accept"), true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, new RuntimePermission("exitVM", null), true); //$NON-NLS-1$
		testPermission(tbc, fileWrite, true); //$NON-NLS-1$ //$NON-NLS-2$
		testPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPostponedConditions01() {
		TestPostPonedCondition.clearConditions();

		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_UNSAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, SERVICE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);

		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		TestPostPonedCondition tc1sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb3.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc1unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb3.getBundleId()); //$NON-NLS-1$

		assertNotNull("tc1sat", tc1sat); //$NON-NLS-1$
		assertNotNull("tc3sat", tc3sat); //$NON-NLS-1$
		assertNotNull("tc1unsat", tc1unsat); //$NON-NLS-1$
		assertNotNull("tc3unsat", tc3unsat); //$NON-NLS-1$

		tc1sat.setSatisfied(false);
		tc3sat.setSatisfied(false);
		tc1unsat.setSatisfied(true);
		tc3unsat.setSatisfied(true);
		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		tc1sat.setSatisfied(true);
		tc3sat.setSatisfied(true);
		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);
		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		tc1unsat.setSatisfied(false);
		tc3unsat.setSatisfied(false);
		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		update = condPermAdmin.newConditionalPermissionUpdate();
		rows = update.getConditionalPermissionInfos();
		rows.remove(0);
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);
		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPostponedConditions02() {
		TestPostPonedCondition.clearConditions();

		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_UNSAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, SERVICE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);

		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		TestPostPonedCondition tc1sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb3.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc1unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb3.getBundleId()); //$NON-NLS-1$

		assertNotNull("tc1sat", tc1sat); //$NON-NLS-1$
		assertNotNull("tc3sat", tc3sat); //$NON-NLS-1$
		assertNotNull("tc1unsat", tc1unsat); //$NON-NLS-1$
		assertNotNull("tc3unsat", tc3unsat); //$NON-NLS-1$

		tc1sat.setSatisfied(false);
		tc3sat.setSatisfied(false);
		tc1unsat.setSatisfied(true);
		tc3unsat.setSatisfied(true);
		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		tc1unsat.setSatisfied(false);
		tc3unsat.setSatisfied(false);
		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPostponedConditions03() {
		TestPostPonedCondition.clearConditions();

		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_UNSAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, SERVICE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);

		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		TestPostPonedCondition tc1sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb3.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc1unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb3.getBundleId()); //$NON-NLS-1$

		assertNotNull("tc1sat", tc1sat); //$NON-NLS-1$
		assertNotNull("tc3sat", tc3sat); //$NON-NLS-1$
		assertNotNull("tc1unsat", tc1unsat); //$NON-NLS-1$
		assertNotNull("tc3unsat", tc3unsat); //$NON-NLS-1$

		tc1sat.setSatisfied(false);
		tc3sat.setSatisfied(false);
		tc1unsat.setSatisfied(true);
		tc3unsat.setSatisfied(true);
		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		tc1unsat.setSatisfied(false);
		tc3unsat.setSatisfied(false);
		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPostponedConditions04() {
		TestPostPonedCondition.clearConditions();

		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_UNSAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, SERVICE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);

		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		TestPostPonedCondition tc1sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb3.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc1unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb3.getBundleId()); //$NON-NLS-1$

		assertNotNull("tc1sat", tc1sat); //$NON-NLS-1$
		assertNotNull("tc3sat", tc3sat); //$NON-NLS-1$
		assertNotNull("tc1unsat", tc1unsat); //$NON-NLS-1$
		assertNotNull("tc3unsat", tc3unsat); //$NON-NLS-1$

		tc1sat.setSatisfied(false);
		tc3sat.setSatisfied(false);
		tc1unsat.setSatisfied(true);
		tc3unsat.setSatisfied(true);
		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		tc1unsat.setSatisfied(false);
		tc3unsat.setSatisfied(false);
		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPostponedConditions05() {
		TestPostPonedCondition.clearConditions();

		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
		List rows = update.getConditionalPermissionInfos();
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_UNSAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_SAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, new ConditionInfo[] {POST_MUT_UNSAT}, READONLY_INFOS, ConditionalPermissionInfo.ALLOW));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, READONLY_INFOS, ConditionalPermissionInfo.DENY));
		rows.add(condPermAdmin.newConditionalPermissionInfo(null, ALLLOCATION_CONDS, SERVICE_INFOS, ConditionalPermissionInfo.ALLOW));
		assertTrue("failed to commit", update.commit()); //$NON-NLS-1$);

		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$

		TestPostPonedCondition tc1sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3sat = TestPostPonedCondition.getTestCondition("POST_MUT_SAT_" + tb3.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc1unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb1.getBundleId()); //$NON-NLS-1$
		TestPostPonedCondition tc3unsat = TestPostPonedCondition.getTestCondition("POST_MUT_UNSAT_" + tb3.getBundleId()); //$NON-NLS-1$

		assertNotNull("tc1sat", tc1sat); //$NON-NLS-1$
		assertNotNull("tc3sat", tc3sat); //$NON-NLS-1$
		assertNotNull("tc1unsat", tc1unsat); //$NON-NLS-1$
		assertNotNull("tc3unsat", tc3unsat); //$NON-NLS-1$

		tc1sat.setSatisfied(false);
		tc3sat.setSatisfied(false);
		tc1unsat.setSatisfied(true);
		tc3unsat.setSatisfied(true);
		testStackPermission(tbc, fileRead, true); //$NON-NLS-1$ //$NON-NLS-2$

		tc1unsat.setSatisfied(false);
		tc3unsat.setSatisfied(false);
		testStackPermission(tbc, fileRead, false); //$NON-NLS-1$ //$NON-NLS-2$
	}	
}
