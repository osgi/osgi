/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.applet;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import netscape.application.*;
import netscape.application.Timer;

import org.osgi.framework.*;
import org.osgi.test.director.*;
import org.osgi.test.script.Tag;
import org.osgi.test.service.*;
import org.osgi.test.shared.*;


/**
 * Applet that communicates with the handler to control the testcases.
 * 
 * This applet is enclosed with the handler bundle and registered on the local
 * http server. When it is started it will contact the codebase() on the
 * AppletLink.PORT (probably 4445). This will not work through firewalls
 * obviously.
 * <p>
 * This is not a normal java Applet but instead it is based on IFC. This is done
 * for many reasons, it makes the Applet significant smaller (this whole applet
 * including all the images is <50K, this class <17K) in Netscape and it will
 * look the same over all platforms (no portability problems like AWT). IFC can
 * be downloaded for free from the Netscape site
 * <code> http://developer.netscape.com/docs/manuals/ifc </code> Obviously it
 * has the disadvantage that it requires the download of ifc112.jar in Internet
 * Explorer. However, this should be taken care of in the html file. As an extra
 * feature, this applet can be run standalone as well without a browser when
 * ifc112.jar is in the classpath.
 * <p>
 * Some quick info about IFC. The UI is layed out with a applet called
 * Constructor (can be downloaded from Netscape). This UI builder positions and
 * parametrizes all the components. Event handling is much simpler that
 * AWT/Swing. Each UI component can send a command string to the
 * TargetChain.applicationChain(). All interested parties can be on this chain
 * and inspect this string to see if they can implement the command. In this
 * applet, the necessary methods (canPerformCommand and performCommand) are
 * implemented using reflection. This means that a command in constructor called
 * doIt can be implemented by writing a method public void doIt( Object o ) in
 * this class. Commands are normally executed in a single thread. Commands can
 * be executed in this thread by calling performCommandLater() to allow
 * synchronization. Important is to not draw components, but to allow them to be
 * redrawn when the command queue is empty, ie use setDirty(true) instead of
 * draw(). There exists several books about IFC:
 * 
 * <pre>
 * 
 *  
 *   Mastering Netscape IFC, Steven Holzner, ISBN 0-7821-2116-0
 *   Using Netscape IFC, Arun Rao, ISBN 0-7897-1251-2
 *   Late Night Netscape IFC, Jason Beaver et al, ISBN 1-56276-540-X
 *   
 *  
 * </pre>
 * 
 * Obviously you should start with the quite well written tutorial that is part
 * of the IFC download.
 * <p>
 * The components are very easy to
 * <p>
 * The applet contains two parts. On the upper part, the list of testcases is
 * shown. On the lower part is the log output. Buttons are used to control the
 * execution.
 * <p>
 * This applet uses a proprietare protocol to talk to the handler. This protocol
 * is implemented in the AppletLink class. This class is used on both sides of
 * the link.
 * <p>
 * Security is implemented using Netscapes security model. Currently the OSGi
 * has no certificate which means that we must use codebase principals. This
 * means that the codebase is used as certificate. Unfortunately, this must be
 * explicitly enabled via a Netscape property. See
 * 
 * <pre>
 * 
 *  
 *   http://developer.netscape.com/docs/technote/security/sectn2.html
 *   http://developer.netscape.com/docs/technote/security/prefwrangler.html
 *   
 *  
 * </pre>
 * 
 * Though the latter might not work anymore.
 */
public class TestApplet extends Application implements ExtendedTarget, IApplet,
		TextViewOwner {
	final static String	NOHOST	= "no host found";
	Timer				tick;						// Mouse emulator to
	// prevent bug
	String				pwd;						// Last opened directory
	Popup				hosts;						// List of target hosts
	ListView			cases;						// List of testcases
	TextView			messages;					// Log view
	TextField			manual;					// Manual text field
	TextField			status;					// Status field
	Slider				progress;					// Shows test progress
	Button				control;					// Start/stop button
	Button				logging;					// Check for log output
	Button				debug;						// Check for debug
	// output
	Button				local;						// Only local hosts
	Button				forever;					// No timeout
	Button				xml;						// Show raw XML
	Button				single;					// Single stepping support
	TextField			prefix;					// Contains bundle name
	// prefix.
	Dictionary			targetProperties;			// Properties of target
	// (VM + Framework)
	RemoteService		target;					// Current selected target
	// ID
	boolean				run;						// If running a test run
	TextField			framework;					// Framework info from
	// targetProperties
	TextField			os;						// OS info from
	// targetProperties
	// (Framework)
	TextField			vm;						// VM info from
	// targetProperties
	Properties			cm;						// Configuration Management
	// properties
	ListView			bundles;					// Bundle manager
	Handler				handler;
	Hashtable			history	= new Hashtable();
	BundleContext		context;
	Thread				testthread;

	TestApplet(Handler handler, BundleContext context) {
		this.handler = handler;
		this.context = context;
	}

	/**
	 * Initialize the UI.
	 * 
	 * This method customizes the window and it will read the plan file. The
	 * plan file is called "test.planb" and is assumed to be accessable through
	 * the local resources.
	 */
	public void init() {
		try {
			RootView root = mainRootView();
			root.setColor(Color.lightGray);
			root.setBuffered(true);
			handler.setApplet(this);
			NPlan plan = getPlan("test");
			//plan.unarchiveObjects();
			View view = plan.viewWithContents();
			TargetChain.applicationChain().addTarget(this, true);
			view.setBounds(4, 4, root.width() - 8, root.height() - 8);
			view.setVertResizeInstruction(View.HEIGHT_CAN_CHANGE);
			view.setHorizResizeInstruction(View.WIDTH_CAN_CHANGE);
			cases = (ListView) ((ScrollGroup) plan.componentNamed("cases"))
					.contentView();
			TabListItem item = (TabListItem) cases.prototypeItem();
			item.setTabs(new int[] {160});
			messages = (TextView) ((ScrollGroup) plan
					.componentNamed("messages")).contentView();
			messages.setCommandForKey("copy", null, 'C', KeyEvent.CONTROL_MASK,
					View.WHEN_IN_MAIN_WINDOW);
			hosts = (Popup) plan.componentNamed("hosts");
			control = (Button) plan.componentNamed("control");
			logging = (Button) plan.componentNamed("logging");
			debug = (Button) plan.componentNamed("debug");
			local = (Button) plan.componentNamed("local");
			single = (Button) plan.componentNamed("single");
			progress = (Slider) plan.componentNamed("progress");
			status = (TextField) plan.componentNamed("status");
			framework = (TextField) plan.componentNamed("framework");
			os = (TextField) plan.componentNamed("os");
			vm = (TextField) plan.componentNamed("vm");
			forever = (Button) plan.componentNamed("forever");
			manual = (TextField) plan.componentNamed("manual");
			prefix = (TextField) plan.componentNamed("prefix");
			prefix.setStringValue("test.");
			bundles = (ListView) ((ScrollGroup) plan.componentNamed("bundles"))
					.contentView();
			cases.setDoubleCommand("showDescription");
			cases.setTarget(this);
			cases.removeAllItems();
			messages.setString("");
			messages.setFont(Font.fontNamed("Courier", Font.PLAIN, 12));
			messages.setOwner(this);
			status.setStringValue("v" + getVersion()
					+ " OSGi (c) 2001 All Rights Reserved");
			root.addSubview(view);
			tick = new Timer(this, "tick", 100);
			tick.start();
			_bundlesChanged(null);
			_targetsChanged(null);
			_casesChanged(null);
		}
		catch (Exception e) {
			System.err.println("In init " + e);
			e.printStackTrace();
		}
	}

	String getVersion() {
		Properties p = getProperties();
		return p.getProperty("version", "<>");
	}

	Properties getProperties() {
		if (cm == null)
			try {
				cm = new Properties();
				InputStream in = getResourceAsStream("/osgi.properties");
				if (in != null)
					cm.load(in);
			}
			catch (IOException e) {
				status.setStringValue("Cannot read properties");
			}
		return cm;
	}

	/**
	 * Convenience method to read in the plan file. If the plan was already read
	 * in, we return the previous plan.
	 * 
	 * Plans contain an archived version of the components.
	 * 
	 * @param name name of the plan file without extension (e.g. "test")
	 * @throws Exception 
	 * @returns The plan object or null if not found
	 */
	NPlan getPlan(String name) throws Exception {
		String file = "plans/" + name + ".plana";
		InputStream in = getResourceAsStream(file);
		if (in != null) {
			return new NPlan(in, this);
		}
		return null;
	}

	/**
	 * Netscape does not implement getResource() in class and therefore URLs for
	 * resources do not work. This is used to hack around this together with the
	 * PrivateBitmap class. This method is called when the decoder
	 * (deserializer) needs to create a new class. We just intercept the bitmap
	 * class and change it to our own implementation where we then use the right
	 * resource handling.
	 */
	public Class classForName(String name) throws ClassNotFoundException {
		if (name.equals("netscape.application.Bitmap"))
			return PrivateBitmap.class;
		if (name.equals("netscape.application.ListItem"))
			return TabListItem.class;
		if (name.equals("netscape.constructor.TargetProxy"))
			return TargetProxy.class;
		return null;
	}

	/**
	 * Get a resource with the right security.
	 * 
	 * @param name full name ame of the resource (as defined in
	 *        Class.getResourceAsStream())
	 * @returns An input stream
	 */
	public static InputStream getResourceAsStream(String name) {
		return TestApplet.class.getResourceAsStream(name);
	}

	/**
	 * Convenience method to get an image from the resources by name. This
	 * method assumes images are local to the package and stored in the images
	 * directory.
	 * 
	 * @param name Simple image name, e.g. "trash.gif"
	 * @returns bitmap or null if not found
	 */
	public static Bitmap getBitmap(String name) {
		try {
			//
			// AWT does not like to get urls that are unknown
			// so we test if the URL actually exists
			URL url = TestApplet.class.getResource("images/" + name);
			if (url == null)
				throw new FileNotFoundException("bitmap: images/" + name);
			InputStream in = url.openStream();
			in.close();
			return Bitmap.bitmapFromURL(url);
		}
		catch (Exception e) {
			System.err.println("Cannot load image " + name + " " + e);
		}
		return null;
	}

	/**
	 * Update the target popup.
	 * 
	 * The targets are represented as ID objects and the hosts is a popup that
	 * holds this list. This method is called from the handler via the
	 * AppletLink.
	 * <p>
	 * Unfortunately, the popup requires at least one element so we have to
	 * check for an empty list.
	 * 
	 * @param id List of target hosts
	 */
	public void targetsChanged() {
		performCommandLater(this, "targetsChanged", null);
	}

	public void _targetsChanged(Object parm) {
		boolean current = false;
		Vector targets = handler.getTargets();
		hosts.removeAllItems();
		if (targets != null && targets.size() >= 0) {
			for (Enumeration e = targets.elements(); e.hasMoreElements();) {
				RemoteService rs = (RemoteService) e.nextElement();
				if (!local.state() || RemoteServiceImpl.isLocal(rs)) {
					ListItem item = hosts.addItem(rs.toString(), "selectHost");
					item.setData(rs);
					if (target == null || target.equals(rs)) {
						hosts.selectItem(item);
						target = rs;
						current = true;
					}
				}
				else
					System.out.println("Skipping " + rs.getHost());
			}
		}
		if (hosts.count() == 0) {
			hosts.addItem(NOHOST, "");
			hosts.setEnabled(false);
			target = null;
		}
		else {
			hosts.setEnabled(true);
			if (!current) {
				target = (RemoteService) targets.elementAt(0);
				hosts.selectItemAt(0);
			}
		}
		hosts.setDirty(true);
		hosts.setCommand("selectHost");
		hosts.setTarget(TargetChain.applicationChain());
	}

	/**
	 * Set the bundles in the manager.
	 */
	public void bundlesChanged() {
		performCommandLater(this, "bundlesChanged", null);
	}

	public void _bundlesChanged(Object parm) throws Exception {
		Bundle bs[] = context.getBundles();
		String names[] = new String[bs.length];
		for (int i = 0; i < bs.length; i++)
			names[i] = (bs[i].getHeaders().get("bundle-name") + "")
					.toUpperCase();
		netscape.util.Sort.sort(names, bs, 0, bs.length, true);
		bundles.removeAllItems();
		for (int i = 0; i < bs.length; i++) {
			String name = bs[i].getHeaders().get("bundle-name") + "";
			if (name.startsWith(prefix.stringValue())) {
				ListItem item = bundles.addItem();
				item.setTitle(name);
				item.setData(bs[i]);
				String image = "unknown.gif";
				if (bs[i].getState() == Bundle.ACTIVE)
					image = "greenball.gif";
				if (bs[i].getState() == Bundle.RESOLVED)
					image = "greenball.gif";
				Image ball = getBitmap(image);
				item.setImage(ball);
				item.setSelectedImage(ball);
			}
		}
		bundles.sizeToMinSize();
		bundles.setDirty(true);
	}

	public void _prefix(Object o) throws Exception {
		_bundlesChanged(null);
	}

	public void _stopBundle(Object o) throws Exception {
		netscape.util.Vector selected = bundles.selectedItems();
		for (netscape.util.Enumeration e = selected.elements(); e
				.hasMoreElements();) {
			ListItem item = (ListItem) e.nextElement();
			Bundle b = (Bundle) item.data();
			b.stop();
		}
		_bundlesChanged(null);
	}

	public void _startBundle(Object o) throws Exception {
		netscape.util.Vector selected = bundles.selectedItems();
		for (netscape.util.Enumeration e = selected.elements(); e
				.hasMoreElements();) {
			ListItem item = (ListItem) e.nextElement();
			Bundle b = (Bundle) item.data();
			b.start();
		}
		_bundlesChanged(null);
	}

	public void _uninstall(Object o) throws Exception {
		netscape.util.Vector selected = bundles.selectedItems();
		for (netscape.util.Enumeration e = selected.elements(); e
				.hasMoreElements();) {
			ListItem item = (ListItem) e.nextElement();
			Bundle b = (Bundle) item.data();
			b.uninstall();
		}
		_bundlesChanged(null);
	}

	public void _updateBundle(Object o) throws Exception {
		netscape.util.Vector selected = bundles.selectedItems();
		if (selected == null)
			return;
		for (netscape.util.Enumeration e = selected.elements(); e
				.hasMoreElements();) {
			ListItem item = (ListItem) e.nextElement();
			Bundle b = (Bundle) item.data();
			b.update();
		}
		_bundlesChanged(null);
	}

	/**
	 * Command method when host is selected in hosts popup menu.
	 * 
	 * If we have a new target we stop a potential current run, set the testcase
	 * status to unknown and clear target info.
	 */
	public void _selectHost(Object o) throws Exception {
		ListItem item = hosts.selectedItem();
		handler.stopRun();
		this.target = (RemoteService) item.data();
		os.setStringValue("");
		framework.setStringValue("");
		vm.setStringValue("");
		setStatus(null, "unknown.gif");
	}

	/**
	 * If you fill in the host manually
	 */
	public void _manual(Object o) throws IOException {
		handler.connectManual(manual.stringValue());
	}

	public void alive(String msg) {
		status.setStringValue(progress.value() + "% " + msg);
		status.setDirty(true);
	}

	/**
	 * Set the list of testcases.
	 * 
	 * Called from the handler over the AppletLink. It will rebuild the list of
	 * testcases and will set status to unknown.
	 */
	public void casesChanged() {
		performCommandLater(this, "casesChanged", null);
	}

	public void _casesChanged(Object p) {
		if (run)
			return;
		cases.removeAllItems();
		Hashtable testcases = handler.getTestCases();
		String names[] = new String[testcases.size()];
		TestCase tcs[] = new TestCase[names.length];
		int index = 0;
		for (Enumeration e = testcases.elements(); e.hasMoreElements();) {
			TestCase tc = (TestCase) e.nextElement();
			names[index] = tc.getName();
			tcs[index] = tc;
			index++;
		}
		netscape.util.Sort.sort(names, tcs, 0, names.length, true);
		for (int i = 0; i < tcs.length; i++) {
			TestCase tc = tcs[i];
			ListItem item = cases.addItem();
			item.setTitle(title(tc));
			item.setData(tc);
			setStatus(tc);
		}
		cases.sizeToMinSize();
		cases.setDirty(true);
	}

	String title(TestCase tc) {
		return tc.getName() + "\t" + tc.getDescription();
	}

	/**
	 * Add a message to the log view and set in status field.
	 * 
	 * Called local and from the AppletLink.
	 * 
	 * @param message Message added as a new line
	 */
	public void setMessage(String message) {
		messages.appendString(message);
		messages.appendString("\n");
		messages.sizeToMinSize();
		status.setStringValue(message);
	}

	/**
	 * Set the current progress.
	 * 
	 * @param progress percentage of progress (0 <= progress <=100)
	 */
	public void setProgress(int perc) {
		progress.setValue(perc);
		status.setStringValue(perc + "%");
	}

	/**
	 * Command callback when the start/stop button is pressed.
	 * 
	 * Depending on the current state, this will cancel or start a testrun.
	 */
	public synchronized void _control(Object o) throws Exception {
		if (waiting) {
			waiting = false;
			control.setImage(TestApplet.getBitmap("stop.gif"));
			control.setAltImage(TestApplet.getBitmap("stopin.gif"));
			control.setTitle("stop");
			control.setDirty(true);
			notifyAll();
			return;
		}
		if (testthread != null) {
			testthread.interrupt();
			return;
		}
		if (target == null) {
			setError("No host to test");
			return;
		}
		netscape.util.Vector set = cases.selectedItems();
		if (set.size() == 0) {
			set = new netscape.util.Vector();
			for (int i = 0; i < cases.count(); i++)
				set.addElement(cases.itemAt(i));
		}
		final netscape.util.Vector all = set;
		if (all.size() == 0) {
			setError("Zero test cases selected");
			return;
		}
		try {
			control.setImage(TestApplet.getBitmap("stop.gif"));
			control.setAltImage(TestApplet.getBitmap("stopin.gif"));
			control.setTitle("stop");
			control.setDirty(true);
			messages.setString("");
			messages.sizeToMinSize();
			messages.setDirty(true);
			progress.setValue(0);
			run = true;
			final int options = (logging.state() ? IHandler.OPTION_LOGGING : 0)
					| (logging.state() ? IHandler.OPTION_DEBUG : 0)
					| (logging.state() ? IHandler.OPTION_FOREVER : 0);
			final TestApplet applet = this;
			testthread = new Thread() {
				public void run() {
					try {
						handler.startRun(applet.target.getHost(), applet.target
								.getPort(), options);
						for (netscape.util.Enumeration e = all.elements(); e
								.hasMoreElements();) {
							ListItem item = (ListItem) e.nextElement();
							TestCase testcase = (TestCase) item.data();
							history.remove(testcase);
							setStatus(testcase);
							Tag result = handler.getRun().doTestCase(testcase);
							history.put(testcase, result);
							setStatus(testcase);
							Thread.sleep(3000);
						}
					}
					catch (InterruptedException ee) {
						System.out.println("interrupted");
					}
					catch (Exception e) {
						System.out.println("exception in testcase");
						e.printStackTrace();
					}
					String lastRun = handler.stopRun();
					performCommandLater(TargetChain.applicationChain(),
							"showTestResult", lastRun);
					testthread = null;
				}
			};
			testthread.start();
		}
		catch (Exception e) {
			setError("Starting run " + e);
		}
		_bundlesChanged(null);
	}

	int errors(Tag result) {
		return Integer.parseInt(result.getAttribute("errors", "0"));
	}

	void setStatus(TestCase testcase) {
		Tag result = (Tag) history.get(testcase);
		if (result == null)
			setStatus(testcase, "unknown.gif");
		else
			if (errors(result) == 0)
				setStatus(testcase, "ok.gif");
			else
				setStatus(testcase, "failed.gif");
	}

	/**
	 * Dummy target for emulating mouse events. When a view is dirtied in
	 * another thread, it is not executed until there is a new event. This will
	 * just generate events every 100 ms.
	 */
	public void _tick(Object o) {
	}

	/**
	 * Called when the run is finished to clean up the UI.
	 */
	public void finished() {
		if (run) {
			control.setImage(TestApplet.getBitmap("right.gif"));
			control.setAltImage(TestApplet.getBitmap("rightin.gif"));
			control.setTitle("start");
			control.setDirty(true);
			run = false;
		}
	}

	/**
	 * Show a popup window with an error message modal.
	 * 
	 * @param message String to show
	 */
	public void setError(String message) {
		status.setStringValue(message);
		Alert.runAlertInternally(Alert.warningImage(), "Error occurred",
				format(message), "OK", null, null);
	}

	String format(String msg) {
		StringBuffer sb = new StringBuffer();
		int pos = 0;
		for (int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if (Character.isWhitespace(c) && pos > 40) {
				sb.append("\n");
				pos = 0;
			}
			else
				sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Command callback for install button.
	 * 
	 * Get a file name and install that file in the handler framework (NOT the
	 * target).
	 */
	public void _install(Object o) throws Exception {
		FileChooser fc = new FileChooser(Application.application()
				.mainRootView(), "Select bundle to install",
				FileChooser.LOAD_TYPE);
		if (pwd != null)
			fc.setDirectory(pwd);
		fc.showModally();
		String name = fc.file();
		String dir = fc.directory();
		if (name != null && dir != null)
			try {
				pwd = dir;
				File loc = new File(dir, name);
				FileInputStream in = new FileInputStream(loc);
				context.installBundle(loc.toURL().toString(), in);
			}
			catch (Exception e) {
				String msg = e.toString();
				if (e instanceof BundleException) {
					msg = ((BundleException) e).getNestedException().toString();
					Alert
							.runAlertInternally("Could not install bundle"
									+ dir + "/" + name, "Error msg: " + msg,
									"OK", null, null);
				}
			}
	}

	/**
	 * Command callback for saving the log file.
	 */
	public void _saveLog(Object o) throws Exception {
		FileChooser fc = new FileChooser(Application.application()
				.mainRootView(), "Save log file", FileChooser.SAVE_TYPE);
		if (pwd != null)
			fc.setDirectory(pwd);
		fc.setFile("log.ref");
		fc.showModally();
		String name = fc.file();
		String dir = fc.directory();
		if (name != null && dir != null) {
			pwd = dir;
			String content = messages.string();
			PrintWriter out = new PrintWriter(new FileWriter(
					new File(dir, name)));
			BufferedReader in = new BufferedReader(new StringReader(content));
			String line = in.readLine();
			while (line != null) {
				out.println(Log.cleanup(line));
				line = in.readLine();
			}
			out.println();
			out.close();
		}
	}

	/**
	 * Called from the handler for a result of the testcase.
	 * 
	 * Update the testcase line in the window to show an icon for the result.
	 * 
	 * @param testcase ID of the testcase results
	 * @param errors Nr of detected errors
	 */
	public void setResult(TestCase testcase, int errors) {
		if (errors == 0) {
			if (!logging.state()) {
				setMessage("[PASSED " + testcase.getName() + "]");
				setStatus(testcase, "ok.gif");
			}
			else {
				setMessage("[DONE " + testcase.getName() + "]");
				setStatus(testcase, "unknown.gif");
			}
		}
		else {
			setMessage("[FAILED due to errors " + testcase.getName() + "]");
			setStatus(testcase, "failed.gif");
		}
	}

	/**
	 * Set the status of a testcase with an icon.
	 * 
	 * The testcases can contain an icon for each test case. This method sets
	 * the icon for one or for all when the ID is null.
	 * 
	 * @param id id for testcase or null for all
	 * @param icon icon name for testcase
	 */
	void setStatus(TestCase testcase, String icon) {
		Bitmap bitmap = TestApplet.getBitmap(icon);
		for (int i = 0; i < cases.count(); i++) {
			ListItem item = cases.itemAt(i);
			if (item.data() == testcase) {
				item.setImage(bitmap);
				item.setSelectedImage(bitmap);
			}
		}
		cases.setDirty(true);
	}

	/**
	 * Called from the handler to set the framework and VM properties from the
	 * target.
	 * 
	 * This method will update the framework, os and vm properties on the
	 * window.
	 * 
	 * @param properties List of VM + Framework properties
	 */
	public void setTargetProperties(Dictionary properties) {
		targetProperties = properties;
		framework.setStringValue(properties.get("org.osgi.framework.vendor")
				+ " " + properties.get("org.osgi.framework.version"));
		os.setStringValue(properties.get("org.osgi.framework.os.name") + " "
				+ properties.get("org.osgi.framework.os.version") + " "
				+ properties.get("org.osgi.framework.processor"));
		vm.setStringValue(properties.get("java.version") + " "
				+ properties.get("java.vendor") + " "
				+ properties.get("os.name") + " " + properties.get("os.arch"));
	}

	public void _local(Object o) throws Exception {
		_targetsChanged(o);
	}

	public void _selectTestCase(Object o) throws Exception {
		/*
		 * ListView list = (ListView) o; ListItem item = list.selectedItem();
		 * Tag tag = (Tag) history.get( item.data() ); show( tag, "Test run
		 * history", false );
		 */
	}

	/**
	 * Double click ...
	 */
	public void _showDescription(Object o) throws Exception {
		ListView list = (ListView) o;
		ListItem item = list.selectedItem();
		TestCase tc = (TestCase) item.data();
		Tag description = handler.getDescription(tc);
		messages.setString(description.toString());
		messages.sizeToMinSize();
		messages.setDirty(true);
	}

	public void _modifyBundle(Object o) {
	}

	public void _showTestResult(Object o) throws Exception {
		String run = (String) o;
		String base = handler.getBaseURL() + "#" + run;
		setMessage("[SEE " + base + "]");
	}

	/**
	 * Script interface
	 */
	public void _newScriptEditor(Object o) throws Exception {
		FileChooser fc = new FileChooser(Application.application()
				.mainRootView(), "Open Script File", FileChooser.LOAD_TYPE);
		if (pwd != null)
			fc.setDirectory(pwd);
		fc.setFile("untitled.xml");
		fc.showModally();
		String name = fc.file();
		String dir = fc.directory();
		if (name != null && dir != null) {
			pwd = dir;
			try {
				File where = new File(dir, name);
				handler.newScriptEditor(name, where.toURL());
			}
			catch (Exception e) {
				setError("Cannot read file: " + e);
				e.printStackTrace();
			}
		}
	}

	boolean isLocal(RemoteService rs) {
		try {
			InetAddress a = InetAddress.getByName(rs.getHost());
			InetAddress b = InetAddress.getLocalHost();
			return a.equals(b);
		}
		catch (UnknownHostException e) {
			return false;
		}
	}

	boolean	waiting	= false;

	public synchronized void step(String message) {
		if (single.state()) {
			waiting = true;
			performCommandLater(this, "step", message);
			while (waiting)
				try {
					wait();
				}
				catch (InterruptedException e) {
				}
				finally {
					waiting = false;
				}
		}
	}

	public void _step(Object o) throws Exception {
		status.setStringValue("Step: " + o.toString());
		control.setImage(TestApplet.getBitmap("mml.gif"));
		control.setAltImage(TestApplet.getBitmap("mml_in.gif"));
		control.setTitle("step");
		control.setDirty(true);
	}
	
	
	public void _properties( Object o ) {
		FileChooser fc = new FileChooser(Application.application()
				.mainRootView(), "Open Properties File", FileChooser.LOAD_TYPE);
		String targetProperties = System.getProperty(IRun.TEST_PROPERTIES_FILE);
		if ( targetProperties != null ) {
			File 	f = new File( targetProperties );
			fc.setFile(f.getName());
			if ( f.getParentFile() != null )
				fc.setDirectory(f.getParentFile().getAbsolutePath());			
		}
		
		fc.showModally();
		if ( fc.file() != null ) {
			File dir  = new File(fc.directory());
			targetProperties = new File(dir, fc.file()).getAbsolutePath();
			System.getProperties().setProperty("org.osgi.test.properties.file", targetProperties);
			setMessage("Set properties file to " + targetProperties );
		}
	}

	/**
	 * TextViewOwner interface
	 */
	public void attributesDidChange(TextView t, Range r) {
	}

	public void attributesWillChange(TextView t, Range r) {
	}

	public void linkWasSelected(TextView t, Range r, String url) {
	}

	public void selectionDidChange(TextView t) {
	}

	public void textDidChange(TextView t, Range r) {
	}

	public void textEditingDidBegin(TextView t) {
	}

	public void textEditingDidEnd(TextView t) {
	}

	public void textWillChange(TextView t, Range r) {
	}

	/**
	 * Convenience method to find a method from the reflection API.
	 */
	public Method find(String command) throws NoSuchMethodException,
			IllegalAccessException {
		Class[] parameterTypes = new Class[1];
		parameterTypes[0] = Object.class;
		return getClass().getMethod("_" + command, parameterTypes);
	}

	/**
	 * Implementation of
	 * 
	 * @see ExtendedTarget interface.
	 * 
	 * Lookup the command as a method with a '' prefix and x(String,Object). If
	 * the method exists, execute it with the given parameters, else throw an
	 * exception.
	 * 
	 * @param command Name of command, translated method:
	 *        <command>(String,Object)
	 * @param arg Argument given to method
	 */
	public void performCommand(String command, Object arg) {
		try {
			Object[] parameters = {arg};
			Method m = find(command);
			m.invoke(this, parameters);
		}
		catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
		catch (IllegalAccessException e) {
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException("No Such Method " + command);
		}
	}

	/**
	 * Implementation of
	 * 
	 * @see ExtendedTarget interface.
	 * 
	 * Lookup the command as a method with a '' prefix and x(String,Object). If
	 * the method exists, return true, else return false.
	 * 
	 * @param command Name of command, translated method:
	 *        <command>(String,Object)
	 */
	public boolean canPerformCommand(String command) {
		try {
			Method m = find(command);
			return m != null;
		}
		catch (NoSuchMethodException e) {
		}
		catch (IllegalAccessException e) {
		}
		return false;
	}
}
