/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.framework;

import java.security.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * Here we handle all the services that are registered in framework.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Services {
	/**
	 * All registered services in current framework.
	 */
	private List	/* ServiceRegistration */services				= new ArrayList();
	/**
	 * Mapping of classname to registered service.
	 */
	private Map	/* String->ServiceRegistration */classServices	= new HashMap();

	/**
	 * Register a service in the framework wide register.
	 * 
	 * @param bundle The bundle registering the service.
	 * @param classes The class names under which the service can be located.
	 * @param service The service object.
	 * @param properties The properties for this service.
	 * @return A {@link ServiceRegistration}object.
	 * @exception java.lang.IllegalArgumentException If one of the following is
	 *            true:
	 *            <ul>
	 *            <li>The service object is null.</li>
	 *            <li>The defining class of the service paramater is not owned
	 *            by the bundle.</li>
	 *            <li>The service parameter is not a ServiceFactory and is not
	 *            an instance of all the named classes in the classes parameter.
	 *            </li>
	 *            </ul>
	 */
	ServiceRegistration register(BundleImpl bundle, String[] classes,
			Object service, Dictionary properties) {

		if (service == null) {
			throw new IllegalArgumentException("Can't register null as service");
		}
		ServiceRegistration res;
		synchronized (this) {
			// Check if service implements claimed classes and that they exists.
			ClassLoader cl = service.getClass().getClassLoader();
			HashSet	 requiredInterfaces = new HashSet();
			
			for (int i = 0; i < classes.length; i++) {
				if (classes[i] == null) {
					throw new IllegalArgumentException(
							"Can't register as null class");
				}
				if (classes[i].equals(PackageAdmin.class.getName())
						&& bundle.getBundleId() != 0) {
					throw new IllegalArgumentException(
							"Illegal to register PackageAdmin service");
				}
				if (classes[i].equals(PermissionAdmin.class.getName())
						&& bundle.getBundleId() != 0) {
					throw new IllegalArgumentException(
							"Illegal to register PermissionAdmin service");
				}
				if (classes[i].equals(StartLevel.class.getName())
						&& bundle.getBundleId() != 0) {
					throw new IllegalArgumentException(
							"Illegal to register StartLevel service");
				}
				if (bundle.framework.checkPermissions) {
					AccessController.checkPermission(new ServicePermission(
							classes[i], ServicePermission.REGISTER));
				}
				/* Change 2004 Nov. 4 because previous check was not taking
				 * into account superclasses */
				Class c;
				try {
					c = Class.forName(classes[i], true, bundle.getClassLoader());
				}
				catch (ClassNotFoundException e) {
					try {
						PkgEntry pe = getPackageEntry(classes[i]);
						c = Class.forName(classes[i], true, pe.bundle.getClassLoader());
					} catch( ClassNotFoundException ee ) {
						throw new IllegalArgumentException("Class does not exist in bundle nor in exported space: "
							+ classes[i]);
					}
				}
				/* End change */
				if (!(service instanceof ServiceFactory)
						&& !c.isInstance(service)) {
					throw new IllegalArgumentException("Object " + service
							+ " is not an instance of " + classes[i]);
				}
			}
			
			res = new ServiceRegistrationImpl(bundle, service,
					new PropertiesDictionary(properties, classes, null));
			services.add(res);
			for (int i = 0; i < classes.length; i++) {
				ArrayList s = (ArrayList) classServices.get(classes[i]);
				if (s == null) {
					s = new ArrayList(1);
					classServices.put(classes[i], s);
				}
				s.add(res);
			}
		}
		bundle.framework.listeners.serviceChanged(new ServiceEvent(
				ServiceEvent.REGISTERED, res.getReference()));
		return res;
	}

		/**
		 * Utility method to get the PkgEntry from the class name
		 * 
		 * @param name
		 * @return
		 */
	PkgEntry getPackageEntry(String name ) {
		int pos = name.lastIndexOf('.');
		if (pos != -1) {
			String pkg = name.substring(0, pos);
			return Main.framework.packages.getProvider(pkg);
		}
		return null;
	}
	
	/**
	 * Get a service implementing a certain class.
	 * 
	 * @param clazz The class name of requested service.
	 * @return A {@link ServiceReference}object.
	 */
	synchronized ServiceReference get(String clazz) {
		ArrayList v = (ArrayList) classServices.get(clazz);
		if (v != null) {
			ServiceReference res = ((ServiceRegistration) v.get(0))
					.getReference();
			for (int i = 1; i < v.size(); i++) {
				ServiceReference s = ((ServiceRegistration) v.get(i))
						.getReference();
				if (ranking(s) > ranking(res)) {
					res = s;
				}
			}
			return res;
		}
		else {
			return null;
		}
	}

	/**
	 * Get all services implementing a certain class and then filter these with
	 * a property filter.
	 * 
	 * @param clazz The class name of requested service.
	 * @param filter The property filter.
	 * @return An array of {@link ServiceReference}object.
	 */
	synchronized ServiceReference[] get(String clazz, String filter)
			throws InvalidSyntaxException {
		Iterator s;
		
		if (clazz == null) {
			s = services.iterator();
			if (s == null) {
				return null;
			}
		}
		else {
			ArrayList v = (ArrayList) classServices.get(clazz);
			if (v != null) {
				s = v.iterator();
			}
			else {
				return null;
			}
		}
		ArrayList res = new ArrayList();
		filter = LDAPQuery.canonicalize(filter);
		while (s.hasNext()) {
			ServiceRegistrationImpl sr = (ServiceRegistrationImpl) s.next();
			if (filter == null || LDAPQuery.query(filter, sr.properties)) {
				res.add(sr.getReference());
			}
		}
		if (res.isEmpty()) {
			return null;
		}
		else {
			ServiceReference[] a = new ServiceReference[res.size()];
			res.toArray((Object[]) a);
			return a;
		}
	}

	/**
	 * Remove a registered service.
	 * 
	 * @param sr The ServiceRegistration object that is registered.
	 */
	synchronized void removeServiceRegistration(ServiceRegistrationImpl sr) {
		String[] classes = (String[]) sr.properties.get(Constants.OBJECTCLASS);
		services.remove(sr);
		for (int i = 0; i < classes.length; i++) {
			ArrayList s = (ArrayList) classServices.get(classes[i]);
			if (s.size() > 1) {
				s.remove(sr);
			}
			else {
				classServices.remove(classes[i]);
			}
		}
	}

	/**
	 * Get all services that a bundle has registered.
	 * 
	 * @param b The bundle
	 * @return A set of {@link ServiceRegistration}objects
	 */
	synchronized Set getRegisteredByBundle(Bundle b) {
		HashSet res = new HashSet();
		for (Iterator e = services.iterator(); e.hasNext();) {
			ServiceRegistrationImpl sr = (ServiceRegistrationImpl) e.next();
			if (sr.bundle == b) {
				res.add(sr);
			}
		}
		return res;
	}

	/**
	 * Get all services that a bundle uses.
	 * 
	 * @param b The bundle
	 * @return A set of {@link ServiceRegistration}objects
	 */
	synchronized Set getUsedByBundle(Bundle b) {
		HashSet res = new HashSet();
		for (Iterator e = services.iterator(); e.hasNext();) {
			ServiceRegistrationImpl sr = (ServiceRegistrationImpl) e.next();
			if (sr.isUsedByBundle(b)) {
				res.add(sr);
			}
		}
		return res;
	}

	//
	// Private methods
	//
	/**
	 * Get service ranking from a service reference.
	 * 
	 * @param s The service reference
	 * @return Ranking value of service, default value is zero
	 */
	private int ranking(ServiceReference s) {
		Object v = s.getProperty(Constants.SERVICE_RANKING);
		if (v != null && v instanceof Integer) {
			return ((Integer) v).intValue();
		}
		else {
			return 0;
		}
	}
}
