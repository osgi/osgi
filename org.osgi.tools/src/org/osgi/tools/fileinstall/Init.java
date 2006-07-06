package org.osgi.tools.fileinstall;

import java.io.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This clever little bundle watches a directory and will install any jar file
 * if finds in that directory.
 * 
 * We maintain a hashtable of files we found in the load directory where the key
 * is the file name and the value is the date. When we see a change from the
 * directory by polling it every [poll] seconds we update the framework.
 */

public class Init implements BundleActivator, Runnable {
	static ServiceTracker		padmin;
	static ServiceTracker		logTracker;

	BundleContext				context;
	Hashtable					bundles	= new Hashtable();
	File						jardir;
	Thread						thread;
	boolean						cont	= true;
	long						poll	= 2000;
	long						delay	= 2500;
	long						debug;

	public final static String	POLL	= "org.osgi.fileinstall.poll";
	public final static String	DIR		= "org.osgi.fileinstall.dir";
	public final static String	DELAY	= "org.osgi.fileinstall.delay";
	public final static String	DEBUG	= "org.osgi.fileinstall.debug";

	public void start(BundleContext context) throws Exception {
		this.context = context;
		padmin = new ServiceTracker(context, PackageAdmin.class.getName(), null);
		padmin.open();
		poll = getTime(POLL, poll);
		delay = getTime(DELAY, delay);
		debug = getTime(DEBUG, -1);

		String dir = System.getProperty(DIR);
		if (dir == null)
			dir = "./load";

		jardir = new File(dir);
		jardir.mkdirs();
		thread = new Thread(this);
		thread.start();
	}

	long getTime(String property, long dflt) {
		String value = System.getProperty(property);
		if (value != null)
			try {
				return Long.parseLong(value);
			}
			catch (Exception e) {
				System.out.println(property + " set, but not a long: " + value);
			}
		return dflt;
	}

	public void run() {
		System.out.println("FILEINSTALL | OSGi (c) 2006 v2.0");
		System.out.println(POLL + "  (ms)   " + poll);
		System.out.println(DELAY + " (ms)   " + delay);
		System.out.println(DIR + "            " + jardir.getAbsolutePath());
		System.out.println(DEBUG + "          " + debug);
		Map		sizes = new HashMap();

		while (cont)
			try {
				boolean refresh = false;
				Map installed = new HashMap();
				traverse(installed, jardir);
				Bundle bundles[] = context.getBundles();
				for (int i = 0; i < bundles.length; i++) {
					Bundle bundle = bundles[i];
					String location = bundle.getLocation();
					File file = (File) installed.get(location);
					if (file != null) {
						// Modified date does not work on the Nokia
						// for some reason, so we take size into account
						// as well.
						long newSize = file.length();
						Long oldSizeObj = (Long) sizes.get(location);
						long oldSize = oldSizeObj == null ? 0 : oldSizeObj.longValue();
						
						installed.remove(location);
						if (file.lastModified() > bundle.getLastModified()
								+ 4000 && oldSize != newSize ) {
							try {
								sizes.put(location,new Long(newSize));
								InputStream in = new FileInputStream(file);
								bundle.update(in);
								refresh = true;
								in.close();
								log("Updated " + location, null);
							}
							catch (Exception e) {
								log("Failed to update bundle ", e);
							}
						}
						if (!isFragment(bundle))
							try {
								bundle.start();
							}
							catch (Exception e) {
								log("Fail to start bundle " + location, e);
							}
					}
					else {
						if (bundle.getLocation().startsWith(
								jardir.getAbsolutePath())) {
							try {
								bundle.uninstall();
								refresh = true;
								log("Uninstalled " + location, null);
							}
							catch (Exception e) {
								log("failed to uninstall bundle: ", e);
							}
						}
					}
				}

				for (Iterator it = installed.values().iterator(); it.hasNext();) {
					try {
						File file = (File) it.next();
						InputStream in = new FileInputStream(file);
						Bundle bundle = context.installBundle(file
								.getAbsolutePath(), in);
						refresh = true;
						in.close();
						if (!isFragment(bundle))
							bundle.start();
						log("Installed " + file.getAbsolutePath(), null);
					}
					catch (Exception e) {
						log("failed to install/start bundle: ", e);
					}
				}
				if (refresh)
					refresh();
				Thread.sleep(poll);

			}
			catch (Throwable e) {
				e.printStackTrace();
			}
	}

	private void log(String string, Exception e) {
		System.err.println(string + ": " + e);
	}

	private void traverse(Map map, File jardir2) {
		String list[] = jardir.list();
		for (int i = 0; i < list.length; i++) {
			File file = new File(jardir2, list[i]);
			if (list[i].endsWith(".jar")) {
				map.put(file.getAbsolutePath(), file);
			}
			else if (file.isDirectory()) {
				// traverse(map,file);
			}
		}
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("INIT exit");
		cont = false;
		thread.interrupt();
	}

	private boolean isFragment(Bundle bundle) {
		PackageAdmin padmin;
		if (Init.padmin == null)
			return false;

		try {
			padmin = (PackageAdmin) Init.padmin.waitForService(10000);
			if (padmin != null) {
				return padmin.getBundleType(bundle) == PackageAdmin.BUNDLE_TYPE_FRAGMENT;
			}
		}
		catch (InterruptedException e) {
			// stupid exception
		}
		return false;
	}

	private void refresh() {
		PackageAdmin padmin;
		try {
			padmin = (PackageAdmin) Init.padmin.waitForService(10000);
			padmin.refreshPackages(null);
		}
		catch (InterruptedException e) {
			// stupid exception
		}
	}
}
