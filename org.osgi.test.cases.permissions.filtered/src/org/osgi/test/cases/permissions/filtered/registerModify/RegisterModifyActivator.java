/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.permissions.filtered.registerModify;

import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.permissions.filtered.util.Util;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class RegisterModifyActivator implements BundleActivator {

	ServiceRegistration sr;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		System.out.println("REGISTER BUNDLE is going to start.");
		final Properties configuredProps = Util
				.getConfiguredProperties("bnd/properties/REGISTER_MODIFY.properties");

		final Hashtable props = new Hashtable();
		final int count = Integer.parseInt(configuredProps
				.getProperty("prop.count"));
		for (int i = 0; i < count; i++) {
			String key = configuredProps.getProperty("key." + i);
			String value = configuredProps.getProperty("value." + i);
			props.put(key, value);
		}
		String clazz = configuredProps.getProperty("objectClass");

		try {
			sr = context.registerService(clazz, new IServiceImpl(context),
					props);
			System.out
					.println("# Properties Modify Test> Succeed in registering service: "
							+ clazz);

		} catch (Exception e) {
			System.out
					.println("# Properties Modify Test> Fail to register service: "
							+ clazz);
			throw e;
		}

		int countMod = Integer.parseInt(configuredProps
				.getProperty("prop.count.mod"));
		for (int i = 0; i < countMod; i++) {
			String keyMod = configuredProps.getProperty("keyMod." + i);
			String valueMod = configuredProps.getProperty("valueMod." + i);
			props.put(keyMod, valueMod);
		}

		try {
			sr.setProperties(props);
			System.out
					.println("# Properties Modify Test> Succeed in modifying properties: "
							+ clazz);

		} catch (Exception e) {
			System.out
					.println("# Properties Modify Test> Fail to modify properties: "
							+ clazz);
			// e.printStackTrace();
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		sr = null;
	}

}
