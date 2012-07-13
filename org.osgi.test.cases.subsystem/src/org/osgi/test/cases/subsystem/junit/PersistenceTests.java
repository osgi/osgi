/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.junit;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.subsystem.Subsystem;


public class PersistenceTests extends SubsystemTest{

	public void test9A1_app_app() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9A1_app_comp() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9A1_app_feat() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test9A2_comp_app() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9A2_comp_comp() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9A2_comp_feat() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test9A3_feat_app() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9A3_feat_comp() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9A3_feat_feat() throws InvalidSyntaxException {
		doTest9A(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	private void doTest9A(String s1Name, String s2Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_2);

		Subsystem root = getRootSubsystem();

		Subsystem s1 = doSubsystemInstall("install s1", root, "s1." + getName(), s1Name, false);
		Subsystem s2 = doSubsystemInstall("install s2", root, "s2." + getName(), s2Name, false);

		getBundle(root, BUNDLE_SHARE_A);
		getBundle(root, BUNDLE_SHARE_B);
		getBundle(s1, BUNDLE_SHARE_C);
		getBundle(s2, BUNDLE_SHARE_D);
		getBundle(s2, BUNDLE_SHARE_E);

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		doSubsystemOperation("Start S2", s2, Operation.START, false);

		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);
		doSubsystemOperation("Stop S2", s2, Operation.STOP, false);

		stopImplementation();
		startImplementation();

		root = getRootSubsystem();
		s1 = s2 = null;
		Collection<Subsystem> children = root.getChildren();
		assertEquals("Wrong number of children.", 2, children.size());
		for (Subsystem subsystem : children) {
			if (getSymbolicName(s1Name).equals(subsystem.getSymbolicName())) {
				s1 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s1);
			} else if (getSymbolicName(s2Name).equals(subsystem.getSymbolicName())) {
				s2 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s2);
			}
		}
		assertNotNull("Could not find subsystem: " + s1Name, s1);
		assertNotNull("Could not find subsystem: " + s2Name, s2);


		getBundle(root, BUNDLE_SHARE_A);
		getBundle(root, BUNDLE_SHARE_B);
		getBundle(s1, BUNDLE_SHARE_C);
		getBundle(s2, BUNDLE_SHARE_D);
		getBundle(s2, BUNDLE_SHARE_E);
	}

	public void test9B1_app_app() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9B1_app_comp() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9B1_app_feat() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test9B2_comp_app() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9B2_comp_comp() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9B2_comp_feat() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test9B3_feat_app() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9B3_feat_comp() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9B3_feat_feat() throws InvalidSyntaxException {
		doTest9B(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	private void doTest9B(String s1Name, String s2Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_2);

		Subsystem root = getRootSubsystem();

		Subsystem s1 = doSubsystemInstall("install s1", root, "s1." + getName(), s1Name, false);
		Subsystem s2 = doSubsystemInstall("install s2", root, "s2." + getName(), s2Name, false);

		Bundle a, b, c, d, e;
		a = getBundle(root, BUNDLE_SHARE_A);
		b = getBundle(root, BUNDLE_SHARE_B);
		c = getBundle(s1, BUNDLE_SHARE_C);
		d = getBundle(s2, BUNDLE_SHARE_D);
		e = getBundle(s2, BUNDLE_SHARE_E);

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		doSubsystemOperation("Start S2", s2, Operation.START, false);

		assertEquals("Wrong state for bundle: " + a, Bundle.ACTIVE, a.getState());
		assertEquals("Wrong state for bundle: " + b, Bundle.ACTIVE, b.getState());
		assertEquals("Wrong state for bundle: " + c, Bundle.ACTIVE, c.getState());
		assertEquals("Wrong state for bundle: " + d, Bundle.ACTIVE, d.getState());
		assertEquals("Wrong state for bundle: " + e, Bundle.ACTIVE, e.getState());

		stopImplementation();

		assertEquals("Wrong state for bundle: " + a, Bundle.RESOLVED, a.getState());
		assertEquals("Wrong state for bundle: " + b, Bundle.RESOLVED, b.getState());
		assertEquals("Wrong state for bundle: " + c, Bundle.RESOLVED, c.getState());
		assertEquals("Wrong state for bundle: " + d, Bundle.RESOLVED, d.getState());
		assertEquals("Wrong state for bundle: " + e, Bundle.RESOLVED, e.getState());

		startImplementation();

		root = getRootSubsystem();
		s1 = s2 = null;
		Collection<Subsystem> children = root.getChildren();
		assertEquals("Wrong number of children.", 2, children.size());
		for (Subsystem subsystem : children) {
			if (getSymbolicName(s1Name).equals(subsystem.getSymbolicName())) {
				s1 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s1);
			} else if (getSymbolicName(s2Name).equals(subsystem.getSymbolicName())) {
				s2 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s2);
			}
		}
		assertNotNull("Could not find subsystem: " + s1Name, s1);
		assertNotNull("Could not find subsystem: " + s2Name, s2);

		assertEquals("Wrong subsystem state: " + s1Name, Subsystem.State.ACTIVE, s1.getState());
		assertEquals("Wrong subsystem state: " + s2Name, Subsystem.State.ACTIVE, s2.getState());

		a = getBundle(root, BUNDLE_SHARE_A);
		b = getBundle(root, BUNDLE_SHARE_B);
		c = getBundle(s1, BUNDLE_SHARE_C);
		d = getBundle(s2, BUNDLE_SHARE_D);
		e = getBundle(s2, BUNDLE_SHARE_E);

		assertEquals("Wrong state for bundle: " + a, Bundle.ACTIVE, a.getState());
		assertEquals("Wrong state for bundle: " + b, Bundle.ACTIVE, b.getState());
		assertEquals("Wrong state for bundle: " + c, Bundle.ACTIVE, c.getState());
		assertEquals("Wrong state for bundle: " + d, Bundle.ACTIVE, d.getState());
		assertEquals("Wrong state for bundle: " + e, Bundle.ACTIVE, e.getState());

		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);
		doSubsystemOperation("Stop S2", s2, Operation.STOP, false);

		stopImplementation();
		startImplementation();

		root = getRootSubsystem();
		s1 = s2 = null;
		children = root.getChildren();
		assertEquals("Wrong number of children.", 2, children.size());
		for (Subsystem subsystem : children) {
			if (getSymbolicName(s1Name).equals(subsystem.getSymbolicName())) {
				s1 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s1);
			} else if (getSymbolicName(s2Name).equals(subsystem.getSymbolicName())) {
				s2 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s2);
			}
		}
		assertNotNull("Could not find subsystem: " + s1Name, s1);
		assertNotNull("Could not find subsystem: " + s2Name, s2);

		assertEquals("Wrong subsystem state: " + s1Name, Subsystem.State.RESOLVED, s1.getState());
		assertEquals("Wrong subsystem state: " + s2Name, Subsystem.State.RESOLVED, s2.getState());

		a = getBundle(root, BUNDLE_SHARE_A);
		b = getBundle(root, BUNDLE_SHARE_B);
		c = getBundle(s1, BUNDLE_SHARE_C);
		d = getBundle(s2, BUNDLE_SHARE_D);
		e = getBundle(s2, BUNDLE_SHARE_E);

		assertEquals("Wrong state for bundle: " + a, Bundle.RESOLVED, a.getState());
		assertEquals("Wrong state for bundle: " + b, Bundle.RESOLVED, b.getState());
		assertEquals("Wrong state for bundle: " + c, Bundle.RESOLVED, c.getState());
		assertEquals("Wrong state for bundle: " + d, Bundle.RESOLVED, d.getState());
		assertEquals("Wrong state for bundle: " + e, Bundle.RESOLVED, e.getState());
	}

	public void test9C1_app_app() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9C1_app_comp() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9C1_app_feat() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_APPLICATION_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test9C2_comp_app() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9C2_comp_comp() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9C2_comp_feat() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_COMPOSITE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	public void test9C3_feat_app() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_APPLICATION_S2);
	}

	public void test9C3_feat_comp() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_COMPOSITE_S2);
	}

	public void test9C3_feat_feat() throws InvalidSyntaxException {
		doTest9C(SUBSYSTEM_7G_FEATURE_S1, SUBSYSTEM_7G_FEATURE_S2);
	}

	private void doTest9C(String s1Name, String s2Name) throws InvalidSyntaxException {
		registerRepository(REPOSITORY_2);

		Subsystem root = getRootSubsystem();

		Subsystem s1 = doSubsystemInstall("install s1", root, "s1." + getName(), s1Name, false);
		Subsystem s2 = doSubsystemInstall("install s2", root, "s2." + getName(), s2Name, false);

		getBundle(root, BUNDLE_SHARE_A);
		getBundle(root, BUNDLE_SHARE_B);
		getBundle(s1, BUNDLE_SHARE_C);
		getBundle(s2, BUNDLE_SHARE_D);
		getBundle(s2, BUNDLE_SHARE_E);

		doSubsystemOperation("Start S1", s1, Operation.START, false);
		doSubsystemOperation("Start S2", s2, Operation.START, false);

		doSubsystemOperation("Stop S1", s1, Operation.STOP, false);
		doSubsystemOperation("Stop S2", s2, Operation.STOP, false);

		stopImplementation();
		startImplementation();

		root = getRootSubsystem();
		s1 = s2 = null;
		Collection<Subsystem> children = root.getChildren();
		assertEquals("Wrong number of children.", 2, children.size());
		for (Subsystem subsystem : children) {
			if (getSymbolicName(s1Name).equals(subsystem.getSymbolicName())) {
				s1 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s1);
			} else if (getSymbolicName(s2Name).equals(subsystem.getSymbolicName())) {
				s2 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s2);
			}
		}
		assertNotNull("Could not find subsystem: " + s1Name, s1);
		assertNotNull("Could not find subsystem: " + s2Name, s2);

		Bundle a, b, c, d, e;
		a = getBundle(root, BUNDLE_SHARE_A);
		b = getBundle(root, BUNDLE_SHARE_B);
		c = getBundle(s1, BUNDLE_SHARE_C);
		d = getBundle(s2, BUNDLE_SHARE_D);
		e = getBundle(s2, BUNDLE_SHARE_E);

		doSubsystemOperation("Uninstall S1", s1, Operation.UNINSTALL, false);
		doSubsystemOperation("Uninstall S2", s2, Operation.UNINSTALL, false);

		stopImplementation();
		startImplementation();

		root = getRootSubsystem();
		children = root.getChildren();
		assertEquals("Wrong number of children.", 0, children.size());
		s1 = s2 = null;
		for (Subsystem subsystem : children) {
			if (getSymbolicName(s1Name).equals(subsystem.getSymbolicName())) {
				s1 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s1);
			} else if (getSymbolicName(s2Name).equals(subsystem.getSymbolicName())) {
				s2 = subsystem;
				// for cleanup purposes
				explicitlyInstalledSubsystems.add(s2);
			}
		}

		assertNull("Found subsystem: " + s1Name, s1);
		assertNull("Found subsystem: " + s2Name, s2);

		assertEquals("Wrong state for bundle: " + a, Bundle.UNINSTALLED, a.getState());
		assertEquals("Wrong state for bundle: " + b, Bundle.UNINSTALLED, b.getState());
		assertEquals("Wrong state for bundle: " + c, Bundle.UNINSTALLED, c.getState());
		assertEquals("Wrong state for bundle: " + d, Bundle.UNINSTALLED, d.getState());
		assertEquals("Wrong state for bundle: " + e, Bundle.UNINSTALLED, e.getState());
	}
}
