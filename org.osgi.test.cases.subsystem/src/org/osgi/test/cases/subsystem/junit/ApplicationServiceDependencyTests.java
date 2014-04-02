package org.osgi.test.cases.subsystem.junit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.test.cases.subsystem.util.SubsystemBuilder;

public class ApplicationServiceDependencyTests extends SubsystemTest {
	public void testRequireCapability() throws Exception {
		String symbolicNameA = "app.service.dep.a";
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					symbolicNameA,
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, symbolicNameA)
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameA)
							.bundle(symbolicNameA + ".jar")
							.build());
			fail("Subsystem should have failed to install due to missing service dependency org.osgi.framework.Constants");
		}
		catch (SubsystemException e) {
			// Okay.
			e.printStackTrace();
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(symbolicNameA, subsystem, Operation.UNINSTALL, false);
			}
		}
	}
	
	public void testProvideCapability() throws Exception {
		String symbolicNameB = "app.service.dep.b";
		String symbolicNameC = "app.service.dep.c";
		Subsystem root = getRootSubsystem();
		Subsystem composite = root.install(
				symbolicNameB,
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, symbolicNameB)
						.header(SubsystemConstants.SUBSYSTEM_TYPE, SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE)
						.header(SubsystemConstants.SUBSYSTEM_EXPORTSERVICE, "org.osgi.test.cases.subsystem.app.service.dep.b.B")
						.header(Constants.IMPORT_PACKAGE, "org.osgi.framework")
						.header(Constants.EXPORT_PACKAGE, "org.osgi.test.cases.subsystem.app.service.dep.b")
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameB + ";version=\"[1,1]\"")
						.bundle(symbolicNameB + ".jar")
						.build());
		try {
			doSubsystemOperation(symbolicNameB, composite, Operation.START, false);
			Subsystem application = root.install(
					symbolicNameC,
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, symbolicNameC)
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameC)
							.bundle(symbolicNameC + ".jar")
							.build());
			try {
				application.start();
			}
			catch (SubsystemException e) {
				e.printStackTrace();
				fail("Subsystem should have started because service dependency org.osgi.test.cases.subsystem.app.service.dep.b.B was registered");
			}
			finally {
				doSubsystemOperation(symbolicNameC, application, Operation.UNINSTALL, false);
			}
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because service dependency org.osgi.test.cases.subsystem.app.service.dep.b.B was provided");
		}
		finally {
			doSubsystemOperation(symbolicNameB, composite, Operation.UNINSTALL, false);
		}
	}

	public void testRequireCapabilityOverridesBlueprint() throws Exception {
		String symbolicNameD = "app.service.dep.d";
		String symbolicNameE = "app.service.dep.e";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameE, root.getBundleContext(), null, symbolicNameE + ".jar", false);
		doBundleOperation(symbolicNameE, bundle, Operation.START, false);
		try {
			Subsystem subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameD)
							.bundle(symbolicNameD + ".jar")
							.build());
			try {
				subsystem.start();
				BundleContext context = subsystem.getBundleContext();
				assertNotNull("Require-Capability service org.osgi.test.cases.subsystem.app.service.dep.e.E1 was not visible", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.e.E1"));
				assertNull("Blueprint service org.osgi.test.cases.subsystem.app.service.dep.e.E2 was visible but is not in Require-Capability", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.e.E2"));
			}
			catch (SubsystemException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.e.E1 was provided");
		}
	}
	
	public void testRequireCapabilityOverridesBlueprintOnInstall() throws Exception {
		String symbolicNameD = "app.service.dep.d";
		String symbolicNameF = "app.service.dep.f";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameF, root.getBundleContext(), null, symbolicNameF + ".jar", false);
		doBundleOperation(symbolicNameF, bundle, Operation.START, false);
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameD)
							.bundle(symbolicNameD + ".jar")
							.build());
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.e.E1 was provided");
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
	}
	
	public void testRequireCapabilityOverridesDeclarativeServices() throws Exception {
		String symbolicNameE = "app.service.dep.e";
		String symbolicNameG = "app.service.dep.g";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameE, root.getBundleContext(), null, symbolicNameE + ".jar", false);
		doBundleOperation(symbolicNameE, bundle, Operation.START, false);
		try {
			Subsystem subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameG)
							.bundle(symbolicNameG + ".jar")
							.build());
			try {
				subsystem.start();
				BundleContext context = subsystem.getBundleContext();
				assertNotNull("Require-Capability service org.osgi.test.cases.subsystem.app.service.dep.e.E1 was not visible", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.e.E1"));
				assertNull("Blueprint service org.osgi.test.cases.subsystem.app.service.dep.e.E2 was visible but is not in Require-Capability", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.e.E2"));
			}
			catch (SubsystemException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.e.E1 was provided");
		}
	}
	
	public void testRequireCapabilityOverridesDeclarativeServicesOnInstall() throws Exception {
		String symbolicNameF = "app.service.dep.f";
		String symbolicNameG = "app.service.dep.g";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameF, root.getBundleContext(), null, symbolicNameF + ".jar", false);
		doBundleOperation(symbolicNameF, bundle, Operation.START, false);
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameG)
							.bundle(symbolicNameG + ".jar")
							.build());
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.e.E1 was provided");
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
	}

	public void testProvideCapabilityOverridesBlueprintOnInstall() throws Exception {
		String symbolicNameH = "app.service.dep.h";
		String symbolicNameJ = "app.service.dep.j";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameH, root.getBundleContext(), null, symbolicNameH + ".jar", false);
		doBundleOperation(symbolicNameH, bundle, Operation.START, false);
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameJ)
							.bundle(symbolicNameJ + ".jar")
							.build());
			fail("Subsystem should not have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.h.H2 was not provided");
		}
		catch (SubsystemException e) {
			// Okay.
			e.printStackTrace();
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
	}
	
	public void testProvideCapabilityOverridesBlueprint() throws Exception {
		String symbolicNameH = "app.service.dep.h";
		String symbolicNameK = "app.service.dep.k";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameH, root.getBundleContext(), null, symbolicNameH + ".jar", false);
		doBundleOperation(symbolicNameH, bundle, Operation.START, false);
		try {
			Subsystem subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameK)
							.bundle(symbolicNameK + ".jar")
							.build());
			try {
				subsystem.start();
				BundleContext context = subsystem.getBundleContext();
				assertNotNull("Require-Capability service org.osgi.test.cases.subsystem.app.service.dep.h.H1 was not visible", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.h.H1"));
				assertNotNull("Optional blueprint service org.osgi.test.cases.subsystem.app.service.dep.h.H2 must be visible even though not in Provide-Capability", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.h.H2"));
			}
			catch (SubsystemException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.h.H2 was not provided but optional");
		}
	}
	
	public void testProvideCapabilityOverridesDeclarativeServicesOnInstall() throws Exception {
		String symbolicNameI = "app.service.dep.i";
		String symbolicNameJ = "app.service.dep.j";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameI, root.getBundleContext(), null, symbolicNameI + ".jar", false);
		doBundleOperation(symbolicNameI, bundle, Operation.START, false);
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameJ)
							.bundle(symbolicNameJ + ".jar")
							.build());
			fail("Subsystem should not have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.h.H2 was not provided");
		}
		catch (SubsystemException e) {
			// Okay.
			e.printStackTrace();
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
	}
	
	public void testProvideCapabilityOverridesDeclarativeServices() throws Exception {
		String symbolicNameI = "app.service.dep.i";
		String symbolicNameK = "app.service.dep.k";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameI, root.getBundleContext(), null, symbolicNameI + ".jar", false);
		doBundleOperation(symbolicNameI, bundle, Operation.START, false);
		try {
			Subsystem subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameK)
							.bundle(symbolicNameK + ".jar")
							.build());
			try {
				subsystem.start();
				BundleContext context = subsystem.getBundleContext();
				assertNotNull("Require-Capability service org.osgi.test.cases.subsystem.app.service.dep.h.H1 was not visible", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.h.H1"));
				assertNotNull("Optional declarative service org.osgi.test.cases.subsystem.app.service.dep.h.H2 must be visible even though not in Provide-Capability", context.getServiceReference("org.osgi.test.cases.subsystem.app.service.dep.h.H2"));
			}
			catch (SubsystemException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because Require-Capability service dependency org.osgi.test.cases.subsystem.app.service.dep.h.H2 was not provided but optional");
		}
	}
}
