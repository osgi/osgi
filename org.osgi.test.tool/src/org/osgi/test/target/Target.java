/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (c) Copyright Ericsson Radio Systems AB. 2000.
 * Parts of this source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.target;

import java.io.*;
import java.net.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.service.TestCaseLink;
import org.osgi.test.shared.*;

/**
 * Bundle activator for target.
 * 
 * This activator is loaded in the target framework. It has a dual function.
 * First, it will broadcast a message on the local net to make the director
 * aware of local targets. Second, it will listen to incoming connections for
 * application control. This will allow the director to download bundles.
 * <p>
 * Downloaded bundles are managed by this bundle. If the connection is closed or
 * lost, this bundle will uninstall all installed bundles.
 * <p>
 * If a second connection comes in, the first connection will be closed and
 * bundles will be uninstalled.
 * <p>
 * When a bundle is installed via us, it should get a TestCaseLink service. We
 * are providing a factory with this. If this service is obtained, we know the
 * bundle is active. If the bundle is tired and wants to go, it should unget
 * this service. We will then uninstall the bundle and inform the director.
 */
public class Target extends Thread implements BundleActivator, ServiceFactory,
		ITarget {
	ServiceRegistration		registration;
	BundleContext			context;
	ServerSocket			listener;
	Vector					links			= new Vector();
	boolean					cont;
	TargetLink				run;
	boolean					open			= false;
	Bundle					snapshot[];
	SimplePermissionPolicy	permission;
	char					ok[]			= {'|', '/', '-', '\\'};
	char					problems[]		= {'?', '!', '\u00BF', '\u00A1'};
	char					status[]		= problems;
	int						index;
	String					permissionSnapshot[];
	Thread					watchdog;
	int						MAX_TICKS		= 10;
	int						WATCHDOG_PERIOD	= 30000;
	int						ticks;

	public Target() {
		super("Target");
	}

	/**
	 * Start the target system.
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		cont = true;
		permission = new SimplePermissionPolicy(context);
		registration = context.registerService(TestCaseLink.class.getName(),
				this, null);
		start();
		String period = getProperty("org.osgi.test.watchdog", null);
		if (period != null) {
			try {
				MAX_TICKS = Integer.parseInt(period);
				watchdog = new Thread("Watchdog") {
					public void run() {
						try {
							while (true) {
								Thread.sleep(WATCHDOG_PERIOD);
								if (ticks++ > MAX_TICKS) {
									System.err
											.println("Watchdog is killing this process do to inactivity for "
													+ (MAX_TICKS
															* WATCHDOG_PERIOD / 60000)
													+ " minutes");
									System.err
											.println("(This option is controlled through the org.osgi.test.watchdog System property)");
									System.exit(-1);
								}
							}
						}
						catch (InterruptedException e) {
							return;
						}
					}
				};
				watchdog.setDaemon(true);
				watchdog.start();
			}
			catch (NumberFormatException e) {
				// too bad, no watchdog
				System.err
						.println("Watchdog requested, but value was not an integer "
								+ period);
			}
		}
	}

	/**
	 * Stop the target system.
	 */
	public void stop(BundleContext context) {
		cont = false;
		try {
			listener.close();
		}
		catch (Exception e) {
		}
	}

	/**
	 * Close the connection to the director.
	 * 
	 * Clean up any installed bundles, close the link (if not done yet) and
	 * reinitialize.
	 */
	public void linkClosed() {
		if (open) {
			open = false;
			run = null;
			// Assure we have the right permissions again
			permission.setPermissions(context.getBundle());
			for (Enumeration e = links.elements(); e.hasMoreElements();) {
				TestCaseLinkImpl control = (TestCaseLinkImpl) e.nextElement();
				control.close();
			}
			Hashtable old = new Hashtable();
			for (int i = 0; snapshot != null && i < snapshot.length; i++)
				old.put(snapshot[i], snapshot[i]);
			links = new Vector();
			Bundle now[] = context.getBundles();
			for (int i = 0; now != null && i < now.length; i++) {
				if (old.get(now[i]) == null) {
					try {
						if (now[i].getLocation().indexOf("~keep~") < 0) {
							now[i].uninstall();
							log("Removing newly installed bundle " + now[i],
									null);
						}
					}
					catch (BundleException e) {
						log("Cannot uninstall " + now[i].getLocation(), e);
					}
				}
			}
			PackageAdmin pack = getPackageAdmin();
			if (pack != null)
				pack.refreshPackages(null);
			PermissionAdmin perm = getPermissionAdmin();
			if (perm != null) {
				Hashtable ht = new Hashtable();
				for (int i = 0; permissionSnapshot != null
						&& i < permissionSnapshot.length; i++)
					ht.put(permissionSnapshot[i], "");
				String[] newer = perm.getLocations();
				for (int i = 0; newer != null && i < newer.length; i++) {
					if (now != null
							&& now[i].getLocation().indexOf("~keep~") < 0) {
						if (ht.get(newer[i]) == null)
							perm.setPermissions(newer[i], null);
					}
				}
			}
		}
	}

	/**
	 * Implementation of factory method.
	 * 
	 * We have a prefabricated service for each installed bundle and return
	 * this. If this method is called by bundles that were not installed by us,
	 * then we return null.
	 * 
	 * @param bundle bundle calling us
	 * @param registration registration of this service
	 * @returns private link to director bundle or null
	 */
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		TestCaseLinkImpl control = getTestCaseLink(bundle);
		return control;
	}

	/**
	 * Implementation of factory unget method.
	 * 
	 * When a testbundle gives up its service, we assume it is ready and warn
	 * the director about it.
	 */
	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object trojan) {
		try {
			TestCaseLinkImpl control = getTestCaseLink(bundle);
			TargetLink run = this.run;
			if (run != null)
				run.stopped(control.getName());
			else
				log(
						"TargetLink already close for ungetservice "
								+ registration, null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main control loop for target.
	 * 
	 * We mainly listen to incoming connections. However, we timeout every now
	 * and then to broadcast an announcement on the net of our existence.
	 */
	public void run() {
		int preferredSocket = 3191;
		while (cont)
			try {
				// Give the user a chance to set the port used.
				String port = getProperty("org.osgi.test.target.port", "3191");
				preferredSocket = Integer.parseInt(port);
				listener = new ServerSocket(preferredSocket);
				listener.setSoTimeout(2000);
				String msg = "application=org.osgi.test"
						+ " host="
						+ InetAddress.getLocalHost().getHostAddress()
						+ " port="
						+ listener.getLocalPort()
						+ " comment="
						+ (getProperty("org.osgi.framework.vendor", null)
								+ " "
								+ getProperty("org.osgi.framework.version",
										null) + " "
								+ getProperty("java.vendor", null) + " " + getProperty(
								"java.version", null)).replace(' ', '_');
				String ipaddr = getProperty("org.osgi.test.broadcast",
						"255.255.255.255");
				DatagramSocket announce = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg
						.getBytes().length, InetAddress.getByName(ipaddr), 2001);
				// TODO check if unconnected
				try {
					announce.send(packet);
				}
				catch (Exception e) {
					log("Cannot send package, deferring to localhost", e);
					packet = new DatagramPacket(msg.getBytes(),
							msg.getBytes().length, InetAddress
									.getByName("127.0.0.1"), 2001);
					try {
						announce.send(packet);
					}
					catch (Exception ee) {
					}
				}
				permission.setPermissions(context.getBundle());
				while (cont) {
					try {
						if (getProperty("org.osgi.test.seewait", null) != null)
							System.out.print(status[index++ % status.length]
									+ "\r");
						Socket socket = listener.accept();
						if (run != null) {
							log("Killing active run due to new request", null);
							run.close();
							linkClosed();
						}
						log("Accepting connection from "
								+ socket.getInetAddress(), null);
						snapshot = context.getBundles();
						PermissionAdmin pa = getPermissionAdmin();
						if (pa != null)
							permissionSnapshot = pa.getLocations();
						try {
							ticks = 0;
							run = new TargetLink(this);
							open = true;
							run.open(socket);
							setProperties();
						}
						catch (Throwable e) {
							log("Error in socket initialization", e);
							run.close();
							linkClosed();
						}
					}
					catch (InterruptedIOException iioe) {
						try {
							announce.send(packet);
							status = ok;
						}
						catch (Exception ioe) {
							status = problems;
						}
					}
					catch (IOException ioe) {
						if (cont)
							log("Problems reading socket.", ioe);
					}
				}
			}
			catch (java.net.BindException e) {
				System.out
						.println("There is likely another target already running; port "
								+ preferredSocket + " is already bound");
			}
			catch (Throwable ee) {
				log("Initialization target service ", ee);
				ee.printStackTrace();
				if (cont)
					try {
						Thread.sleep(5000);
					}
					catch (Exception e) {
					}
			}
			finally {
				try {
					listener.close();
					Socket s = new Socket("localhost", preferredSocket);
					s.close();
				}
				catch (Exception e) {
					log("Cannot cleanup",e);
				}
				if (run != null)
					run.close();
				try {
					Thread.sleep(5000);
				}
				catch (Exception ee) {
				}
			}
		log("Leaving target", null);
	}

	/**
	 * Get the permission admin.
	 */
	PermissionAdmin getPermissionAdmin() {
		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("No PermissionAdmin");
			return null;
		}
		return (PermissionAdmin) context.getService(ref);
	}

	/**
	 * Get the package admin.
	 */
	PackageAdmin getPackageAdmin() {
		ServiceReference ref = context.getServiceReference(PackageAdmin.class
				.getName());
		if (ref == null) {
			System.out.println("No PackageAdmin");
			return null;
		}
		return (PackageAdmin) context.getService(ref);
	}

	/**
	 * Request from the director to install a new bundle.
	 * 
	 * @param name name of the bundle
	 * @param in Inputstream with bundle
	 */
	public void install(String name, InputStream in) throws Exception {
		ticks = 0; // for watchdog
		try {
			log("Installing " + name, null);
			Bundle bundle = find(name);
			if (bundle == null) {
				bundle = context.installBundle(name, in);
			}
			else {
				bundle.update(in);
			}
			in.close();
			permission.setPermissions(bundle);
			TestCaseLinkImpl control = new TestCaseLinkImpl(run, bundle, name);
			links.addElement(control);
			bundle.start();
		}
		catch (BundleException e) {
			log("Cannnot install " + name, e);
			throw e;
		}
		catch (Throwable e) {
			log("While install " + name, e);
			throw new IOException("Failed to install " + e);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private Bundle find(String name) {
		Bundle b[] = context.getBundles();
		for (int i = 0; i < b.length; i++)
			if (b[i].getLocation().equals(name))
				return b[i];
		return null;
	}

	/**
	 * Request from the director to uninstall an installed bundle.
	 * 
	 * @param name name of the bundle.
	 */
	public void uninstall(String name) throws Exception {
		log("Uninstalling " + name, null);
		TestCaseLinkImpl control = getTestCaseLink(name);
		if (control != null) {
			control.getBundle().uninstall();
			links.removeElement(control);
		}
	}

	/**
	 * Push an object to the test bundle on this target coming from the
	 * testcase.
	 * 
	 * @param name name of the bundle
	 * @param msg payload
	 */
	public void push(String name, Object msg) {
		ticks = 0; // for watchdog
		TestCaseLinkImpl control = getTestCaseLink(name);
		control.push(msg);
	}

	/**
	 * Convenience method to find the corresponding test case link via its name.
	 * 
	 * @param name name of the bundle
	 * @returns the corresponding testcaselink
	 */
	TestCaseLinkImpl getTestCaseLink(String name) {
		if (name == null)
			return null;
		for (Enumeration e = links.elements(); e.hasMoreElements();) {
			TestCaseLinkImpl control = (TestCaseLinkImpl) e.nextElement();
			if (control.getName().equals(name))
				return control;
		}
		return null;
	}

	/**
	 * Convenience method to find the corresponding test case link via its
	 * bundle.
	 * 
	 * @param name name of the bundle
	 * @returns the corresponding testcaselink
	 */
	TestCaseLinkImpl getTestCaseLink(Bundle bundle) {
		if (bundle == null)
			return null;
		for (Enumeration e = links.elements(); e.hasMoreElements();) {
			TestCaseLinkImpl control = (TestCaseLinkImpl) e.nextElement();
			if (control.getBundle().getBundleId() == bundle.getBundleId())
				return control;
		}
		// We ignore this because sometimes this is useful
		// for example TestMagic uses this to discover how
		// it was installed.
		return null;
	}

	/**
	 * Tmp method for logging.
	 */
	void log(String msg, Throwable e) {
		ticks = 0; // For watchdog
		String extra = "";
		if (e instanceof BundleException) {
			Throwable nested = ((BundleException) e).getNestedException();
			if (nested != null)
				extra = "(" + nested.toString() + ") ";
		}
		System.out.println("Target: " + msg + extra
				+ (e == null ? "" : e.toString()));
		if (e != null)
			e.printStackTrace();
		System.out.flush();
	}

	/**
	 * Framework keys we want to have in the applet.
	 */
	static String[]	keys	= new String[] {"org.osgi.framework.os.name",
			"org.osgi.framework.os.version", "org.osgi.framework.processor",
			"org.osgi.framework.vendor", "org.osgi.framework.version",};

	/**
	 * Method called after opening the target.
	 * 
	 * We will send some of the framework properties and all VM properties to
	 * the director.
	 */
	public void setProperties() throws IOException {
		Hashtable dict = new Hashtable();
		Properties properties = System.getProperties();
		for (Enumeration e = properties.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			dict.put(key, properties.get(key));
		}
		for (int i = 0; i < keys.length; i++) {
			String value = context.getProperty(keys[i]);
			if (value != null)
				dict.put(keys[i], value);
		}
		//
		// The XSL output depends on the java.security.manager
		// property. This is in certain cases not set, but there
		// still is a security manager, so we set it in that case.
		//
		if (dict.get("java.security.manager") == null
				&& System.getSecurityManager() != null)
			dict.put("java.security.manager", "");
		TargetLink l = run;
		if (l != null)
			l.setTargetProperties(dict);
	}

	/**
	 * Reboot the framework.
	 */
	public void reboot(int cause) {
		System.exit(cause);
	}

	/**
	 * Try to update the framework. However, this method is not very useful
	 * because it seems to run in many race conditions.
	 */
	public void updateFramework() {
		Bundle b = context.getBundle(0);
		try {
			b.update();
		}
		catch (Exception e) {
			System.out.println("Update failed ");
		}
		return;
	}

	public String getProperty(String name, String def) {
		String result = System.getProperty(name);
		if (result == null) {
			result = context.getProperty(name);
		}
		return (result == null) ? def : result;
	}

	public void setTestProperties(Dictionary properties) throws IOException {
		Properties sysProps = System.getProperties();
		for (Enumeration e = properties.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = (String) properties.get(key);
			sysProps.setProperty(key, value);
		}
	}
}
