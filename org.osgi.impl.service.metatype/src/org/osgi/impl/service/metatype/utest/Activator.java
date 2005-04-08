/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
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

/**
 * The unit test bundle for MetaType Service. 
 * 
 * @author	Julian Chen
 * @version 1.0
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
	private void showMetaData() {

		ServiceTracker tracker = new ServiceTracker(_context,
				MetaTypeService.class.getName(), null);

		tracker.open();
		MetaTypeService mts = (MetaTypeService) tracker.getService();
		if (mts == null) {
			System.out.println("No MetaTypeService");
			return;
		}
		
		Bundle[] bs = _context.getBundles();
		if (bs != null) {
			System.out.println("There are total " + bs.length + " bundles.");
			System.out.println();

			for (int bIdx = 0; bIdx < bs.length; bIdx++) {
				MetaTypeInformation mti = mts.getMetaTypeInformation(bs[bIdx]);
				String[] pids  = mti.getPids();
				String[] fpids = mti.getFactoryPids();
				if ((pids != null ? pids.length : 0)
						+ (fpids != null ? fpids.length : 0) > 0) {
					System.out.println("Bundle(" + bs[bIdx].getBundleId()
							+ ", " + bs[bIdx].getSymbolicName()
							+ ") has Meta data.");

					String[] locales = mti.getLocales();
					if (locales == null || locales.length == 0) {
						System.out.println("There is no Locale available.");
					}
					else {
						System.out.println("There is " + locales.length
								+ " Locale(s) available.");
						for (int lIdx = 0; lIdx < locales.length; lIdx++) {
							System.out.print("\"" + locales[lIdx] + "\" ");
						}
						System.out.println();
					}
				}

				for (int pidIdx = 0; pidIdx < pids.length; pidIdx++) {
					try {
						System.out.println("\tThis is PID: (" + pids[pidIdx] 
								+ ")");
						ObjectClassDefinition ocd = mti.getObjectClassDefinition(
								pids[pidIdx], "en_US");
						showOCD(ocd);
					}
					catch (Exception e) {
						System.out.println("Exception when getting OCD data.");
						e.printStackTrace();
					}
				}

				for (int fpidIdx = 0; fpidIdx < fpids.length; fpidIdx++) {
					try {
						System.out.println("\tThis is FPID: (" + fpids[fpidIdx]
								+ ")");
						ObjectClassDefinition ocd = mti.getObjectClassDefinition(
								fpids[fpidIdx], "en_US");
						showOCD(ocd);
					}
					catch (Exception e) {
						System.out.println("Exception when getting OCD data.");
						e.printStackTrace();
					}
				}
			}
		}
		tracker.close();
	}

	/*
	 * 
	 */
	private void showOCD(ObjectClassDefinition ocd) {

		System.out.println("\tOCD name: \"" + ocd.getName() + "\"");
		System.out.println("\tOCD id: \"" + ocd.getID() + "\"");
		System.out.println("\tOCD description: \""
				+ ocd.getDescription() + "\"");
		try {
			java.io.InputStream is = ocd.getIcon(16);
			if (is == null) {
				System.out.println("\tOCD icon: NULL");
			}
			else {
				byte[] bb = new byte[is.available()];
				int iconSize = is.read(bb);
				if (iconSize > 0) {
					System.out.println("\t\tOCD icon header: ["
							+ new String(bb).substring(0,
									iconSize > 10 ? 10 : iconSize) + "]");
				}
				else
					System.out.println("\tOCD icon: Empty");
			}
		} catch (Exception e) {
			System.out.println("Exception when getting Icon data.");
			e.printStackTrace();
		}

		AttributeDefinition[] ADs =
			ocd.getAttributeDefinitions(ObjectClassDefinition.REQUIRED);

		System.out.println("\tRequired ADs:");
		for (int adIdx = ADs.length-1; adIdx >= 0; adIdx--) {
			// Show in inversed order.
			showAD(ADs[adIdx], ADs.length-1-adIdx);
			System.out.println();
		}
		System.out.println();

		ADs = ocd.getAttributeDefinitions(ObjectClassDefinition.OPTIONAL);
		System.out.println("\tOptional ADs:");
		for (int adIdx = ADs.length-1; adIdx >= 0; adIdx--) {
			// Show in inversed order.
			showAD(ADs[adIdx], ADs.length-1-adIdx);
			System.out.println();
		}
		System.out.println();
	}

	/*
	 * 
	 */
	private void showAD(AttributeDefinition ad, int adIdx) {

		System.out.println("\t\tAD[" + adIdx + "] name: \""
				+ ad.getName() + "\"");
		System.out.println("\t\tAD[" + adIdx + "] required: \"true\"");
		System.out.println("\t\tAD[" + adIdx + "] id: \"" + ad.getID() + "\"");
		System.out.println("\t\tAD[" + adIdx + "] type: \"" 
				+ getString(ad.getType()) + "\"");
		System.out.println("\t\tAD[" + adIdx + "] cardinality: \""
				+ ad.getCardinality() + "\"");
		System.out.println("\t\tAD[" + adIdx + "] description: \""
				+ ad.getDescription() + "\"");

		String[] defaultValues = ad.getDefaultValue();
		if (defaultValues != null) {
			System.out.println("\t\t\tThere are total " + defaultValues.length
					+ " default value(s).");
			for (int dfValIdx = 0; dfValIdx < defaultValues.length; dfValIdx++) {
				System.out.println("\t\t\t - Default Value[" + dfValIdx
						+ "]: \"" + defaultValues[dfValIdx] + "\"");
			}
		}
		else {
			System.out.println("\t\t\tThere is no default value.");
		}

		int min = 0;
		String[] optionLabels = ad.getOptionLabels();
		if (optionLabels != null) {
			System.out.println("\t\t\tThere are total " + optionLabels.length
					+ " option label(s).");
			min = optionLabels.length;
		}
		else {
			System.out.println("\t\t\tOption Labels is NULL.");
		}

		String[] optionValues = ad.getOptionValues();
		if (optionValues != null) {
			System.out.println("\t\t\tThere are total "	+ optionValues.length
					+ " option values.");

			if (min > optionValues.length) {
				min = optionValues.length;
			}
		}
		else {
			System.out.println("\t\t\tOption Values is NULL.");
		}

		for (int optIdx = 0; optIdx < min; optIdx++) {
			System.out.println("\t\t\t - Option[" + optIdx
					+ "]: Label / Value = \"" + optionLabels[optIdx]
					+ "\" / \"" + optionValues[optIdx] + "\"");
		}
	}

	/*
	 * 
	 */
	private String getString(int idx) {

		String res = null;
		switch (idx) {
			case AttributeDefinition.STRING :
				res = new String("STRING");
				break;
			case AttributeDefinition.LONG :
				res = new String("LONG");
				break;
			case AttributeDefinition.INTEGER :
				res = new String("INTEGER");
				break;
			case AttributeDefinition.SHORT :
				res = new String("SHORT");
				break;
			case AttributeDefinition.CHARACTER :
				res = new String("CHARACTER");
				break;
			case AttributeDefinition.BYTE :
				res = new String("BYTE");
				break;
			case AttributeDefinition.DOUBLE :
				res = new String("DOUBLE");
				break;
			case AttributeDefinition.FLOAT :
				res = new String("FLOAT");
				break;
			case AttributeDefinition.BIGINTEGER :
				res = new String("BIGINTEGER");
				break;
			case AttributeDefinition.BIGDECIMAL :
				res = new String("BIGDECIMAL");
				break;
			case AttributeDefinition.BOOLEAN :
				res = new String("BOOLEAN");
				break;
			default :
				res = new String("Unknown Data Type");
		}
		return res;
	}
}
