package org.osgi.test.cases.subsystem.junit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.resource.Resource;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;

public class RootSubsystemTests extends SubsystemTest{
	private static final String ROOT_SYMBOLIC_NAME = "org.osgi.service.subsystem.root";
	private static final Version ROOT_VERSION = new Version(1,0,0);
	private static final long ROOT_ID = 1;
	private static final String ROOT_LOCATION = "subsystem://?Subsystem-SymbolicName=org.osgi.service.subsystem.root&Subsystem-Version=1.0.0";

	// TestPlan item 1A Subsystem object
	public void testRootSubsystemExists() {
		Subsystem root = getRootSubsystem();
		checkSubsystemProperties(root, "root", ROOT_SYMBOLIC_NAME, ROOT_VERSION, SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, ROOT_ID, ROOT_LOCATION, State.ACTIVE);
		assertNotNull("The root subsystem parents must not be null.", root.getParents());
		assertTrue("The root subsystem parents must be empty.", root.getParents().isEmpty());
	}

	// TestPlan item 1A Subsystem service properties
	public void testRootSubsystemServiceProperties() {
		ServiceReference<Subsystem> rootReference = rootSubsystem.getServiceReference();
		checkSubsystemProperties(rootReference, "root", ROOT_SYMBOLIC_NAME, ROOT_VERSION, SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, ROOT_ID, State.ACTIVE);
	}

	// TestPlan item 1B
	public void testRootConstiuents() {
		Subsystem root = getRootSubsystem();
		Collection<Resource> rootConstituents = root.getConstituents();
		assertNotNull("Root constituents is null.", rootConstituents);
		assertEquals("Wrong number of root constituents.", initialRootConstituents.size(), rootConstituents.size());
		checkBundleConstituents("root", initialRootConstituents, rootConstituents);
	}

	// TestPlan item 1C
	public void testRootContextBundle() {
		checkContextBundle("root", getRootSubsystem());
	}

	// TestPlan item 1D
	public void testInvalidRootOperations() {
		Subsystem root = getRootSubsystem();
		doSubsystemOperation("root", root, SubsystemOperation.STOP, true);
		doSubsystemOperation("root", root, SubsystemOperation.UNINSTALL, true);
	}
}
