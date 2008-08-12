/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.test.script.*;
import org.osgi.test.service.TestCase;
import org.osgi.test.shared.*;
import org.osgi.tools.command.*;

/**
 * Provide commands to the console and the script.
 * 
 * Originally it was the intention to use only console commands and provide a
 * scripting facility to the console. However, the amount of information that
 * the test resuls had to contain was so staggering, that there was a need for
 * the XML Tag object. Maybe, in the future, the console should standardize the
 * XML object. It was considered still nice to run the tests from the console,
 * so these commands remained.
 */
public class TestCommands implements CommandProvider {
	BundleContext		context;
	Handler				handler;
	File				dir;
	String				name;
	int					options		= 0;
	Hashtable			installed;
	ServiceRegistration	registration;
	long				lastExecution;
	int					separate	= 2000;

	TestCommands(Handler handler, BundleContext context) {
		this.context = context;
		this.handler = handler;
		Hashtable props = new Hashtable();
		props.put(Constants.SERVICE_PID, "org.osgi.test.director.commands");
		props.put(Constants.SERVICE_DESCRIPTION,
				"Provides console access to test runs");
		registration = context.registerService(CommandProvider.class.getName(),
				this, props);
		Script.addProvider(this);
	}

	/**
	 * Close and remove all traces.
	 */
	void close() {
		Script.removeProvider(this);
		registration.unregister();
	}

	public String getName() {
		return "test";
	}

	public String getHelp() {
		return "TEST\r\n" + "startun			   Starts a test run\r\n"
				+ "testcase <name> ...   Runs one or more testcases\r\n"
				+ "exec <script url>     executes a script\r\n"
				+ "stoprun			   Stops the run\r\n";
	}

	public Object toString(Object o) {
		return null;
	}

	/**
	 * Wait for a number of services to appear in the service registry.
	 * 
	 * If the /count/ services appear in the registry this command returns
	 * without error. Otherwise it returns an error.
	 */
	public Tag _waitfor(ScriptContext script, Tag tag) throws Exception {
		int max = Integer.parseInt(tag.getAttribute("max", "10000"));
		int count = Integer.parseInt(tag.getAttribute("count", "1"));
		int poll = Integer.parseInt(tag.getAttribute("poll", "500"));
		String filter = tag.getContentsAsString();
		if (filter.length() == 0)
			throw new RuntimeException("Need filter argument to waitfor");
		long deadline = System.currentTimeMillis() + max * 1000;
		while (deadline > System.currentTimeMillis()) {
			ServiceReference ref[] = context.getServiceReferences(null, filter);
			if (ref != null && ref.length >= count)
				return tag;
			Thread.sleep(poll);
		}
		throw new RuntimeException("Timed out");
	}

	/**
	 * Set the properties property
	 */
	
	public Tag _properties( ScriptContext script, Tag tag) throws Exception {
		Tag result = new Tag("testproperties");
		String location = tag.getAttribute("location");
		if ( location != null ) {
			result.addAttribute("location", location );
			System.getProperties().setProperty(IRun.TEST_PROPERTIES_FILE, location );
		} else {
			result.addAttribute("error", "no location");
		}
		return tag;
	}

	
	/**
	 * Update the framework.
	 * 
	 * If the /count/ services appear in the registry this command returns
	 * without error. Otherwise it returns an error.
	 */
	public Tag _updateframework(ScriptContext script, Tag tag) throws Exception {
		handler.run.updateTarget();
		Tag result = new Tag("updateframework");
		result.addAttribute("time", new Date());
		return result;
	}

	/**
	 * Reboot the framework.
	 * 
	 * If the /count/ services appear in the registry this command returns
	 * without error. Otherwise it returns an error.
	 */
	public Tag _reboot(ScriptContext script, Tag tag) throws Exception {
		int status = Integer.parseInt(tag.getAttribute("status", "0"));
		handler.run.rebootTarget(status);
		Tag result = new Tag("reboot");
		result.addAttribute("time", new Date());
		return result;
	}

	/**
	 * Run a testcase.
	 */
	public Tag _testcase(ScriptContext script, Tag tag) throws Exception {
		String name = tag.getContentsAsString().trim();
		long wait = (lastExecution + separate) - System.currentTimeMillis();
		if (wait > 0)
			Thread.sleep(wait);
		lastExecution = System.currentTimeMillis();
		try {
			TestCase tc = handler.getTestCase(name);
			if (tc == null)
				throw new RuntimeException("No such test case: " + name);
			return handler.run.doTestCase(tc);
		}
		catch (Throwable t) {
			t.printStackTrace();
			return new Tag("exception", "" + t);
		}
		finally {
			lastExecution = System.currentTimeMillis();
		}
	}

	/**
	 * Delay a time.
	 */
	public Tag _delay(ScriptContext context, Tag tag) throws Exception {
		int value = Integer.parseInt(tag.getAttribute("duration", "10"));
		String type = tag.getAttribute("type", "sec");
		if (type == null || type.startsWith("sec"))
			value *= 1000;
		else
			if (type.equals("minute"))
				value *= 60 * 1000;
			else
				if (type.equals("hour"))
					value *= 60 * 60 * 1000;
				else
					if (type.equals("week"))
						value *= 60 * 60 * 1000 * 24 * 7;
					else
						if (type.equals("day"))
							value *= 60 * 60 * 1000 * 24;
						else
							if (type.equals("ms"))
								;
		Thread.sleep(value);
		return tag;
	}

	/**
	 * Space two test case execution in time so the target has some time to come
	 * to rest.
	 */
	public Tag _separate(ScriptContext context, Tag tag) throws Exception {
		int value = Integer.parseInt(tag.getAttribute("duration", "2"));
		String type = tag.getAttribute("type", "sec");
		if (type == null || type.startsWith("sec"))
			value *= 1000;
		else
			if (type.equals("minute"))
				value *= 60 * 1000;
			else
				if (type.equals("hour"))
					value *= 60 * 60 * 1000;
				else
					if (type.equals("week"))
						value *= 60 * 60 * 1000 * 24 * 7;
					else
						if (type.equals("day"))
							value *= 60 * 60 * 1000 * 24;
						else
							if (type.equals("ms"))
								;
		separate = value;
		return tag;
	}

	/**
	 * Cancel a run.
	 */
	public Object _cancel(CommandProvider intp) {
		handler.stopRun();
		return null;
	}

	/**
	 * Install and start a bundle.
	 */
	public Tag _istart(ScriptContext context, Tag tag) throws Exception {
		Hashtable phased = new Hashtable();
		Tag result = new Tag("istart");
		for (Enumeration e = tag.getContents("url").elements(); e
				.hasMoreElements();) {
			Tag urltag = (Tag) e.nextElement();
			String url = urltag.getContentsAsString();
			try {
				Bundle bundle = this.context.installBundle(url);
				phased.put(url, bundle);
				bundle.update();
			}
			catch (Exception ee) {
				result.addAttribute("exception", "" + ee);
				ee.printStackTrace();
			}
		}
		for (Enumeration e = phased.keys(); e.hasMoreElements();) {
			String url = (String) e.nextElement();
			Bundle b = (Bundle) phased.get(url);
			try {
				b.start();
			}
			catch (Exception ee) {
				ee.printStackTrace();
				result.addAttribute("exception", "" + e);
			}
			result.addContent(new Tag("bundle", new String[] {"url", url}));
		}
		return result;
	}

	/**
	 * Start a run. Options:
	 * 
	 * <pre>
	 * 
	 *  
	 *   
	 *     -host host
	 *     -port port
	 *     -forever
	 *     -debug
	 *     -logging
	 *    
	 *   
	 *  
	 * </pre>
	 */
	public Object _startrun(CommandInterpreter intp) throws Exception {
		installed = new Hashtable();
		int options = 0;
		String host = "localhost";
		int port = 3191;
		String arg = intp.nextArgument();
		while (arg != null) {
			if (arg.equals("-host"))
				host = intp.nextArgument();
			else
				if (arg.equals("-port"))
					port = Integer.parseInt(intp.nextArgument());
				else
					if (arg.equals("-forever"))
						options |= IHandler.OPTION_FOREVER;
					else
						if (arg.equals("-debug"))
							options |= IHandler.OPTION_DEBUG;
						else
							if (arg.equals("-logging"))
								options |= IHandler.OPTION_LOGGING;
							else
								throw new RuntimeException("No such option: "
										+ arg);
			arg = intp.nextArgument();
		}
		handler.startRun(host, port, options);
		return handler.run.history;
	}

	/**
	 * Console command to execute a script. Start run before calling this.
	 * 
	 * Options:
	 * 
	 * <pre>
	 * 
	 *  
	 *   
	 *     script-url
	 *     -output file
	 *    
	 *   
	 *  
	 * </pre>
	 */
	public Object _exec(CommandInterpreter intp) throws Throwable {
		String arg = intp.nextArgument();
		String output = null;
		String input = null;
		Tag result = null;
		while (arg != null) {
			if (arg.equals("-output"))
				output = intp.nextArgument();
			else {
				input = arg;
				try {
					if (input.indexOf(':') < 0) {
						input = new File(dir, input).toURL().toString();
					}
					Script script = new Script(new URL(input));
					result = script.execute();
				}
				catch (Exception e) {
					result = new Tag("exception", "" + e);
					e.printStackTrace();
				}
			}
			arg = intp.nextArgument();
		}
		if (result != null && output != null) {
			File file = new File(dir, output);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file), "ISO-8859-1"));
			pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
			pw.println("<?xml-stylesheet type='text/xsl' href=''?>");
			result.print(0, pw);
			pw.close();
		}
		return result;
	}

	/**
	 * Execute a testcase. Options:
	 * 
	 * <pre>
	 * 
	 *  
	 *   
	 *     testcase ...
	 *    
	 *   
	 *  
	 * </pre>
	 */
	public Object _testcase(CommandInterpreter intp) throws Exception {
		Tag result = new Tag("testcases");
		String arg = intp.nextArgument();
		while (arg != null) {
			TestCase tc = handler.getTestCase(arg);
			if (tc == null)
				throw new RuntimeException("No such test case: " + arg);
			Tag tag = handler.run.doTestCase(tc);
			result.addContent(tag);
			arg = intp.nextArgument();
		}
		return result;
	}

	/**
	 * Stop a run.
	 */
	public Object _stoprun(CommandInterpreter intp) throws Exception {
		String result = handler.stopRun();
		if (installed != null) {
			Bundle bundles[] = this.context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if (installed.get(bundles[i]) != null)
					try {
						bundles[i].uninstall();
					}
					catch (Exception e) {
						e.printStackTrace();
					};
			}
			installed = null;
		}
		return result;
	}
}