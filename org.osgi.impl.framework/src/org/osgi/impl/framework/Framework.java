/**
 * Copyright (c) 1999 - 2002 Gatespace AB. All Rights Reserved.
 *
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.framework.Constants;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * This class contains references to all common data structures
 * inside framework.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Framework
{
    /**
     * Specification version for this framework.
     */
    static final String SPECIFICATION_VERSION = "1.2";

    /**
     * AdminPermission used for permission check.
     */
    final static AdminPermission ADMIN_PERMISSION = new AdminPermission();

    /**
     * Default start level.
     */
    final static int DEFAULT_START_LEVEL = 1;

    /**
     * Boolean indicating that framework is running.
     */
    boolean active;

    /**
     * Set during shutdown process.
     */
    boolean shuttingdown = false;

    /**
     * If true, check framework permissions.
     */
    boolean checkPermissions = false;

    /**
     * PermissionAdmin service.
     */
    PermissionAdminImpl permissions;

    /**
     * All bundle in this framework.
     */
    Bundles bundles;

    /**
     * All listeners in this framework.
     */
    Listeners listeners = new Listeners();

    /**
     * All exported and imported packages in this framework.
     */
    Packages packages;

    /**
     * All registered services in this framework.
     */
    Services services = new Services();

    /**
     * System bundle
     */
    SystemBundle systemBundle;

    /**
     * Start level service
     */
    StartLevelImpl startLevel;

    /**
      * URL Handler
      */
    URLHandler urlHandler;


    private static final Hashtable processorAliases = new Hashtable();
    static {
      processorAliases.put("psc1k",     "Ignite");
      processorAliases.put("power",     "PowerPC");
      processorAliases.put("ppc",       "PowerPC");
      processorAliases.put("pentium",   "x86");
      processorAliases.put("i386",      "x86");
      processorAliases.put("i486",      "x86");
      processorAliases.put("i586",      "x86");
      processorAliases.put("i686",      "x86");
    }

    private static final Hashtable osAliases = new Hashtable();
    static {
      osAliases.put("os/2",         "OS2");
      osAliases.put("procnto",      "QNX");
      osAliases.put("win95",        "Windows95");
      osAliases.put("windows 95",   "Windows95");
      osAliases.put("win98",        "Windows98");
      osAliases.put("windows 98",   "Windows98");
      osAliases.put("winnt",        "WindowsNT");
      osAliases.put("windows nt",   "WindowsNT");
      osAliases.put("wince",        "WindowsCE");
      osAliases.put("windows ce",   "WindowsCE");
      osAliases.put("win2000",      "Windows2000");
      osAliases.put("windows 2000", "Windows2000");
      osAliases.put("winxp",        "Windows95");
      osAliases.put("windows xp",   "Windows95");
    }
    /**
     * Contruct a framework.
     *
     * @param dir Directory where framework should store persistent data
     */
    public Framework(File dir) throws Exception
    {
	// See if we have a storage directory
	if (dir != null) {
	    if (dir.exists()) {
		if (!dir.isDirectory()) {
		    throw new IOException("Not a directory: " + dir);
		}
	    } else {
		if (!dir.mkdirs()) {
		    throw new IOException("Cannot create directory: " + dir);
		}
	    }
	}
	packages = new Packages(this);
	systemBundle = new SystemBundle(this);
	bundles = new Bundles(this, dir.getAbsoluteFile());
  urlHandler = new URLHandler(this);
        startLevel = new StartLevelImpl(this);
	checkPermissions = System.getSecurityManager() != null;
	if (checkPermissions) {
	    permissions = new PermissionAdminImpl(this, dir);
	    Policy.setPolicy(new FrameworkPolicy(permissions, Policy.getPolicy()));
	    String [] classes = new String [] { PermissionAdmin.class.getName() };
	    services.register(systemBundle,
			      classes,
			      permissions,
			      null);
	}
	String [] classes = new String [] { PackageAdmin.class.getName() };
	services.register(systemBundle,
			  classes,
			  new PackageAdminImpl(this),
			  null);
	bundles.load();
    }


    /**
     * Start this Framework.
     * This method starts all the bundles that were started at
     * the time of the last shutdown.
     *
     * <p>If the Framework is already started, this method does nothing.
     * If the Framework is not started, this method will:
     * <ol>
     * <li>Enable event handling. At this point, events can be delivered to
     * listeners.</li>
     * <li>Attempt to start all bundles marked for starting as described in the
     * {@link Bundle#start} method.
     * Reports any exceptions that occur during startup using
     * <code>FrameworkErrorEvents</code>.</li>
     * <li>Set the state of the Framework to <i>active</i>.</li>
     * <li>Broadcasting a <code>FrameworkEvent</code> through the
     * <code>FrameworkListener.frameworkStarted</code> method.</li>
     * </ol></p>
     *
     * <p>If this Framework is not launched, it can still install,
     * uninstall, start and stop bundles.  (It does these tasks without
     * broadcasting events, however.)  Using Framework without launching
     * it allows for off-line debugging of the Framework.</p>
     *
     */
    public void launch() throws BundleException
    {
	launch(DEFAULT_START_LEVEL);
    }

    /**
     * Start this Framework at an initial start level.
     * For further details, see launch().
     *
     * @param startlevel New start level.
     * @exception BundleException If launch fails.
     */
    public void launch(int startlevel) throws BundleException
    {
	if (!active) {
	    active = true;
            startLevel.forceStartLevel(startlevel);
            for (int i = 0; i <= startlevel; ++i) {
              bundles.startBundles(i);
            }
            bundles.save();
            systemBundle.systemActive();
            String [] classes = new String [] { StartLevel.class.getName() };
	    services.register(systemBundle, classes, startLevel, null);
            listeners.frameworkEvent(new FrameworkEvent(FrameworkEvent.STARTED, systemBundle, null));
	}
    }

    /**
     * Set the framework current start level.
     *
     * @param startlevel New start level.
     */
    public void setStartLevel(int startlevel)
    {
	startLevel.setStartLevel(startlevel);
    }

    /**
     * Stop this Framework, suspending all started contexts.
     * This method suspends all started contexts so that they can be
     * automatically restarted when this Framework is next launched.
     *
     * <p>If the framework is not started, this method does nothing.
     * If the framework is started, this method will:
     * <ol>
     * <li>Set the state of the Framework to <i>inactive</i>.</li>
     * <li>Suspended all started bundles as described in the
     * {@link Bundle#stop} method except that the persistent
     * state of the bundle will continue to be started.
     * Reports any exceptions that occur during stopping using
     * <code>FrameworkErrorEvents</code>.</li>
     * <li>Disable event handling.</li>
     * </ol></p>
     *
     */
    public void shutdown()
    {
	if (active) {
	    active = false;
	    List slist = bundles.getRunningBundles();
	    Collections.sort(slist, Collections.reverseOrder());
	    shuttingdown = true;
	    systemBundle.systemShuttingdown();
	    // Stop bundles
	    for (Iterator i = slist.iterator(); i.hasNext();) {
		BundleImpl b = (BundleImpl)i.next();
		try {
		    synchronized (b) {
			if (b.state == Bundle.ACTIVE) {
			    b.stop();
			}
		    }
		} catch (BundleException be) {
		    listeners.frameworkEvent(new FrameworkEvent(FrameworkEvent.ERROR, b, be));
		}
	    }
	    shuttingdown = false;
	    bundles.save();
	    // Purge any unrefreshed bundles
	    BundleImpl [] all = bundles.getBundles();
	    for (int i = 0; i < all.length; i++) {
	        all[i].purge();
	    }
	}
    }


    /**
     * Install a bundle from the given location.
     *
     * @param location The location identifier of the bundle to install.
     * @param in The InputStream from which the bundle will be read.
     * @return The BundleImpl object of the installed bundle.
     * @exception BundleException If the install failed.
     */
    public long installBundle(String location, InputStream in)
	throws BundleException
    {
	return bundles.install(location, in).id;
    }



    /**
     * Set the start level for a bundle.
     *
     * @param id Id of bundle to start.
     * @param startlevel The new start level.
     * @exception BundleException If set level faild.
     */
    public void setBundleStartLevel(long id, int startlevel)
	throws BundleException
    {
	BundleImpl b = bundles.getBundle(id);
	if (b != null) {
	    startLevel.setBundleStartLevel(b, startlevel);
	} else {
	    throw new BundleException("No such bundle: " + id);
	}
    }


    /**
     * Start a bundle.
     *
     * @param id Id of bundle to start.
     * @exception BundleException If start failed.
     */
    public void startBundle(long id) throws BundleException
    {
	BundleImpl b = bundles.getBundle(id);
	if (b != null) {
	    b.start();
	} else {
	    throw new BundleException("No such bundle: " + id);
	}
    }


    /**
     * Stop a bundle.
     *
     * @param id Id of bundle to stop.
     * @exception BundleException If stop failed.
     */
    public void stopBundle(long id) throws BundleException
    {
	BundleImpl b = bundles.getBundle(id);
	if (b != null) {
	    b.stop();
	} else {
	    throw new BundleException("No such bundle: " + id);
	}
    }


    /**
     * Uninstall a bundle.
     *
     * @param id Id of bundle to stop.
     * @exception BundleException If uninstall failed.
     */
    public void uninstallBundle(long id) throws BundleException
    {
	BundleImpl b = bundles.getBundle(id);
	if (b != null) {
	    b.uninstall();
	} else {
	    throw new BundleException("No such bundle: " + id);
	}
    }


    /**
     * Update a bundle.
     *
     * @param id Id of bundle to update.
     * @exception BundleException If update failed.
     */
    public void updateBundle(long id) throws BundleException
    {
	BundleImpl b = bundles.getBundle(id);
	if (b != null) {
	    b.update();
	} else {
	    throw new BundleException("No such bundle: " + id);
	}
    }


    /**
     * Retrieve location of the bundle that has the given
     * unique identifier.
     *
     * @param id The identifier of the bundle to retrieve.
     * @return A location as a string, or <code>null</code>
     * if the identifier doesn't match any installed bundle.
     */
    public String getBundleLocation(long id)
    {
	BundleImpl b = bundles.getBundle(id);
	if (b != null) {
	    return b.location;
	} else {
	    return null;
	}
    }

    /**
     * Check that we have admin permission.
     *
     * @exception SecurityException if we don't have admin permission.
     */
    void checkAdminPermission() {
	if (checkPermissions) {
	    AccessController.checkPermission(ADMIN_PERMISSION);
	}
    }

    //
    // Static package methods
    //

    /**
     * Retrieve the value of the named framework property.
     *
     */
    static String getProperty(String key)
    {
	if (Constants.FRAMEWORK_VERSION.equals(key)) {
	    // The version of the framework.
	    return SPECIFICATION_VERSION;
	} else if (Constants.FRAMEWORK_VENDOR.equals(key)) {
	    // The vendor of this framework implementation.
	    return "Gatespace AB";
	} else if (Constants.FRAMEWORK_LANGUAGE.equals(key)) {
	    // The language being used. See ISO 639 for possible values.
	    return Locale.getDefault().getLanguage();
	} else if (Constants.FRAMEWORK_OS_NAME.equals(key)) {
	    // The name of the operating system of the hosting computer.
      String osName = System.getProperty("os.name");
      if(osAliases.containsKey(osName.toLowerCase())) {
        osName = (String)osAliases.get(osName.toLowerCase());
      }
	    return osName;
	} else if (Constants.FRAMEWORK_OS_VERSION.equals(key)) {
	    // The version number of the operating system of the hosting computer.
	    String v = System.getProperty("os.version");
	    // Make sure it is not more characters than X.Y.Z
	    int dc = 0;
	    for (int i = 0; i < v.length(); i++) {
		char c = v.charAt(i);
		if (c >= '0' && c <= '9') {
		    continue;
		} else if (c == '.' && dc < 2) {
		    dc++;
		    continue;
		} else {
		    v = v.substring(0, i);
		}
	    }
	    return v;
	} else if (Constants.FRAMEWORK_PROCESSOR.equals(key)) {
    // The name of the processor of the hosting computer.
    String procName = System.getProperty("os.arch");
    if(processorAliases.containsKey(procName.toLowerCase())) {
      procName = (String)processorAliases.get(procName.toLowerCase());
    }
    return procName;
  } else if (Constants.FRAMEWORK_EXECUTIONENVIRONMENT.equals(key)) {
    return System.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
	} else {
	    return null;
	}
    }


    /**
     * Parse strings of format:
     *
     *   ENTRY (, ENTRY)*
     *   ENTRY = key (; key)* (; OP)*
     *   OP = param '=' value
     *
     * @param s String to parse
     * @param single If true, only allow one key per ENTRY
     *        and only allow unique parmeters for each ENTRY.
     * @return Iterator(Map(param -> value)).
     * @exception IllegalArgumentException If syntax error in input string.
     */
    static Iterator parseEntries(String s, boolean single)
    {
	ArrayList result = new ArrayList();
	if (s != null) {
	    AttributeTokenizer at = new AttributeTokenizer(s);
	    do {
		ArrayList keys = new ArrayList();
		HashMap params = new HashMap();
		boolean doingKeys = true;
		String key = at.getKey();
		if (key == null) {
		    throw new IllegalArgumentException("Expected key at: " + at.getRest());
		}
		if (!single) {
		    keys.add(key);
		    while ((key = at.getKey()) != null) {
			keys.add(key);
		    }
		}
		String param;
		while ((param = at.getParam()) != null) {
		    List old = (List)params.get(param);
		    if (old != null && single) {
			throw new IllegalArgumentException("Duplicate parameter: " + param);
		    }
		    String value = at.getValue();
		    if (value == null) {
			throw new IllegalArgumentException("Expected value at: " + at.getRest());
		    }
		    if (single) {
			params.put(param, value);
		    } else {
			if (old == null) {
			    old = new ArrayList();
			    params.put(param, old);
			}
			old.add(value);
		    }
		}
		if (at.getEntryEnd()) {
		    if (single) {
			params.put("key", key);
		    } else {
			params.put("keys", keys);
		    }
		    result.add(params);
		} else {
		    throw new IllegalArgumentException("Expected end of entry at: " + at.getRest());
		}
	    } while (!at.getEnd());
	}
	return result.iterator();
    }

}


/**
 * Class for tokenize an attribute string.
 */
class AttributeTokenizer
{
    String s;
    int length;
    int pos = 0;


    AttributeTokenizer(String input)
    {
	s = input;
	length = s.length();
    }


    String getWord()
    {
	skipWhite();
	boolean backslash = false;
	boolean quote = false;
	StringBuffer val = new StringBuffer();
	int end = 0;
    loop:
	for (; pos < length; pos++) {
	    if (backslash) {
		backslash = false;
		val.append(s.charAt(pos));
	    } else {
		char c = s.charAt(pos);
		switch (c) {
		case '"':
		    quote = !quote;
		    end = val.length();
		    break;
		case '\\':
		    backslash = true;
		    break;
		case ',': case ';': case '=':
		    if (!quote) {
			break loop;
		    }
		    // Fall through
		default:
		    val.append(c);
		    if (!Character.isWhitespace(c)) {
			end = val.length();
		    }
		    break;
		}
	    }
	}
	if (quote || backslash || end == 0) {
	    return null;
	}
	return val.substring(0, end);
    }


    String getKey()
    {
	if (pos >= length) {
	    return null;
	}
	int save = pos;
	if (s.charAt(pos) == ';') {
	    pos++;
	}
	String res = getWord();
	if (res != null) {
	    if (pos == length) {
		return res;
	    }
	    char c = s.charAt(pos);
	    if (c == ';' || c == ',') {
		return res;
	    }
	}
	pos = save;
	return null;
    }


    String getParam()
    {
	if (pos == length || s.charAt(pos) != ';') {
	    return null;
	}
	int save = pos++;
	String res = getWord();
	if (res != null && pos < length && s.charAt(pos) == '=') {
	    return res;
	}
	pos = save;
	return null;
    }


    String getValue()
    {
	if (s.charAt(pos) != '=') {
	    return null;
	}
	int save = pos++;
	skipWhite();
	String val = getWord();
	if (val == null) {
	    pos = save;
	    return null;
	}
	return val;
    }


    boolean getEntryEnd()
    {
	int save = pos;
	skipWhite();
	if (pos == length) {
	    return true;
	} else if (s.charAt(pos) == ',') {
	    pos++;
	    return true;
	} else {
	    pos = save;
	    return false;
	}
    }

    boolean getEnd()
    {
	int save = pos;
	skipWhite();
	if (pos == length) {
	    return true;
	} else {
	    pos = save;
	    return false;
	}
    }


    String getRest()
    {
	return s.substring(pos);
    }


    private void skipWhite()
    {
	for (; pos < length; pos++) {
	    if (!Character.isWhitespace(s.charAt(pos))) {
		break;
	    }
	}
    }

}
