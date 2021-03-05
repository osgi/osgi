/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.coordinator.secure.junit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.cases.coordinator.secure.TestClassResult;
import org.osgi.test.support.OSGiTestCase;

/**
 * Basic Coordinator secure test case.
 */
public class CoordinatorSecureTests extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference<Coordinator>	coordinatorReference;
	
	/**
	 * Coordination.addParticipant(Participant) should throw a SecurityException
	 * if the caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationAddParticipantFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb19.jar");
	}
	
	/**
	 * Coordination.addParticipant(Participant) should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationAddParticipantSucceedPartcipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb20.jar");
	}
	
	/**
	 * Coordination.end() should throw a SecurityException if the
	 * caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationEndFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb13.jar");
	}
	
	/**
	 * Coordination.end() should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationEndSucceedInitiate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb14.jar");;
	}
	
	/**
	 * Coordination.extendTimeout(long) should throw a SecurityException if the
	 * caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationExtendTimeoutFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb25.jar", 10000);
	}
	
	/**
	 * Coordination.extendTimeout(long) should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationExtendTimeoutSucceedParticipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb26.jar", 10000);
	}
	
	/**
	 * Coordination.fail(Throwable) should throw a SecurityException if the
	 * caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationFailFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb15.jar");
	}
	
	/**
	 * Coordination.fail(Throwable) should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationFailSucceedParticipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb16.jar");
	}
	
	/**
	 * Coordination.getBundle() should throw a SecurityException if the
	 * caller does not have ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinationGetBundleFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb33.jar");
	}
	
	/**
	 * Coordination.getBundle() should not throw a 
	 * SecurityException if the caller has ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinationGetBundleSucceedAdmin() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb34.jar");
	}
	
	/**
	 * Coordination.getEnclosingCoordination() should throw a SecurityException
	 * if the caller does not have ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinationGetEnclosingCoordinationFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb35.jar");
	}
	
	/**
	 * Coordination.getEnclosingCoordination() should not throw a 
	 * SecurityException if the caller has ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinationGetEnclosingCoordinationSucceedAdmin() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb36.jar");
	}
	
	/**
	 * Coordination.getFailure() should throw a SecurityException if the
	 * caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationGetFailureFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb17.jar");
	}
	
	/**
	 * Coordination.getFailure() should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationGetFailureSucceedInitiate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb18.jar");
	}
	
	/**
	 * Coordination.getParticipants() should throw a SecurityException if the
	 * caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationGetParticipantsFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb21.jar");
	}
	
	/**
	 * Coordination.getParticipants() should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationGetParticipantsSucceedInitiate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb22.jar");
	}
	
	/**
	 * Coordination.getThread() should throw a SecurityException if the
	 * caller does not have ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinationGetThreadFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb31.jar");
	}
	
	/**
	 * Coordination.getThread() should not throw a 
	 * SecurityException if the caller has ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinationGetThreadSucceedAdmin() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb32.jar");
	}
	
	/**
	 * Coordination.getVariables() should throw a SecurityException if the
	 * caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationGetVariablesFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb23.jar");
	}
	
	/**
	 * Coordination.getVariables() should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationGetVariablesSucceedParticipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb24.jar");
	}
	
	/**
	 * Coordination.join(long) should throw a SecurityException if the
	 * caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationJoinFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb27.jar");
	}
	
	/**
	 * Coordination.join(long) should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinationJoinSucceedParticipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb28.jar");
	}
	
	/**
	 * Coordination.push() should throw a SecurityException if the
	 * caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationPushFailNone() throws Exception {
		assertSecurityExceptionWithDirectCoordination("tb29.jar");
	}
	
	/**
	 * Coordination.push() should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinationPushSucceedInitiate() throws Exception {
		assertNotSecurityExceptionWithDirectCoordination("tb30.jar");
	}
	
	/**
	 * Coordinator.addParticipant(Participant) should throw a SecurityException
	 * if the caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorAddParticipantFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb9.jar");
	}
	
	/**
	 * Coordinator.addParticipant(Participant) should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorAddParticipantSucceedParticipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb10.jar");
	}
	
	/**
	 * Coordinator.begin(String, long) should throw a SecurityException if the 
	 * caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorBeginFailNone() throws Exception {
		assertSecurityException("tb4.jar");
	}
	
	/**
	 * Coordinator.begin(String, long) should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorBeginSucceedInitiate() throws Exception {
		assertNotSecurityException("tb3.jar");
	}
	
	/**
	 * Coordinator.create(String, long) should throw a SecurityException
	 * if the caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorCreateFailNone() throws Exception {
		assertSecurityException("tb2.jar");
	}
	
	/**
	 * Coordinator.create(String, long) should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorCreateSucceedInitiate() throws Exception {
		assertNotSecurityException("tb1.jar");
	}
	
	/**
	 * Coordinator.fail(Throwable) should throw a SecurityException
	 * if the caller does not have PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorFailFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb8.jar");
	}
	
	/**
	 * Coordinator.fail(Throwable) should not throw a 
	 * SecurityException if the caller has PARTICIPATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorFailSucceedParticipate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb7.jar");;
	}
	
	/**
	 * Coordinator.getCoordination(long) should not return the requested
	 * coordination if the caller does not have ADMIN permission for that
	 * coordination.
	 * @throws Exception
	 */
	public void testCoordinatorGetCoordination() throws Exception {
		BundleContext bc = getContext();
		Bundle b = install("tb12.jar");
		Coordination c1 = coordinator.create("com.acme.1", 0);
		Coordination c2 = null;
		ServiceRegistration<Coordination> c1sr = null;
		ServiceRegistration<Coordination> c2sr = null;
		ServiceReference<TestClassResult> sr = null;
		try {
			c2 = coordinator.create("com.ibm.2", 0);
			assertEquals("The coordination was not the expected one.", c1, coordinator.getCoordination(c1.getId()));
			assertEquals("The coordination was not the expected one.", c2, coordinator.getCoordination(c2.getId()));
			c1sr = bc.registerService(Coordination.class, c1, null);
			c2sr = bc.registerService(Coordination.class, c2, null);
			b.start();
			sr = bc.getServiceReference(TestClassResult.class);
			assertNotNull("The test bundle did not register a result.", sr);
			TestClassResult tr = bc.getService(sr);
			assertTrue("The coordinator should only return coordinations for which the caller has permissions.", tr.succeeded());
		}
		finally {
			ungetQuietly(sr);
			unregisterQuietly(c2sr);
			unregisterQuietly(c1sr);
			endQuietly(c2);
			endQuietly(c1);
			uninstallQuietly(b);
		}
	}
	
	/**
	 * Coordinator.getCoordinations() should not return any coordinations for
	 * which the caller does not have ADMIN permission.
	 * @throws Exception
	 */
	public void testCoordinatorGetCoordinations() throws Exception {
		BundleContext bc = getContext();
		Bundle b = install("tb11.jar");
		Coordination c1 = coordinator.create("com.acme.1", 0);
		Coordination c2 = null;
		ServiceReference<TestClassResult> sr = null;
		try {
			c2 = coordinator.create("com.ibm.2", 0);
			assertEquals("The coordinator did not return the correct number of coordinations.", 2, coordinator.getCoordinations().size());
			b.start();
			sr = bc.getServiceReference(TestClassResult.class);
			assertNotNull("The test bundle did not register a result.", sr);
			TestClassResult tr = bc.getService(sr);
			assertTrue("The coordinator should only return coordinations for which the caller has permissions.", tr.succeeded());
		}
		finally {
			ungetQuietly(sr);
			endQuietly(c2);
			endQuietly(c1);
			uninstallQuietly(b);
		}
	}
	
	/**
	 * Coordinator.pop() should throw a SecurityException
	 * if the caller does not have INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorPopFailNone() throws Exception {
		assertSecurityExceptionWithThreadCoordination("tb6.jar");
	}
	
	/**
	 * Coordinator.pop() should not throw a 
	 * SecurityException if the caller has INITIATE permission.
	 * @throws Exception
	 */
	public void testCoordinatorPopSucceedInitiate() throws Exception {
		assertNotSecurityExceptionWithThreadCoordination("tb5.jar");
	}
	
	protected void setUp() throws Exception {
		BundleContext bc = getContext();
		coordinatorReference = bc.getServiceReference(Coordinator.class);
		coordinator = bc.getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertNotSecurityException(String bundle) throws Exception {
		execute(bundle, true);
	}
	
	private void assertNotSecurityExceptionWithDirectCoordination(String bundle) throws Exception {
		assertNotSecurityExceptionWithDirectCoordination(bundle, 0);
	}
	
	private void assertNotSecurityExceptionWithDirectCoordination(String bundle, long timeout) throws Exception {
		executeWithDirectCoordination(bundle, true, timeout);
	}
	
	private void assertNotSecurityExceptionWithThreadCoordination(String bundle) throws Exception {
		assertNotSecurityExceptionWithThreadCoordination(bundle, 0);
	}
	
	private void assertNotSecurityExceptionWithThreadCoordination(String bundle, long timeout) throws Exception {
		executeWithThreadCoordination(bundle, true, timeout);
	}
	
	private void assertSecurityException(String bundle) throws Exception {
		execute(bundle, false);
	}
	
	private void assertSecurityExceptionWithDirectCoordination(String bundle) throws Exception {
		assertSecurityExceptionWithDirectCoordination(bundle, 0);
	}
	
	private void assertSecurityExceptionWithDirectCoordination(String bundle, long timeout) throws Exception {
		executeWithDirectCoordination(bundle, false, timeout);
	}
	
	private void assertSecurityExceptionWithThreadCoordination(String bundle) throws Exception {
		assertSecurityExceptionWithThreadCoordination(bundle, 0);
	}
	
	private void assertSecurityExceptionWithThreadCoordination(String bundle, long timeout) throws Exception {
		executeWithThreadCoordination(bundle, false, timeout);
	}
	
	private void execute(String bundle, boolean hasPermission) throws Exception {
		Bundle b = install(bundle);
		BundleContext bc = getContext();
		ServiceReference<TestClassResult> sr = null;
		try {
			b.start();
			sr = bc.getServiceReference(TestClassResult.class);
			assertNotNull("The test bundle did not register a result.", sr);
			TestClassResult tr = bc.getService(sr);
			bc.ungetService(sr);
			b.uninstall();
			if (hasPermission)
				assertTrue("A SecurityException should not have been thrown.", tr.succeeded());
			else
				assertTrue("A SecurityException should have been thrown.", tr.succeeded());
		}
		finally {
			ungetQuietly(sr);
			uninstallQuietly(b);
		}
	}
	
	private void endQuietly(Coordination c) {
		if (c == null) return;
		try {
			c.end();
		}
		catch (CoordinationException e) {
			// ignore
		}
	}
	
	private void executeWithCoordination(String bundle, boolean hasPermission, long timeout, Coordination c) throws Exception {
		try {
			execute(bundle, hasPermission);
		}
		finally {
			endQuietly(c);
		}
	}
	
	private void executeWithDirectCoordination(String bundle, boolean hasPermission, long timeout) throws Exception {
		Coordination c = coordinator.create("c", timeout);
		BundleContext bc = getContext();
		ServiceRegistration<Coordination> sr = bc.registerService(
				Coordination.class, c, null);
		try {
			executeWithCoordination(bundle, hasPermission, timeout, c);
		}
		finally {
			unregisterQuietly(sr);
		}
	}
	
	private void executeWithThreadCoordination(String bundle, boolean hasPermission, long timeout) throws Exception {
		Coordination c = coordinator.begin("c", timeout);
		assertEquals("The coordination was not at the top of the stack.", c, coordinator.peek());
		executeWithCoordination(bundle, hasPermission, timeout, c);
	}
	
	private void ungetQuietly(ServiceReference<TestClassResult> sr) {
		if (sr == null) return;
		try {
			getContext().ungetService(sr);
		}
		catch (Exception e) {
			// ignore
		}
	}
	
	private void uninstallQuietly(Bundle b) {
		if (b == null) return;
		try {
			b.uninstall();
		}
		catch (Exception e) {
			// ignore;
		}
	}
	
	private void unregisterQuietly(ServiceRegistration<Coordination> sr) {
		if (sr == null) return;
		try {
			sr.unregister();
		}
		catch (Exception e) {
			// ignore
		}
	}
}
