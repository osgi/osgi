/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.metatype.utest;

import org.osgi.framework.*;
import org.osgi.service.metatype.*;
import org.osgi.util.tracker.ServiceTracker;

/*
 * 
 */
public class Activator implements BundleActivator {

	BundleContext	_context	= null;

	/**
	 * Default constructor. This is constructed by the framework prior to
	 * calling the BundleActivator.start method.
	 * 
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		System.out.println("====== Testing bundle is starting =====");
		this._context = context;
		showMetaData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {

		System.out.println("====== Test bundle is stoping =====");
		context = null;
	}

	/*
	 * 
	 */
	public void showMetaData() {

		ServiceTracker tracker = new ServiceTracker(_context,
				MetaTypeService.class.getName(), null);

		tracker.open();
		MetaTypeService mts = (MetaTypeService) tracker.getService();
		if (mts == null)
			System.out.println("Not MetaTypeService");
		Bundle[] bs = _context.getBundles();
		if (bs.length > 0) {
			System.out.println("There are total " + bs.length + " bundles.");
			for (int bIdx = 0; bIdx < bs.length; bIdx++) {

				MetaTypeInformation mti = mts.getMetaTypeInformation(bs[bIdx]);
				String[] pids = mti.getPids();
				String[] factoryPids = mti.getFactoryPids();
				String[] locales = mti.getLocales();
				if ((pids.length + factoryPids.length) > 0) {
					System.out.println("Bundle(" + bs[bIdx].getBundleId()
							+ ", " + bs[bIdx].getSymbolicName()
							+ ") has Meta data.");
				}

				for (int pidIdx = 0; pidIdx < pids.length; pidIdx++) {
					System.out
							.println("\tThis is PIDs: (" + pids[pidIdx] + ")");
					ObjectClassDefinition ocd = mti.getObjectClassDefinition(
							pids[pidIdx], null);
					showOCD(ocd);
				}

				for (int fpidIdx = 0; fpidIdx < factoryPids.length; fpidIdx++) {
					System.out.println("\tThis is FPIDs: ("
							+ factoryPids[fpidIdx] + ")");
					ObjectClassDefinition ocd = mti.getObjectClassDefinition(
							factoryPids[fpidIdx], null);
					showOCD(ocd);
				}
			}
		}
		tracker.close();

	}
	public void showOCD(ObjectClassDefinition ocd) {

		AttributeDefinition[] ADs = ocd
				.getAttributeDefinitions(ObjectClassDefinition.ALL);

		for (int adIdx = 0; adIdx < ADs.length; adIdx++) {
			System.out.println("\t\tOCD name: (" + ocd.getName() + ")");
			System.out.println("\t\tOCD id: (" + ocd.getID() + ")");
			System.out.println("\t\tOCD description: ("
					+ ocd.getDescription() + ")");
			try {
			ocd.getIcon(0);
			} catch (Exception e) {}

			System.out.println("\t\tAD[" + adIdx + "] name: ("
					+ ADs[adIdx].getName() + ")");
			System.out.println("\t\tAD[" + adIdx + "] id: ("
					+ ADs[adIdx].getID() + ")");
			System.out.println("\t\tAD[" + adIdx + "] type: ("
					+ ADs[adIdx].getType() + ")");
			System.out.println("\t\tAD[" + adIdx
					+ "] cardinality: ("
					+ ADs[adIdx].getCardinality() + ")");
			System.out.println("\t\tAD[" + adIdx
					+ "] description: ("
					+ ADs[adIdx].getDescription() + ")");

			String[] defaultValues = ADs[adIdx].getDefaultValue();
			if (defaultValues != null) {
				System.out.println("\t\t\tThere are total "
						+ defaultValues.length
						+ " default values.");
				for (int dfValIdx = 0; dfValIdx < defaultValues.length; dfValIdx++) {
					System.out.println("\t\t\t - Default Value["
							+ dfValIdx + "]: ("
							+ defaultValues[dfValIdx] + ")");
				}
			}
			else {
				System.out.println("\t\t\tThere is no default value.");
			}

			String[] optionLabels = ADs[adIdx].getOptionLabels();
			if (optionLabels != null) {
				System.out.println("\t\t\tThere are total "
						+ optionLabels.length + " option labels.");
				for (int optLabIdx = 0; optLabIdx < optionLabels.length; optLabIdx++) {
					System.out.println("\t\t\t - Option Labels["
							+ optLabIdx + "]: ("
							+ optionLabels[optLabIdx] + ")");
				}
			}
			else {
				System.out.println("\t\t\tOption Labels is null");
			}

			String[] optionValues = ADs[adIdx].getOptionValues();
			if (optionValues != null) {
				System.out.println("\t\t\tThere are total "
						+ optionValues.length + " option values.");
				for (int optValIdx = 0; optValIdx < optionValues.length; optValIdx++) {
					System.out.println("\t\t\t - Option Values["
							+ optValIdx + "]: ("
							+ optionValues[optValIdx] + ")");
				}
			}
			else {
				System.out.println("\t\t\tOption Values is null");
			}
			System.out.println();
		}
		System.out.println();
	}
}
