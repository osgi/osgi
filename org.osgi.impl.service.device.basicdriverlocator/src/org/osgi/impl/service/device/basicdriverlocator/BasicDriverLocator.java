/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.device.basicdriverlocator;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.device.DriverLocator;

/**
 * Basic implementation of a DriverLocator.
 * 
 * Uses a text-based driver database in Properties format. The driver is named
 * <code>driverDB.props</code> and should reside in the bundle jar file.
 * 
 * <p>
 * Database file format:
 * 
 * <pre>
 * 
 *  
 *    count=&lt;number of drivers&gt;
 *    &lt;number&gt;.id=&lt;driver id&gt; 
 *    &lt;number&gt;.category=&lt;driver category&gt;
 *    &lt;number&gt;.product=&lt;driver product&gt;
 *    &lt;number&gt;.vendor=&lt;driver vendor&gt;
 *    &lt;number&gt;.url=&lt;driver url&gt; 
 *    &lt;number&gt;.desc=&lt;driver description&gt;
 *   
 *  
 * </pre>
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 *  
 */
public class BasicDriverLocator implements DriverLocator {
	/** Context service is running in */
	BundleContext	bc				= null;
	String			dbResourceName	= "/org/osgi/impl/service/device/basicdriverlocator/data/driverDB.props";

	BasicDriverLocator(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Implements DriverLocator.
	 * 
	 * Loads driver DB, then calls match()
	 */
	public String[] findDrivers(Dictionary dict) {
		Hashtable drivers = loadDriverDB();
		return match(drivers, dict);
	}

	/**
	 * Implements DriverLocator.
	 * 
	 * Load a driver bundle using URL info.
	 * 
	 * @return Stream to driver bundle, null if no driver is found.
	 */
	public InputStream loadDriver(String id) throws IOException {
		Hashtable drivers = loadDriverDB();
		DriverInfo di = (DriverInfo) drivers.get(id);
		InputStream stream = null;
		try {
			URL url = new URL(di.url);
			stream = url.openStream();
		}
		catch (MalformedURLException e) {
			Log.error("bad URL for in " + di, e);
			throw new IOException("Bad driver URL " + e);
		}
		catch (IOException e) {
			Log.error("can't connect to URL in " + di, e);
			// rethrow exception
			throw e;
		}
		return stream;
	}

	/**
	 * Load driver database.
	 * 
	 * @return Hashtable String (driver id) -> DriverInfo
	 */
	Hashtable loadDriverDB() {
		Hashtable d = new Hashtable();
		InputStream instream = null;
		try {
			String dbURL = (String) System
					.getProperty("org.osgi.service.basicdriverlocator.dburl");
			// If property exists, try to open URL, otherwise use internal
			// props.
			if (dbURL != null && !dbURL.equals("")) {
				URL url = new URL(dbURL);
				instream = url.openStream();
			}
			else {
				instream = getClass().getResourceAsStream(dbResourceName);
			}
			Properties db = new Properties();
			db.load(instream);
			int nCount = Integer.parseInt((String) db.get("count"));
			for (int i = 0; i < nCount; i++) {
				DriverInfo di = new DriverInfo(db, i);
				d.put(di.id, di);
			}
		}
		catch (Exception e) {
			Log.error("Can't load driverDB: ", e);
		}
		finally {
			try {
				instream.close();
			}
			catch (IOException e2) {
				// silently ignore
			}
		}
		return d;
	}

	/**
	 * Check all Drivers in drivers for match agains props.
	 * 
	 * @param drivers String (driver ID) -> DriverInfo
	 * @param props Device properties to match
	 * @return String array of driver IDs
	 */
	String[] match(Hashtable drivers, Dictionary dict) {
		Vector r = new Vector();
		for (Enumeration e = drivers.elements(); e.hasMoreElements();) {
			DriverInfo di = (DriverInfo) e.nextElement();
			if (di.match(dict)) {
				r.addElement(di.id);
			}
		}
		// Create string array, since API says so
		String[] rl = new String[r.size()];
		r.copyInto(rl);
		return rl;
	}
}
/**
 * Utility class for holding driver database
 */

class DriverInfo {
	String	url;
	String	id;
	String	category;
	String	vendor;
	String	product;
	String	desc;

	/**
	 * Create from Properties.
	 * 
	 * Values of the form <code>number</code> .key are get() from props to
	 * build the DriverInfo.
	 * 
	 * @param p Properties containing driver info
	 * @param number count number inte named properties
	 */
	DriverInfo(Properties p, int number) {
		id = (String) p.get("" + number + ".id");
		url = (String) p.get("" + number + ".url");
		desc = (String) p.get("" + number + ".desc");
		vendor = (String) p.get("" + number + ".vendor");
		product = (String) p.get("" + number + ".product");
		category = (String) p.get("" + number + ".category");
	}

	/**
	 * Matching method used by locator.
	 * 
	 * @return true if DriverInfo matches props, false otherwise
	 */
	boolean match(Dictionary props) {
		boolean b = cmp(category, (String) props.get("category"))
				&& cmp(product, (String) props.get("product"))
				&& cmp(vendor, (String) props.get("vendor"));
		Log.debug("probing " + toString() + " resulted in " + b);
		return b;
	}

	/**
	 * Compare method which allows empty strings and simple wildcards.
	 * 
	 * null, "" and "*" are match-all wildcards.
	 * 
	 * @param s1 string to compare to s2
	 * @param s2 string to compare to s1
	 * @return true id s1 compares nicely to s2
	 */
	boolean cmp(String s1, String s2) {
		return s1 == null || s2 == null || s1.equals("") || s2.equals("")
				|| s1.equals("*") || s2.equals("*") || s1.equals(s2);
	}

	/**
	 * Convert to string.
	 * 
	 * @return Human-readable string representation
	 */
	public String toString() {
		return "id=" + id + ", url=" + url + ", desc=" + desc + ", product="
				+ product + ", vendor=" + vendor;
	}
}
