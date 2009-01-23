package org.osgi.test.cases.permissions.filtered.tbc;

import java.io.FileInputStream;
import java.security.AllPermission;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissions.filtered.util.IServiceRegister;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author kondou
 * 
 *         This is the TestCase to confirm the behavior of Service Permission.
 * 
 */
public class FilteredTestControl extends DefaultTestBundleControl {
	private BundleContext context;

	private PermissionAdmin permAdmin;
	ServiceReference ref = null;

	private Hashtable bundlesTable = new Hashtable();

	// private static String REGISTER_BUNDLE_LOCATION =
	// "bnd/bundles/test.sp.register.jar";

	// private static String REGISTER_BUNDLE_BUNDLELOCATION =
	// "test.sp.register";

	// public void setBundleContext(BundleContext context) {
	// this.context = context;
	// }

	public void setUp() {
		context = super.getContext();
		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) context.getService(ref);

		PermissionInfo[] pis = new PermissionInfo[1];
		pis[0] = new PermissionInfo("(" + AllPermission.class.getName() + ")");
		permAdmin.setPermissions(context.getBundle().getLocation(), pis);

		/*
		 * File file = new File("policy.all"); boolean bl = file.exists();
		 * System.out.println(bl); Properties prop = System.getProperties();
		 * prop.list(System.out);
		 */

	}

	/**
	 * This method is implemented by Peter. It carries no special significance
	 * as a test, but memorable test.
	 */
	public void testFirst() {
		System.out.println(PackagePermission.class);
		boolean a = true;
		assertTrue(a);

	}

	/**
	 * This test is to confirm behavior of Service Permission when bundleA which
	 * is permitted to register serviceA registers serviceA. The class name
	 * isn't described using "filter", this test is to confirm backward
	 * compatibility.
	 * 
	 * This registration MUST succeed.
	 */
	public void testServiceRegistration2_1_1_1() throws Exception {

		Bundle regBundle = null;

		regBundle = installBundle(getWebServer() + "www/register.jar", false);
		// File fileReg = new File(REGISTER_BUNDLE_LOCATION);
		// FileInputStream fisReg = new FileInputStream(fileReg);
		// regBundle = context.installBundle(REGISTER_BUNDLE_BUNDLELOCATION,
		// fisReg);

		manageBundles();
		setPermissions("2_1_1_1");
		printPermissions();

		try {

			regBundle.start();

		} catch (BundleException be) {
			System.out.println("BUNDLE_START_ERROR");
		}

		try {
			ref = context.getServiceReference(IServiceRegister.class.getName());
			if (ref == null)
				fail("REGISTER_SERVICE_TEST_ERROR");
			IServiceRegister iRegService = (IServiceRegister) context
					.getService(ref);
			iRegService.registerIService("2_1_1_1");
			assertTrue(true);

		} catch (SecurityException expected) {
			expected.printStackTrace();
			fail("REGISTER_SERVICE_TEST_ERROR");
		}

		try {

			regBundle.stop();
			regBundle.uninstall();
		} catch (BundleException be) {
			System.out.println("UNINSTALL_ERROR");
		}

	}

	// /**
	// * This test is to confirm behavior of Service Permission when bundleA
	// which
	// * is permitted to register serviceB registers serviceA. The class name
	// * isn't described using "filter", this test is to confirm backward
	// * compatibility.
	// *
	// * This registration MUST fail.
	// */
	// public void testServiceRegistration2_1_1_2() {
	//
	// Bundle regBundle = null;
	//
	// try {
	//
	// File fileReg = new File(REGISTER_BUNDLE_LOCATION);
	// FileInputStream fisReg = new FileInputStream(fileReg);
	// regBundle = context.installBundle(REGISTER_BUNDLE_BUNDLELOCATION,
	// fisReg);
	// } catch (Exception e) {
	// System.out.println("INSTALL_ERROR");
	// }
	//
	// manageBundles();
	// setPermissions("2_1_1_2");
	// printPermissions();
	//
	// try {
	//
	// regBundle.start();
	//
	// } catch (BundleException be) {
	// System.out.println("BUNDLE_START_ERROR");
	// }
	//
	// try {
	// ref = context
	// .getServiceReference("org.osgi.test.cases.permissions.filtered.util.IServiceRegister");
	// IServiceRegister iRegService = (IServiceRegister) context
	// .getService(ref);
	// iRegService.registerService("2_1_1_2");
	// fail("REGISTER_SERVICE_TEST_ERROR");
	//
	// } catch (SecurityException expected) {
	// assertTrue(true);
	// }
	//
	// try {
	// regBundle.stop();
	// regBundle.uninstall();
	// } catch (BundleException be) {
	// System.out.println("UNINSTALL_ERROR");
	// }
	//
	// }

	private void setPermissions(String testid) {

		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream("bnd/permission/Test" + testid
					+ ".properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String scount = prop.getProperty("count");
		int count = Integer.parseInt(scount);

		for (int i = 0; i < count; i++) {

			String si = Integer.toString(i);
			String sbcount = prop.getProperty(si + ".perm.count");
			int bcount = Integer.parseInt(sbcount);

			PermissionInfo[] pis = new PermissionInfo[bcount];

			String bundleSymbolicName = prop.getProperty(si + ".sname");

			for (int j = 0; j < bcount; j++) {
				String sj = Integer.toString(j);

				String perm = prop.getProperty(si + ".perm." + sj);
				pis[j] = new PermissionInfo(perm);

			}
			if (bundleSymbolicName.equals("default")) {
				permAdmin.setDefaultPermissions(pis);

			} else {
				Bundle bundle = getSpecifiedBundle(bundleSymbolicName);
				permAdmin.setPermissions(bundle.getLocation(), pis);

			}
		}

	}

	private Bundle getSpecifiedBundle(String bundleSymbolicName) {
		Bundle bundle = (Bundle) this.bundlesTable.get(bundleSymbolicName);
		if (bundle == null)
			throw new IllegalStateException("No bundle \"" + bundleSymbolicName
					+ "\" exists.");
		return bundle;
	}

	private void manageBundles() {
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			this.bundlesTable.put(bundles[i].getSymbolicName(), bundles[i]);
		}

	}

	private void printPermissions() {
		PermissionInfo[] pis = permAdmin.getDefaultPermissions();
		if (pis == null) {
			System.out.println("DefaultPermissions[] is not set");
		} else {
			for (int i = 0; i < pis.length; i++) {
				System.out.println("DefaultPermissions[" + i + "]="
						+ pis[i].getEncoded());
			}
		}
		String[] locations = permAdmin.getLocations();
		if (locations == null) {
			System.out.println("pa.getLocation() == null");
		} else {
			for (int j = 0; j < locations.length; j++) {
				System.out.println("Permissions of (" + locations[j] + "):");
				pis = permAdmin.getPermissions(locations[j]);
				if (pis == null) {
					System.out.println("Permissions of (" + locations[j]
							+ ") is not set");
				} else {
					for (int i = 0; i < pis.length; i++) {
						System.out.println("\tPermission[" + i + "]="
								+ pis[i].getEncoded());
					}
				}
			}
		}

	}

}
