/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.metatype;

import java.io.IOException;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.metatype.MetaTypeService;

public class Activator implements BundleActivator, BundleListener {
	private Map		bundles	= new Hashtable();	// bundle -> MTI
	boolean			initialized;
	BundleContext	context;

	public void start(BundleContext context) throws Exception {
		this.context = context;

		context.registerService(MetaTypeService.class.getName(), new MTS(this),
				null);
	}

	void parseBundle(Bundle target) throws IOException, Exception {
		MTI mti = new MTI(target, null);

		Enumeration paths = target.getEntryPaths("META-INF/metatype/");
		while (paths.hasMoreElements()) {
			mti.parseMetaData(target.getEntry((String) paths.nextElement()));
		}

		bundles.put(target, mti);
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye World");
	}

	public void bundleChanged(BundleEvent event) {
		try {
			switch (event.getType()) {
				case BundleEvent.STARTED :
					if (bundles.containsKey(event.getBundle()))
						return;
					parseBundle(event.getBundle());
					return;

				case BundleEvent.INSTALLED :
					parseBundle(event.getBundle());
					return;

				case BundleEvent.UNINSTALLED :
					bundles.remove(event.getBundle());

				case BundleEvent.STOPPED :
				case BundleEvent.RESOLVED :
				case BundleEvent.UNRESOLVED :
			}
		}
		catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}

	// TODO handle caching, this is not
	MTI getMTI(Bundle bundle) {

		try {
			if (!initialized) {
				synchronized (bundles) {
					if (!initialized) {
						initialized = true;
						context.addBundleListener(this);
						Bundle[] all = context.getBundles();
						for (int i = 0; i < all.length; i++) {
							if (!bundles.containsKey(all[i]))
								parseBundle(all[i]);
						}
					}
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (MTI) bundles.get(bundle);
	}
}