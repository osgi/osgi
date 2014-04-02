package org.osgi.test.cases.subsystem.junit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.test.cases.subsystem.util.SubsystemBuilder;

public class WeavingHookTests extends SubsystemTest {
	public void testDynamicImportAddedByWeavingHook() throws Exception {
		String symbolicNameA = "weaving.a";
		final String symbolicNameB = "weaving.b";
		final String dynamicPackage = "org.osgi.test.cases.subsystem.weaving.a";
		Subsystem root = getRootSubsystem();
		BundleContext context = root.getBundleContext();
		Bundle bundle = doBundleInstall(symbolicNameA, context, null, symbolicNameA + ".jar", false);
		doBundleOperation(symbolicNameA, bundle, Operation.START, false);
		context.registerService(
				WeavingHook.class,
				new WeavingHook() {
					public void weave(WovenClass wovenClass) {
						Bundle b = wovenClass.getBundleWiring().getBundle();
						if (symbolicNameB.equals(b.getSymbolicName()))
								wovenClass.getDynamicImports().add(dynamicPackage);
					}
				}, 
				null);
		Subsystem subsystem = root.install(
				getName(),
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameB)
						.bundle(symbolicNameB + ".jar")
						.build());
		try {
			subsystem.start();
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have started because dynamic package import " + dynamicPackage + " should have been added to the sharing policy");
		}
		finally {
			subsystem.uninstall();
		}
	}

	public void testDynamicImportNotInSharingPolicyWhenSatisfiedWithinSubsystem() throws Exception {
		String symbolicNameA = "weaving.a";
		String symbolicNameAA = "weaving.aa";
		final String symbolicNameB = "weaving.b";
		String symbolicNameC = "weaving.c";
		final String dynamicPackage = "org.osgi.test.cases.subsystem.weaving.a";
		Subsystem root = getRootSubsystem();
		BundleContext context = root.getBundleContext();
		// Explicitly install bundle AA exporting package added by weaving hook
		// into root subsystem.
		Bundle bundle = doBundleInstall(symbolicNameAA, context, null, symbolicNameAA + ".jar", false);
		doBundleOperation(symbolicNameAA, bundle, Operation.START, false);
		context.registerService(WeavingHook.class, new WeavingHook() {
			public void weave(WovenClass wovenClass) {
				// This will get called when bundle B is started and the
				// Activator class is loaded.
				Bundle b = wovenClass.getBundleWiring().getBundle();
				if (symbolicNameB.equals(b.getSymbolicName()))
					wovenClass.getDynamicImports().add(dynamicPackage);
			}
		}, null);
		Subsystem subsystem = root.install(
				getName(),
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameA + ',' + symbolicNameB)
						// Also add bundle A as content so package added by
						// weaving hook is satisfied within subsystem.
						.bundle(symbolicNameA + ".jar")
						.bundle(symbolicNameB + ".jar").build());
		try {
			subsystem.start(); // This will start bundle B.
			// Ensure bundle B was wired to content bundle A (not explicitly 
			// installed bundle AA in root subsystem).
			Bundle a = findBundle(subsystem, symbolicNameA);
			Bundle b = findBundle(subsystem, symbolicNameB);
			assertEquals(
					"Package provider should be content bundle A",
					a.adapt(BundleRevision.class),
					b.adapt(BundleWiring.class).getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE).get(1).getProvider());
			// Explicitly install a bundle into the application with the same
			// package dependency as B but must come from bundle AA.
			Bundle c = doBundleInstall(symbolicNameC, subsystem.getBundleContext(), null, symbolicNameC + ".jar", false);
			try {
				c.start();
				fail("Bundle C should have failed to resolve because package capability from bundle AA should not be visibile");
			}
			catch (BundleException e) {
				// Okay.
				e.printStackTrace();
			}
		} catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have started because dynamic package import " + dynamicPackage + " should have been added to the sharing policy");
		} finally {
			subsystem.uninstall();
		}
	}
	
	public void testDynamicImportWithWildcard() throws Exception {
		String symbolicNameA = "weaving.a";
		String symbolicNameAA = "weaving.aa";
		final String symbolicNameB = "weaving.b";
		String symbolicNameC = "weaving.c";
		final String dynamicPackage = "org.osgi.test.cases.subsystem.weaving.*";
		Subsystem root = getRootSubsystem();
		BundleContext context = root.getBundleContext();
		// Explicitly install bundle AA exporting package added by weaving hook
		// into root subsystem.
		Bundle bundle = doBundleInstall(symbolicNameAA, context, null, symbolicNameAA + ".jar", false);
		doBundleOperation(symbolicNameAA, bundle, Operation.START, false);
		context.registerService(WeavingHook.class, new WeavingHook() {
			public void weave(WovenClass wovenClass) {
				// This will get called when bundle B is started and the
				// Activator class is loaded.
				Bundle b = wovenClass.getBundleWiring().getBundle();
				if (symbolicNameB.equals(b.getSymbolicName()))
					wovenClass.getDynamicImports().add(dynamicPackage);
			}
		}, null);
		Subsystem subsystem = root.install(
				getName(),
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameA + ',' + symbolicNameB)
						// Also add bundle A as content so package added by
						// weaving hook is satisfied within subsystem.
						.bundle(symbolicNameA + ".jar")
						.bundle(symbolicNameB + ".jar").build());
		try {
			subsystem.start(); // This will start bundle B.
			// Explicitly install a bundle into the application with the same
			// package dependency as B but must come from bundle AA.
			Bundle c = doBundleInstall(symbolicNameC, subsystem.getBundleContext(), null, symbolicNameC + ".jar", false);
			try {
				c.start();
			}
			catch (BundleException e) {
				e.printStackTrace();
				fail("Bundle C should have resolved because package capability from bundle AA should be visibile due to wildcard");
			}
		} catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have started because dynamic package import " + dynamicPackage + " should have been added to the sharing policy");
		} finally {
			subsystem.uninstall();
		}
	}
	
	private Bundle findBundle(Subsystem subsystem, String symbolicName) {
		for (Bundle bundle : subsystem.getBundleContext().getBundles()) {
			if (bundle.getSymbolicName().equals(symbolicName)) {
				return bundle;
			}
		}
		return null;
	}
}
