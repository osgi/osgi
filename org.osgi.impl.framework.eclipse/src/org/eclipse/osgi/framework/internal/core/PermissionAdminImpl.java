/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.net.URL;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.util.Vector;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.PermissionStorage;
import org.eclipse.osgi.framework.debug.Debug;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * Permission Admin service for the OSGi specification.
 *
 * The Permission Admin service allows operators to
 * manage the permissions of bundles. There is at most one Permission Admin
 * service present in the Framework.
 * <p>
 * Access to the Permission Admin service is protected by
 * corresponding
 * <tt>ServicePermission</tt>. In addition the <tt>AdminPermission</tt>
 * is required to actually set permissions.
 *
 * <p>Bundle permissions are managed using a permission table. A bundle's location
 * serves as the key into this permission table. The value of a table entry is
 * the set of permissions (of type <tt>PermissionInfo</tt>) granted to the
 * bundle with the given location.
 * A bundle may have an entry in the permission table prior to being installed
 * in the Framework.
 *
 * <p>The permissions specified in <tt>setDefaultPermissions</tt> are used as the
 * default
 * permissions which are granted to all bundles that do not have an entry in
 * the permission table.
 *
 * <p>Any changes to a bundle's permissions in the permission table will take
 * effect no later than when bundle's <tt>java.security.ProtectionDomain</tt>
 * is involved in a permission check, and will be made persistent.
 *
 * <p>Only permission classes on the system classpath or from an exported
 * package are considered during a permission check.
 * Additionally, only permission classes that are subclasses of
 * <tt>java.security.Permission</tt> and define a 2-argument constructor
 * that takes a <i>name</i> string and an <i>actions</i> string can be used.
 * <p>
 * Permissions implicitly granted by the Framework (for example, a bundle's
 * permission to access its persistent storage area) cannot be changed, and
 * are not reflected in the permissions returned by <tt>getPermissions</tt>
 * and <tt>getDefaultPermissions</tt>.
 */
public class PermissionAdminImpl implements PermissionAdmin {
	/** framework object */
	protected Framework framework;

	/** permission storage object */
	protected PermissionStorage storage;

	/** The permissions to use if no other permissions can be determined */
	protected PermissionInfo[] defaultDefaultPermissionInfos;

	/** The basic implied permissions for a bundle */
	protected PermissionInfo[] baseImpliedPermissionInfos;

	/** The permission collection containing the default assigned permissions */
	protected BundleCombinedPermissions defaultAssignedPermissions;

	/**
	 * Construstor.
	 *
	 * @param framework Framework object.
	 */
	protected PermissionAdminImpl(Framework framework, PermissionStorage storage) {
		this.framework = framework;
		this.storage = storage;

		defaultDefaultPermissionInfos = getPermissionInfos(Constants.OSGI_DEFAULT_DEFAULT_PERMISSIONS);
		baseImpliedPermissionInfos = getPermissionInfos(Constants.OSGI_BASE_IMPLIED_PERMISSIONS);

		//      if (Debug.DEBUG)
		//      {
		//          // This is necessary to allow File.getAbsolutePath() in debug statements.
		//          int _length = baseImpliedPermissionInfos.length;
		//
		//          PermissionInfo[] debugBaseImpliedPermissionInfos = new PermissionInfo[_length + 1];
		//
		//          System.arraycopy(baseImpliedPermissionInfos, 0, debugBaseImpliedPermissionInfos, 0, _length);
		//
		//          debugBaseImpliedPermissionInfos[_length] = new PermissionInfo("(java.util.PropertyPermission \"user.dir\" \"read\")");
		//
		//          baseImpliedPermissionInfos = debugBaseImpliedPermissionInfos;
		//      }

		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("Default default assigned bundle permissions"); //$NON-NLS-1$
			if (defaultDefaultPermissionInfos == null) {
				Debug.println("  <none>"); //$NON-NLS-1$
			} else {
				for (int i = 0; i < defaultDefaultPermissionInfos.length; i++) {
					Debug.println("  " + defaultDefaultPermissionInfos[i]); //$NON-NLS-1$
				}
			}

			Debug.println("Base implied bundle permissions"); //$NON-NLS-1$
			if (baseImpliedPermissionInfos == null) {
				Debug.println("  <none>"); //$NON-NLS-1$
			} else {
				for (int i = 0; i < baseImpliedPermissionInfos.length; i++) {
					Debug.println("  " + baseImpliedPermissionInfos[i]); //$NON-NLS-1$
				}
			}
		}

		defaultAssignedPermissions = new BundleCombinedPermissions(null);
		defaultAssignedPermissions.setAssignedPermissions(createDefaultAssignedPermissions(getDefaultPermissions()), true);
	}

	/**
	 * Gets the permissions assigned to the bundle with the specified
	 * location.
	 *
	 * @param location The location of the bundle whose permissions are to
	 * be returned.
	 *
	 * @return The permissions assigned to the bundle with the specified
	 * location, or <tt>null</tt> if that bundle has not been assigned any
	 * permissions.
	 */
	public PermissionInfo[] getPermissions(String location) {
		if (location == null) {
			throw new NullPointerException();
		}

		PermissionStorage storage = new org.eclipse.osgi.framework.internal.core.SecurePermissionStorage(this.storage);

		try {
			String[] data = storage.getPermissionData(location);

			if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
				Debug.println("Getting permissions for location: " + location); //$NON-NLS-1$
				if (data == null) {
					Debug.println("  <none>"); //$NON-NLS-1$
				} else {
					for (int i = 0; i < data.length; i++) {
						Debug.println("  " + data[i]); //$NON-NLS-1$
					}
				}
			}

			return makePermissionInfo(data);
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);

			return null;
		}
	}

	/**
	 * Assigns the specified permissions to the bundle with the specified
	 * location.
	 *
	 * @param location The location of the bundle that will be assigned the
	 *                 permissions.
	 * @param permissions The permissions to be assigned, or <tt>null</tt>
	 * if the specified location is to be removed from the permission table.
	 * @exception SecurityException if the caller does not have the
	 * <tt>AdminPermission</tt>.
	 */
	public void setPermissions(String location, PermissionInfo[] permissions) {
		framework.checkAdminPermission(framework.systemBundle, AdminPermission.PERMISSION);

		if (location == null) {
			throw new NullPointerException();
		}

		PermissionStorage storage = new org.eclipse.osgi.framework.internal.core.SecurePermissionStorage(this.storage);

		try {
			String[] data = makePermissionData(permissions);

			if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
				Debug.println("Setting permissions for location: " + location); //$NON-NLS-1$
				if (data == null) {
					Debug.println("  <none>"); //$NON-NLS-1$
				} else {
					for (int i = 0; i < data.length; i++) {
						Debug.println("  " + data[i]); //$NON-NLS-1$
					}
				}
			}

			storage.setPermissionData(location, data);
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);

			return;
		}

		AbstractBundle bundle = framework.getBundleByLocation(location);

		if ((bundle != null) && (bundle.getBundleId() != 0)) {
			ProtectionDomain domain = bundle.getProtectionDomain();

			if (domain != null) {
				BundleCombinedPermissions combined = (BundleCombinedPermissions) domain.getPermissions();

				if (permissions == null) {
					combined.setAssignedPermissions(defaultAssignedPermissions, true);
				} else {
					combined.setAssignedPermissions(createPermissions(permissions, bundle), false);
				}
			}
		}
	}

	/**
	 * Returns the bundle locations that have permissions assigned to them,
	 * that is, bundle locations for which an entry
	 * exists in the permission table.
	 *
	 * @return The locations of bundles that have been assigned any
	 * permissions, or <tt>null</tt> if the permission table is empty.
	 */
	public String[] getLocations() {
		PermissionStorage storage = new org.eclipse.osgi.framework.internal.core.SecurePermissionStorage(this.storage);

		try {
			String[] locations = storage.getLocations();

			return locations;
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);

			return null;
		}
	}

	/**
	 * Gets the default permissions.
	 *
	 * <p>These are the permissions granted to any bundle that does not
	 * have permissions assigned to its location.
	 *
	 * @return The default permissions, or <tt>null</tt> if default
	 * permissions have not been defined.
	 */
	public PermissionInfo[] getDefaultPermissions() {
		PermissionStorage storage = new org.eclipse.osgi.framework.internal.core.SecurePermissionStorage(this.storage);

		try {
			String[] data = storage.getPermissionData(null);

			if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
				Debug.println("Getting default permissions"); //$NON-NLS-1$
				if (data == null) {
					Debug.println("  <none>"); //$NON-NLS-1$
				} else {
					for (int i = 0; i < data.length; i++) {
						Debug.println("  " + data[i]); //$NON-NLS-1$
					}
				}
			}

			return makePermissionInfo(data);
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);

			return null;
		}
	}

	/**
	 * Sets the default permissions.
	 *
	 * <p>These are the permissions granted to any bundle that does not
	 * have permissions assigned to its location.
	 *
	 * @param permissions The default permissions.
	 * @exception SecurityException if the caller does not have the
	 * <tt>AdminPermission</tt>.
	 */
	public void setDefaultPermissions(PermissionInfo[] permissions) {
		framework.checkAdminPermission(framework.systemBundle, AdminPermission.PERMISSION);

		PermissionStorage storage = new org.eclipse.osgi.framework.internal.core.SecurePermissionStorage(this.storage);

		try {
			String[] data = makePermissionData(permissions);

			if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
				Debug.println("Setting default permissions"); //$NON-NLS-1$
				if (data == null) {
					Debug.println("  <none>"); //$NON-NLS-1$
				} else {
					for (int i = 0; i < data.length; i++) {
						Debug.println("  " + data[i]); //$NON-NLS-1$
					}
				}
			}

			storage.setPermissionData(null, data);
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);

			return;
		}

		defaultAssignedPermissions.setAssignedPermissions(createDefaultAssignedPermissions(permissions), true);
	}

	/**
	 * Make a PermissionInfo array from an array of encoded permission Strings.
	 *
	 * @param data Array of encoded permission Strings
	 * @return Array of PermissionInfo objects.
	 */
	protected PermissionInfo[] makePermissionInfo(String[] data) {
		if (data == null) {
			return null;
		}

		int size = data.length;

		PermissionInfo[] permissions = new PermissionInfo[size];

		for (int i = 0; i < size; i++) {
			permissions[i] = new PermissionInfo(data[i]);
		}

		return permissions;
	}

	/**
	 * Make an array of encoded permission Strings from a PermissionInfo array.
	 *
	 * @param permissions Array of PermissionInfor objects.
	 * @return Array of encoded permission Strings
	 */
	protected String[] makePermissionData(PermissionInfo[] permissions) {
		if (permissions == null) {
			return null;
		}

		int size = permissions.length;

		String[] data = new String[size];

		for (int i = 0; i < size; i++) {
			data[i] = permissions[i].getEncoded();
		}

		return data;
	}

	/**
	 * This method is called by the Bundle object to create the
	 * PermissionCollection used by the bundle's ProtectionDomain.
	 *
	 * @param bundle the bundle object
	 * @return BundleCombinedPermission object with the bundle's
	 * dynamic permissions.
	 */
	protected BundleProtectionDomain createProtectionDomain(AbstractBundle bundle) {
		BundlePermissionCollection implied = getImpliedPermissions(bundle);

		BundleCombinedPermissions combined = new BundleCombinedPermissions(implied);

		BundlePermissionCollection assigned = getAssignedPermissions(bundle);

		combined.setAssignedPermissions(assigned, assigned == defaultAssignedPermissions);

		combined.setConditionalPermissions(new ConditionalPermissions(bundle, framework.condPermAdmin));

		/* now process the permissions.perm file, if it exists, and build the
		 * restrictedPermissions using it. */
		URL u = bundle.getEntry("META-INF/permissions.perm"); //$NON-NLS-1$
		if (u != null) {
			try {
				DataInputStream dis = new DataInputStream(u.openStream());
				String line;
				Vector piList = new Vector();
				while ((line = dis.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("#") || line.startsWith("//") || line.length() == 0)  //$NON-NLS-1$//$NON-NLS-2$
						continue;
					try {
						PermissionInfo pi = new PermissionInfo(line);
						piList.add(pi);
					} catch (Exception e) {
						// Right now we just eat any exception that happens when
						// parsing the PermissionInfo
						framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
					}
				}
				ConditionalPermissionInfoImpl cpiArray[] = new ConditionalPermissionInfoImpl[1];
				cpiArray[0] = new ConditionalPermissionInfoImpl(new ConditionInfo[0], (PermissionInfo[]) piList.toArray(new PermissionInfo[0]));
				ConditionalPermissionSet cps = new ConditionalPermissionSet(cpiArray, new Condition[0]);
				combined.setRestrictedPermissions(cps);
			} catch (IOException e) {
				// TODO What do we do here? The fact that we got the URL indicates that
				// the file exists, but now we can't read it for some reason...
				framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
			}
		}

		return new BundleProtectionDomainImpl(bundle, combined);
	}

	/**
	 * Creates the default assigned permissions for bundles that
	 * have no assigned permissions.
	 * The default permissions are assigned via the PermissionAdmin service
	 * and may change dynamically.
	 *
	 * @return A PermissionCollection of the default assigned permissions.
	 */
	protected BundlePermissionCollection createDefaultAssignedPermissions(PermissionInfo[] info) {
		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("Creating default assigned permissions"); //$NON-NLS-1$
		}

		if (info == null) {
			info = defaultDefaultPermissionInfos;
		}

		return createPermissions(info, null);
	}

	/**
	 * Returns the assigned permissions for a bundle.
	 * These permissions are assigned via the PermissionAdmin service
	 * and may change dynamically.
	 *
	 * @param bundle The bundle to create the permissions for.
	 * @return A PermissionCollection of the assigned permissions.
	 */
	protected BundlePermissionCollection getAssignedPermissions(AbstractBundle bundle) {
		String location = bundle.getLocation();

		PermissionInfo[] info = getPermissions(location);

		if (info == null) {
			return defaultAssignedPermissions;
		}

		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("Creating assigned permissions for " + bundle); //$NON-NLS-1$
		}

		return createPermissions(info, bundle);
	}

	/**
	 * Returns the implied permissions for a bundle.
	 * These permissions never change.
	 *
	 * @param bundle The bundle to create the permissions for.
	 * @return A PermissionCollection of the implied permissions.
	 */
	protected BundlePermissionCollection getImpliedPermissions(AbstractBundle bundle) {
		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("Creating implied permissions for " + bundle); //$NON-NLS-1$
		}

		BundlePermissionCollection collection = createPermissions(baseImpliedPermissionInfos, bundle);

		Permission permission = new BundleResourcePermission(bundle.getBundleId());

		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("Created permission: " + permission); //$NON-NLS-1$
		}

		collection.add(permission);

		return collection;
	}

	/**
	 * Read the permissions from the specified resource.
	 *
	 * @return An array of PermissionInfo objects from the specified
	 * resource.
	 */
	protected PermissionInfo[] getPermissionInfos(String resource) {
		PermissionInfo[] info = null;

		InputStream in = getClass().getResourceAsStream(resource);

		if (in != null) {
			try {
				Vector permissions = new Vector();

				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(in, "UTF8")); //$NON-NLS-1$
				} catch (UnsupportedEncodingException e) {
					reader = new BufferedReader(new InputStreamReader(in));
				}

				while (true) {
					String line = reader.readLine();

					if (line == null) /* EOF */{
						break;
					}

					line = line.trim();

					if ((line.length() == 0) || line.startsWith("#") || line.startsWith("//")) /* comments */{ //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					}

					try {
						permissions.addElement(new PermissionInfo(line));
					} catch (IllegalArgumentException iae) {
						/* incorrectly encoded permission */

						framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, iae);
					}
				}

				int size = permissions.size();

				if (size > 0) {
					info = new PermissionInfo[size];

					permissions.copyInto(info);
				}
			} catch (IOException e) {
			} finally {
				try {
					in.close();
				} catch (IOException ee) {
				}
			}
		}

		return info;
	}

	/**
	 * Create a PermissionCollection from a PermissionInfo array.
	 *
	 * @param info Array of PermissionInfo objects.
	 * @param bundle The target bundle for the permissions.
	 * @return A PermissionCollection containing Permission objects.
	 */
	protected BundlePermissionCollection createPermissions(PermissionInfo[] info, final AbstractBundle bundle) {
		BundlePermissionCollection collection = new BundlePermissions(framework.packageAdmin);

		/* add the permissions */
		int size = info.length;
		for (int i = 0; i < size; i++) {
			PermissionInfo perm = info[i];

			String type = perm.getType();

			if (type.equals("java.io.FilePermission")) { //$NON-NLS-1$
				/* map FilePermissions for relative names to
				 * the bundle's data area
				 */
				String name = perm.getName();

				if (!name.equals("<<ALL FILES>>")) { //$NON-NLS-1$
					File file = new File(name);

					if (!file.isAbsolute()) /* relative name */{
						if (bundle == null) /* default permissions */{
							continue; /* no relative file permissions */
						}

						File target = framework.getDataFile(bundle, name);

						if (target == null) /* no bundle data file area */{
							continue; /* no relative file permissions */
						}

						perm = new PermissionInfo(type, target.getPath(), perm.getActions());
					}
				}
			}

			collection.add(createPermission(perm));
		}

		return collection;
	}

	/**
	 * Create a Permission object from a PermissionInfo object.
	 * If the type of the permission is not loadable from
	 * this object's classloader (i.e. the system classloader)
	 * then an UnresolvedPermission is returned.
	 *
	 * @param info Description of the desired permission.
	 * @return A permission object.
	 */
	protected Permission createPermission(PermissionInfo info) {
		String type = info.getType();
		String name = info.getName();
		String actions = info.getActions();

		UnresolvedPermission permission = new UnresolvedPermission(type, name, actions);

		try {
			/* Only search the system classloader (ours) at this point.
			 * Permission classes exported by bundles will be
			 * resolved later.
			 * This is done so that permission classes exported by bundles
			 * may be easily unresolved during packageRefresh.
			 */
			Class clazz = Class.forName(type);

			Permission resolved = permission.resolve(clazz);

			if (resolved != null) {
				if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
					Debug.println("Created permission: " + resolved); //$NON-NLS-1$
				}

				return resolved;
			}
		} catch (ClassNotFoundException e) {
		}

		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("Created permission: " + permission); //$NON-NLS-1$
		}

		return permission;
	}
}
