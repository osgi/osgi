package org.osgi.test.cases.cm.shared;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class Util {

	public static String createPid() {
		return createPid(null);
	}

	public static String createPid(String suffix) {
		String root = Util.class.getName();
		if (suffix == null) {
			return root;
		}
		return root + "." + suffix;
	}

	public static Object getService(BundleContext context, String clazz) {
		ServiceReference reference = context.getServiceReference(clazz);
		if (reference == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ clazz);
		Object service = context.getService(reference);
		if (service == null)
			throw new IllegalStateException("Fail to get Service of " + clazz);
		return service;
	}

	public static Object getService(BundleContext context, String clazz,
			String filter) throws InvalidSyntaxException {
		ServiceReference[] references = context.getServiceReferences(clazz,
				filter);
		if (references == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ clazz);
		Object service = context.getService(references[0]);
		if (service == null)
			throw new IllegalStateException("Fail to get Service of " + clazz);
		return service;
	}
}
