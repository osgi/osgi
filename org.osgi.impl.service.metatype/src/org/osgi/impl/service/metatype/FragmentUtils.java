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

package org.osgi.impl.service.metatype;

import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import java.util.Enumeration;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;

/*
 * Fragment Utilities
 */
public class FragmentUtils {
	static PackageAdmin packageAdmin;

	/*
	 * 
	 */
	public static boolean isFragment(Bundle bundle) {

		if (packageAdmin == null)
			return false;
		return (packageAdmin.getBundleType(bundle) & PackageAdmin.BUNDLE_TYPE_FRAGMENT) != 0;
	}

	/*
	 * Find all the paths to entries for the bundle and its fragments.
	 * Returned data is got by Bundle.getEntryPaths().
	 */
	public static Enumeration findEntryPaths(Bundle bundle, String path) {

		Bundle[] fragments = packageAdmin == null ? null : packageAdmin.getFragments(bundle);
		Vector result = new Vector();
		addEntryPaths(bundle.getEntryPaths(path), result);
		if (fragments != null) {
			for (int i = 0; i < fragments.length; i++)
				addEntryPaths(fragments[i].getEntryPaths(path), result);
		}
		return result.size() == 0 ? null : result.elements();
	}

	/*
	 * Internal method - add an path to vector, and check for duplucate.
	 */
	private static void addEntryPaths(Enumeration ePaths, Vector pathList) {

		if (ePaths == null)
			return;
		while (ePaths.hasMoreElements()) {
			Object path = ePaths.nextElement();
			if (!pathList.contains(path))
				pathList.add(path);
		}
	}

	/*
	 * Find all the URLs to entries for the bundle and its fragments.
	 * Returned data is got by Bundle.getEntry().
	 */
	public static URL[] findEntries(Bundle bundle, String path) {

		Bundle[] fragments = packageAdmin == null ? null : packageAdmin.getFragments(bundle);
		URL url = bundle.getEntry(path);
		if (fragments == null || fragments.length == 0)
			return url == null ? null : new URL[] {url};
		ArrayList result = new ArrayList();
		if (url != null)
			result.add(url);

		for (int i = 0; i < fragments.length; i++) {
			URL fragUrl = fragments[i].getEntry(path);
			if (fragUrl != null)
				result.add(fragUrl);
		}
		return result.size() == 0 ? null : (URL[]) result.toArray(new URL[result.size()]);
	}
}
