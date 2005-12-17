/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.net.URL;
import java.util.*;
import org.osgi.framework.*;

public class PolicyHandler {
	//Key for the framework buddies
	private final static String DEPENDENT_POLICY = "dependent"; //$NON-NLS-1$
	private final static String GLOBAL_POLICY = "global"; //$NON-NLS-1$
	private final static String REGISTERED_POLICY = "registered"; //$NON-NLS-1$
	private final static String APP_POLICY = "app"; //$NON-NLS-1$
	private final static String EXT_POLICY = "ext"; //$NON-NLS-1$
	private final static String BOOT_POLICY = "boot"; //$NON-NLS-1$
	private final static String PARENT_POLICY = "parent"; //$NON-NLS-1$
	
	//The loader to which this policy is attached.
	BundleLoader policedLoader;
	//List of the policies as well as cache for the one that have been created. The size of this array never changes over time. This is why the synchronization is not done when iterating over it.
	Object[] policies = null;

	//Support to cut class / resource loading cycles in the context of one thread. The contained object is a set of classname
	private ThreadLocal beingLoaded;

	private BundleListener listener = new BundleListener() {
		public void bundleChanged(BundleEvent event) {
			if (event.getType() == BundleEvent.STARTED || event.getType() == BundleEvent.STOPPED)
				return;
			try {
				String list = (String) policedLoader.getBundle().getBundleData().getManifest().get(Constants.BUDDY_LOADER);
				synchronized (this) {
					policies = getArrayFromList(list);
				}
			} catch (BundleException e) {
				//Ignore
			}
		}
	};

	public PolicyHandler(BundleLoader loader, String buddyList) {
		policedLoader = loader;
		policies = getArrayFromList(buddyList);
		beingLoaded = new ThreadLocal();
		policedLoader.bundle.framework.systemBundle.context.addBundleListener(listener);
	}

	static Object[] getArrayFromList(String stringList) {
		if (stringList == null || stringList.trim().equals("")) //$NON-NLS-1$
			return null;
		Vector list = new Vector();
		StringTokenizer tokens = new StringTokenizer(stringList, ","); //$NON-NLS-1$
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			if (!token.equals("")) //$NON-NLS-1$
				list.addElement(token);
		}
		return list.isEmpty() ? new Object[0] : (Object[]) list.toArray(new Object[list.size()]);
	}

	private synchronized IBuddyPolicy getPolicyImplementation(int policyOrder) {
		if (policies[policyOrder] instanceof String) {
			String buddyName = (String) policies[policyOrder];

			if (REGISTERED_POLICY.equals(buddyName)) {
				policies[policyOrder] = new RegisteredPolicy(policedLoader);
				return (IBuddyPolicy) policies[policyOrder];
			}
			if (BOOT_POLICY.equals(buddyName)) {
				policies[policyOrder] = SystemPolicy.getInstance(SystemPolicy.BOOT);
				return (IBuddyPolicy) policies[policyOrder];
			}
			if (APP_POLICY.equals(buddyName)) {
				policies[policyOrder] = SystemPolicy.getInstance(SystemPolicy.APP);
				return (IBuddyPolicy) policies[policyOrder];
			}
			if (EXT_POLICY.equals(buddyName)) {
				policies[policyOrder] = SystemPolicy.getInstance(SystemPolicy.EXT);
				return (IBuddyPolicy) policies[policyOrder];
			}
			if (DEPENDENT_POLICY.equals(buddyName)) {
				policies[policyOrder] = new DependentPolicy(policedLoader);
				return (IBuddyPolicy) policies[policyOrder];
			}
			if (GLOBAL_POLICY.equals(buddyName)) {
				policies[policyOrder] = new GlobalPolicy(policedLoader.bundle.framework.packageAdmin);
				return (IBuddyPolicy) policies[policyOrder];
			}
			if (PARENT_POLICY.equals(buddyName)) {
				policies[policyOrder] = new SystemPolicy(policedLoader.parent);
				return (IBuddyPolicy) policies[policyOrder];
			}
			
			//			//Buddy policy can be provided by service implementations
			//			BundleContext fwkCtx = policedLoader.bundle.framework.systemBundle.context;
			//			ServiceReference[] matchingBuddies = null;
			//			try {
			//				matchingBuddies = fwkCtx.getAllServiceReferences(IBuddyPolicy.class.getName(), "buddyName=" + buddyName);
			//			} catch (InvalidSyntaxException e) {
			//				//The filter is valid
			//			}
			//			if (matchingBuddies == null)
			//				return new IBuddyPolicy() {
			//					public Class loadClass(String name) {
			//						return null;
			//					}
			//
			//					public URL loadResource(String name) {
			//						return null;
			//					}
			//
			//					public Enumeration loadResources(String name) {
			//						return null;
			//					}
			//				};
			//
			//			//The policies loaded through service are not cached
			//			return ((IBuddyPolicy) fwkCtx.getService(matchingBuddies[0]));
		}
		return (IBuddyPolicy) policies[policyOrder];
	}

	public Class doBuddyClassLoading(String name) {
		if (startLoading(name) == false)
			return null;

		Class result = null;
		for (int i = 0; i < policies.length && result == null; i++) {
			result = getPolicyImplementation(i).loadClass(name);
		}
		stopLoading(name);
		return result;
	}

	public URL doBuddyResourceLoading(String name) {
		if (startLoading(name) == false)
			return null;

		if (policies == null)
			return null;
		URL result = null;
		for (int i = 0; i < policies.length && result == null; i++) {
			result = getPolicyImplementation(i).loadResource(name);
		}
		stopLoading(name);
		return result;
	}

	public Enumeration doBuddyResourcesLoading(String name) {
		if (startLoading(name) == false)
			return null;

		if (policies == null)
			return null;
		Enumeration result = null;
		for (int i = 0; i < policies.length && result == null; i++) {
			result = getPolicyImplementation(i).loadResources(name);
		}
		stopLoading(name);
		return result;
	}

	private boolean startLoading(String name) {
		Set classesAndResources = (Set) beingLoaded.get();
		if (classesAndResources != null && classesAndResources.contains(name))
			return false;

		if (classesAndResources == null) {
			classesAndResources = new HashSet(3);
			beingLoaded.set(classesAndResources);
		}
		classesAndResources.add(name);
		return true;
	}

	private void stopLoading(String name) {
		((Set) beingLoaded.get()).remove(name);
	}

	public void close() {
		policedLoader.bundle.framework.systemBundle.context.removeBundleListener(listener);
	}
}
