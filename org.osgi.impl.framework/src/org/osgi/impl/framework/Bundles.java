/**
 * Copyright (c) 1999 - 2002 Gatespace AB. All Rights Reserved.
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

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;

/**
 * Here we handle all the bundles that are installed in framework.
 * Also handles load and save of bundle states to file, so that we
 * can restart the platform.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Bundles
{
	/**
	 * Version of data format saved by this class.
	 */
	private final static int bundleInfoVersion = 1;
	
	/**
	 * Minimum version of data format saved data required for this
	 * class to be able to read data.
	 */
	private final static int minBundleInfoVersion = 1;
	
	/**
	 * Filename of stored bundle property data.
	 */
	private final static String TOP_FILE = "bundle_info";
	
	/**
	 * Default bundle start level.
	 */
	private final static int DEFAULT_BUNDLE_START_LEVEL = 1;
	
	/**
	 * Name tag for stored or loaded bundle property data.
	 */
	private String name = "new";
	
	/**
	 * Version of stored or loaded bundle property data.
	 */
	private String version = Integer.toString(bundleInfoVersion);
	
	/**
	 * Comment of stored or loaded bundle property data.
	 */
	private String comment = "Bundle info";
	
	/**
	 * Table of all installed bundles in this framework.
	 * Key is bundle location.
	 */
	private HashMap /* String -> BundleImpl */ bundles = new HashMap();
	
	/**
	 * Bundle identifier we will give to next installed bundle.
	 */
	private long nextFreeId = 1;
	
	/**
	 * Link to framework object.
	 */
	private Framework framework;
	
	/**
	 * Top directory for storing all information about bundles and there
	 * persistent storage area.
	 */
	private File bundlesDir;
	
	/**
	 * The initial bundle start level. Contained in this class because
	 * we want to use bundle_info for persistent storage.
	 */
	private int initialBundleStartLevel = DEFAULT_BUNDLE_START_LEVEL;    
	
	/**
	 * Create a container for all bundles in this framework.
	 */
	Bundles(Framework fw, File dir)
	{
		framework = fw;
		bundlesDir = dir;
		bundles.put(fw.systemBundle.getLocation(), fw.systemBundle);
	}
	
	
	/**
	 * Install a new bundle.
	 *
	 * @param location The location to be installed
	 */
	BundleImpl install(final String location, final InputStream in) throws BundleException
	{
		BundleImpl b;
		synchronized (this) {
			b = (BundleImpl) bundles.get(location);
			if (b != null) {
				return b;
			}
			try {
				b = (BundleImpl) AccessController.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							BundleImpl res = null;
							InputStream bin;
							if (in == null) {
								bin = (new URL(location)).openConnection().getInputStream();
							} else {
								bin = in;
							}
							try {
								res = new BundleImpl(bundlesDir, framework, location, bin);
							} finally {
								bin.close();
							}
							bundles.put(location, res);
							save();
							return res;
						}
					});
			} catch (PrivilegedActionException e) {
				throw new BundleException("Failed to install bundle", e.getException());
			} catch (Exception e) {
				throw new BundleException("Failed to install bundle", e);
			}
		}
		framework.listeners.bundleChanged(new BundleEvent(BundleEvent.INSTALLED, b));
		return b;
	}
	
	
	/**
	 * Remove bundle registration.
	 *
	 * @param location The location to be removed
	 */
	synchronized void remove(String location)
	{
		bundles.remove(location);
	}
	
	
	/**
	 * Get bundle that has specified bundle identifier.
	 *
	 * @param id The identifier of bundle to get.
	 * @return BundleImpl representing bundle or null
	 *         if bundle was not found.
	 */
	synchronized BundleImpl getBundle(long id)
	{
		for (Iterator e = bundles.values().iterator(); e.hasNext();) {
			BundleImpl b = (BundleImpl)e.next();
			if (b.id == id) {
				return b;
			}
		}
		return null;
	}
	
	
	/**
	 * Get all installed bundles.
	 *
	 * @return A Bundle array with bundles.
	 */
	synchronized BundleImpl[] getBundles()
	{
		BundleImpl [] result = new BundleImpl[bundles.size()];
		bundles.values().toArray(result);
		return result;
	}
	
	
	/**
	 * Get all bundles currently in bundle state ACTIVE.
	 *
	 * @return A Bundle array with bundles.
	 */
	synchronized List getRunningBundles()
	{
		ArrayList slist = new ArrayList();
		for (Iterator e = bundles.values().iterator(); e.hasNext();) {
			BundleImpl b = (BundleImpl)e.next();
			if (b.id != 0 && b.state == Bundle.ACTIVE) {
				slist.add(b);
			}
		}
		return slist;
	}
	
	/**
	 * Returns all started bundles on a given start level.
	 *
	 * @param startlevel The start level.
	 * @return A List of bundles.
	 */
	synchronized List getStartBundles(int startlevel)
	{
		ArrayList slist = new ArrayList();
		for (Iterator e = bundles.values().iterator(); e.hasNext();) {
			BundleImpl b = (BundleImpl)e.next();
			if (b.startLevel == startlevel && b.isPersistentlyStarted) {
				slist.add(b);
			}
		}
		return slist;
	}
	
	/**
	 * Returns all active bundles on a given start level.
	 *
	 * @param startlevel The start level.
	 * @return A List of bundles.
	 */
	synchronized List getStopBundles(int startlevel)
	{
		ArrayList slist = new ArrayList();
		for (Iterator e = bundles.values().iterator(); e.hasNext();) {
			BundleImpl b = (BundleImpl)e.next();
			if (b.startLevel == startlevel && b.state == Bundle.ACTIVE) {
				slist.add(b);
			}
		}
		return slist;
	}
	
	
	/**
	 * Stop a list of bundles in correct order
	 *
	 * @param bundles Bundles to stop.
	 */
	synchronized void stopBundles(List bundles)
	{
		// Sort in start order
		Collections.sort(bundles, Collections.reverseOrder());
		for (Iterator i = bundles.iterator(); i.hasNext();) {
			BundleImpl b = (BundleImpl)i.next();
			if (b.state == Bundle.ACTIVE) {
				try {
					b.stop();
				} catch (BundleException be) {
					b.framework.listeners.frameworkError(b, be);
				}
			}
		}    
	}
	
	/**
	 * Stop bundles on a given start level in correct order.
	 *
	 * @param startlevel The start level.
	 */
	synchronized void stopBundles(int startlevel)
	{
		List slist = getStopBundles(startlevel);
		Collections.sort(slist, Collections.reverseOrder());
		for (Iterator i = slist.iterator(); i.hasNext();) {
			BundleImpl b = (BundleImpl)i.next();
			if (b.state == Bundle.ACTIVE) {
				try {
					b.stop();
				} catch (BundleException be) {
					b.framework.listeners.frameworkError(b, be);
				}
			}
		}    
	}
	
	/**
	 * Try to load any saved framework state.
	 * This is done by installing all saved bundle from the local archive
	 * copy. And restoring the saved state for each bundle. This is only
	 * intended to be executed during start of framework.
	 *
	 * @return True if we found a saved state.
	 * @exception Exception If we failed to read data or if data
	 *            is corrupted.
	 */
	synchronized boolean load() throws Exception
	{
		File topFile = new File(bundlesDir, TOP_FILE);
		if (topFile.exists()) {
			FileInputStream fs = new FileInputStream(topFile);
			try {
				BufferedInputStream bs = new BufferedInputStream(fs);
				load(bs);
			} finally {
				fs.close();
			}
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Save the state of all bundles.
	 */
	synchronized void save()
	{
		
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public Object run() throws Exception {
						File topFile = new File(bundlesDir, TOP_FILE);
						FileOutputStream fs = new FileOutputStream(topFile);
						try {
							BufferedOutputStream bs = new BufferedOutputStream(fs);
							save(bs);
							bs.flush();
						} finally {
							fs.close();
						}
						return null;
					}
				}
			);
			} catch (PrivilegedActionException e) {
				framework.listeners.frameworkEvent(new FrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e.getException() ));
			}
		}
		
		
		/**
		 * Retrieve the next unused bundle identifier.
		 *
		 * @return The identifier
		 */
		synchronized long getNewId()
		{
			return nextFreeId++;
		}
		
		
		
		/**
		 * Check if a bundle identifier is already in use.
		 *
		 * @param id The bundle id to be checked
		 * @return True if bundle identifier is in use
		 */
		synchronized boolean checkIllegalId(long id)
		{
			// 0 is reserved for internal use.
			if (id == 0) {
				return true;
			}
			return getBundle(id) != null;
		}
		
		
		/**
		 * Check if a bundle location is already in use.
		 *
		 * @param location The location to be checked
		 * @return True if bundle location is in use
		 */
		synchronized boolean checkIllegalLocation(String location)
		{
			if (location != null) {
				return bundles.containsKey(location);
			} else {
				return true;
			}
		}
		
		
		/**
		 * Start a list of bundles in correct order
		 *
		 * @param bl List of bundles to start.
		 */
		void startBundles(List bl)
		{
			// Sort in start order
			Collections.sort(bl);
			for (Iterator i = bl.iterator(); i.hasNext();) {
				BundleImpl rb = (BundleImpl)i.next();
				if (rb.getUpdatedState() == Bundle.RESOLVED) {
					try {
						rb.start();
					} catch (BundleException be) {
						rb.framework.listeners.frameworkError(rb, be);
					}
				}
			}    
		}
		
		/**
		 * Start bundles on a given start level in correct order.
		 *
		 * @param startlevel The start level.
		 */
		synchronized void startBundles(int startlevel)
		{
			List slist = getStartBundles(startlevel);
			Collections.sort(slist);
			for (Iterator i = slist.iterator(); i.hasNext();) {
				BundleImpl rb = (BundleImpl)i.next();
				if (rb.getUpdatedState() == Bundle.RESOLVED) {
					try {
						rb.start();
					} catch (BundleException be) {
						rb.framework.listeners.frameworkError(rb, be);
					}
				}
			}    
		}
		
		/**
		 * Set initial (i.e. default) bundle start level
		 *
		 * @param startlevel The initial bundle start level.
		 */
		synchronized void setInitialBundleStartLevel(int startlevel)
		{
			initialBundleStartLevel = startlevel;
			save();
		}
		
		/**
		 * Get initial (i.e. default) bundle start level
		 *
		 * @return The initial bundle start level.
		 */
		synchronized int getInitialBundleStartLevel()
		{
			return initialBundleStartLevel;
		}
		
		
		//
		// Private methods
		//
		
		/**
		 * Save the state of all bundles using properties to an outputstream.
		 *
		 * @param props OutputStream to save to.
		 */
		private void save(OutputStream props) throws IOException
		{
			Properties bi_props = new Properties();
			int i = 0;
			
			for (Iterator e = bundles.values().iterator(); e.hasNext(); ) {
				BundleImpl b = (BundleImpl)e.next();
				// Don't save system bundle or UNINSTALLED bundles
				if (b.id != 0 && b.getState() != Bundle.UNINSTALLED) {
					b.toProperties(bi_props);
				}
			}
			
			bi_props.put("bundleinfo.format", String.valueOf(bundleInfoVersion));
			bi_props.put("bundleinfo.name", name);
			bi_props.put("bundleinfo.comment", comment);
			bi_props.put("bundleinfo.initialbundlestartlevel", String.valueOf(initialBundleStartLevel));
			bi_props.store(props, "Bundle Info");
		}
		
		
		/**
		 * Restore all bundles based on the previously saved properties data.
		 *
		 * @param props InputStream of in properties format.
		 * @exception Exception If we failed to read data or if data
		 *            is corrupted.
		 */
		private void load(InputStream props) throws Exception
		{
			int n_cells, n_libs, n_groups, n_accesses;
			Properties bi_props = new Properties();
			bi_props.load(props);
			
			int ver;
			try {
				ver = Integer.parseInt(bi_props.getProperty("bundleinfo.format"));
				bi_props.remove("bundleinfo.format");
			} catch (NumberFormatException e) {
				throw new IOException("Parse error, bundleinfo format version");
			}
			if (ver < minBundleInfoVersion || ver > bundleInfoVersion) {
				throw new IOException("Unsupported bundleinfo format version");
			}
			
			name = bi_props.getProperty("bundleinfo.name","");
			bi_props.remove("bundleinfo.name");
			comment = bi_props.getProperty("bundleinfo.comment","");
			bi_props.remove("bundleinfo.comment");
			
			try {
				initialBundleStartLevel = Integer.parseInt(bi_props.getProperty("bundleinfo.initialbundlestartlevel", Integer.toString(DEFAULT_BUNDLE_START_LEVEL)));
				bi_props.remove("bundleinfo.initialbundlestartlevel");
			} catch (NumberFormatException e) {
				initialBundleStartLevel = DEFAULT_BUNDLE_START_LEVEL;
			}
			
			while (!bi_props.isEmpty()) {
				String p = (String)bi_props.propertyNames().nextElement(); 
				BundleImpl b = new BundleImpl(bundlesDir, framework, p, bi_props);
				if (b.id >= nextFreeId) {
					nextFreeId = b.id + 1;
				}
				bundles.put(b.location, b);
				// Only called at contruction of Framework, no bundle install event needed.
			}
		}
		
	}
