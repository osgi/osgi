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

/**
 * Register the metatype service and wait till someone uses it. Then initialize
 * the list of metatypes from bundles.
 * 
 * This information should be cached because it is horrendously expensive to
 * rebuild this list all the time. However, that is for real implementations.
 * 
 * @version $Revision$
 */
public class Activator implements BundleActivator, BundleListener {
	private Map		bundles	= new Hashtable();	// bundle -> MTI
	boolean			initialized;
	BundleContext	context;

	/**
	 * Register the MetatTypeService. 
	 * 
	 * We use a proxy so that we can use public methods.
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		context.registerService(MetaTypeService.class.getName(), new MTS(this),
				null);
	}

	/**
	 * Check if a bunlde has meta data, if so parse
	 * it and construct the internal structures.
	 * 
	 * We assume the metatype info is stoed in the META-INF/metatype
	 * directory. We parse all the files in that directory
	 * according to the meta data schema.
	 * 
	 */
	void parseBundle(Bundle target) throws IOException, Exception {
		MTI mti = new MTI(target, null);

		Enumeration paths = target.getEntryPaths("META-INF/metatype/");
		while (paths.hasMoreElements()) {
			mti.parseMetaData(target.getEntry((String) paths.nextElement()));
		}

		bundles.put(target, mti);
	}

	/** 
	 * We let the framework do the clean up ... as it should.
	 */
	public void stop(BundleContext context) throws Exception {
	}

	/**
	 * Once we are initialized, we listen to changes to the
	 * bundle set. 
	 * 
	 */
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

	/**
	 * This method is called by the MetaType Service proxy. It checks
	 * if we are already initialized, if not, we do that.
	 * 
	 * We now go through all the bundles and check their
	 * meta data. This is obviously expensive.
	 * 
	 * @param bundle	The bundle to analyze
	 * @return			The MetaTypeInformation object
	 */
	// TODO handle caching, this is not acceptable for a real
	// world impl.
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