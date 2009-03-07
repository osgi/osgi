package org.osgi.test.cases.permissions.filtered.setPermission;

import java.io.FileInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Bundle;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissions.filtered.util.*;

public class SetPermissionService implements ISetPermissionService {
	private final BundleContext context;
	private PermissionAdmin permAdmin;
	private Hashtable /* <String bundleSymbolicName, Bundle bundle> */bundlesTable = new Hashtable();

	SetPermissionService(BundleContext context) {
		this.context = context;
	}

	public void setPermissions(final String testId) {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				manageBundles();
				ServiceReference ref = context
						.getServiceReference(PermissionAdmin.class.getName());
				if (ref == null) {
					System.out.println("Fail to get ServiceReference of "
							+ PermissionAdmin.class.getName());
				}
				permAdmin = (PermissionAdmin) context.getService(ref);
				Properties prop = new Properties();
				try {
					prop.load(new FileInputStream("bnd/permission/Test"
							+ testId + ".properties"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				final int count = Integer.parseInt(prop.getProperty("count"));
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
						System.out.println(bundle.getLocation());
						permAdmin.setPermissions(bundle.getLocation(), pis);
					}
				}
				return null;
			}
		});
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
		for (int i = 0; i < bundles.length; i++)
			this.bundlesTable.put(bundles[i].getSymbolicName(), bundles[i]);
	}
}
