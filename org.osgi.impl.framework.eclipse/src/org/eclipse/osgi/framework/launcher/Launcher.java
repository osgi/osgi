/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.launcher;

import java.lang.reflect.Constructor;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.internal.core.Msg;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.osgi.framework.util.Tokenizer;

/**
 * <p>This class provides an entry point for launching the OSGi framework.
 * It configures the OSGi framework according to the command line arguments:
 * <ul>
 * <li><b>-con[sole][:<i>port</i>]</b><br>
 *   Starts the OSGi framework with a console window.  Any command line arguments not recognized are passed
 *   to the console for it to execute.  If a port is specified the console will listen on that
 *   port for commands.  If no port is specified, the console will use System.in and System.out.
 * </li>
 * <li><b>-adaptor[:adaptor-name][adaptor-args]</b>
 * <pre>
 * [adaptor-name] := "" | fully qualified class name of the FrameworkAdapter
 * [adaptor-args] := *( ":" [value])
 * [value] := [token] | [quoted-string]
 *
 * This allows
 *
 * -adaptor::"bundledir=c:\jarbundles":reset 		DefaultAdaptor is chosen with args[] {"bundledir=c:\jarbundles", "reset"}
 * -adaptor:com.foo.MyAdaptor				com.foo.MyAdaptor chosen with args[] {}
 * </pre>
 *   <p>-adaptor specifies the implementation class for the FrameworkAdapter to be used.
 *   args contains a list of FrameworkAdaptor arguments, separated by ":".  FrameworkAdaptor arguments
 *   format is defined by the adaptor implementation class.  They
 *   are passed to the adaptor class as an array of Strings.
 *   Example arguments used by the DefaultAdaptor are:
 *   <ul>
 *   <li>"bundledir=<i>directory"</i>.  The directory to be used by the adaptor to store data.
 *   <li>reset</i>.  Perform the reset action to clear the bundledir.
 *   <p>Actions can be defined by an adaptor.  Multiple actions can be specified,
 *   separated by ":".
 *   </ul>
 *   <p>It is up to the adaptor implementation to define reasonable defaults if it's required
 *   arguments are not specified.
 *   <p>If -adaptor is not specified, or if no adaptor classname is specified, DefaultAdaptor will be
 *   used, which is file based and stores the files in the \bundles directory
 *   relative to the current directory.
 * </ul>
 * <li>-app[lication]:application-args
 * <pre>
 *    [application-args] := *( ":" [value])
 *    [value] := [token] | [quoted-string]
 * </pre>
 * <p>This argument allows arguments to be passed to specific applications at launch time.  This is for eclipse
 * plugins installed as applications.  The arguments are as Eclipse currently needs them - one list of key=value pairs 
 * which are parsed by the applications.  The application peels off only the args that apply to it.  Others are ignored. 
 * </li>
 * <p>
 * Any other command line arguments are passed on to the console window
 * of the framework if started with the -console option.  If the console is not started,
 * any unrecognized arguments will be ignored and a message displayed.
 * <p>
 * If none of the options above are specified, the OSGi framework is started:
 * <ul>
 * <li>with the Default FrameworkAdaptor
 * <li>without a console window
 * <li>without the remote agent
 * </ul>
 */
public class Launcher {

	/** default console port */
	protected String consolePort = ""; //$NON-NLS-1$

	/** flag to indicate whether or not to start the console */
	protected boolean console = false;

	/** string containing the classname of the adaptor to be used in this framework instance */
	protected String adaptorClassName = "org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor"; //$NON-NLS-1$

	protected final String osgiConsoleClazz = "org.eclipse.osgi.framework.internal.core.FrameworkConsole"; //$NON-NLS-1$

	/** array of adaptor arguments to be passed to FrameworkAdaptor.initialize() */
	protected String[] adaptorArgs = null;

	/* Components that can be installed and activated optionally. */
	private static final String OSGI_CONSOLE_COMPONENT_NAME = "OSGi Console"; //$NON-NLS-1$
	private static final String OSGI_CONSOLE_COMPONENT = "console.jar"; //$NON-NLS-1$

	/**
	 * main method for Launcher. This method creates an Launcher object
	 * and kicks off the actual launch of a Framework instance.
	 *
	 * @param args The command line arguments
	 */
	public static void main(String args[]) {

		new Launcher().doIt(args);

	}

	/**
	 *  Default constructor.  Nothing at all to do here.
	 */
	public Launcher() {
	}

	/**
	 *  Performs the actual launch based on the command line arguments
	 *
	 *  @param args The command line arguments
	 */
	protected void doIt(String[] args) {
		String[] consoleArgs = parseArgs(args);

		FrameworkAdaptor adaptor = null;
		try {
			adaptor = doAdaptor();
		} catch (Exception e) {
			System.out.println(Msg.formatter.getString("LAUNCHER_ADAPTOR_ERROR")); //$NON-NLS-1$
			e.printStackTrace();
			return;
		}

		OSGi osgi = doOSGi(adaptor);
		if (osgi != null) {
			if (console) {
				doConsole(osgi, consoleArgs);
			} else {
				osgi.launch();
			}
		}
	}

	/**
	 *  Parses the command line arguments and remembers them so they can be processed later.
	 *
	 *  @param args The command line arguments
	 *  @return String [] Any arguments that should be passed to the console
	 */
	protected String[] parseArgs(String[] args) {
		Vector consoleArgsVector = new Vector();
		for (int i = 0; i < args.length; i++) {
			boolean match = false;

			// Have to check for args that may be contained in double quotes but broken up by spaces - for example
			// -adaptor::"bundledir=c:/my bundle dir":reset should all be one arg, but java breaks it into 3 args, 
			// ignoring the quotes.  Must put it back together into one arg.
			String fullarg = args[i];
			int quoteidx = fullarg.indexOf("\""); //$NON-NLS-1$
			if (quoteidx > 0) {
				if (quoteidx == fullarg.lastIndexOf("\"")) { //$NON-NLS-1$
					boolean stillparsing = true;
					i++;
					while (i < args.length && stillparsing) {
						fullarg = fullarg + " " + args[i]; //$NON-NLS-1$
						i++;
						if (quoteidx < fullarg.lastIndexOf("\"")) { //$NON-NLS-1$
							stillparsing = false;
						}
					}
				}
			} else {
				// IDE can't pass double quotes due to known eclipse bug (see Bugzilla 93201).  Allowing for use of single quotes.
				quoteidx = fullarg.indexOf("'"); //$NON-NLS-1$
				if (quoteidx > 0) {
					if (quoteidx == fullarg.lastIndexOf("'")) { //$NON-NLS-1$
						boolean stillparsing = true;
						i++;
						while (i < args.length && stillparsing) {
							fullarg = fullarg + " " + args[i]; //$NON-NLS-1$
							i++;
							if (quoteidx < fullarg.lastIndexOf("'")) { //$NON-NLS-1$
								stillparsing = false;
							}
						}
					}
					fullarg = fullarg.replace('\'', '\"');
				}
			}

			Tokenizer tok = new Tokenizer(fullarg);
			if (tok.hasMoreTokens()) {
				String command = tok.getString(" "); //$NON-NLS-1$
				StringTokenizer subtok = new StringTokenizer(command, ":"); //$NON-NLS-1$
				String subcommand = subtok.nextToken().toLowerCase();

				if (matchCommand("-console", subcommand, 4)) { //$NON-NLS-1$
					_console(command);
					match = true;
				}
				if (matchCommand("-adaptor", subcommand, 2)) { //$NON-NLS-1$
					_adaptor(command);
					match = true;
				}
				if (match == false) {
					// if the command doesn't match any of the known commands, save it to pass
					// to the console
					consoleArgsVector.addElement(fullarg);
				}
			}
		}
		// convert arguments to be passed to console into a string array for the Console
		String[] consoleArgsArray = new String[consoleArgsVector.size()];
		Enumeration e = consoleArgsVector.elements();
		for (int i = 0; i < consoleArgsArray.length; i++) {
			consoleArgsArray[i] = (String) e.nextElement();
		}
		return consoleArgsArray;
	}

	public static boolean matchCommand(String command, String input, int minLength) {
		if (minLength <= 0) {
			minLength = command.length();
		}

		int length = input.length();

		if (minLength > length) {
			length = minLength;
		}

		return (command.regionMatches(0, input, 0, length));
	}

	/**
	 *  Remembers that the -console option has been requested.
	 */
	protected void _console(String command) {
		console = true;
		StringTokenizer tok = new StringTokenizer(command, ":"); //$NON-NLS-1$
		// first token is always "-console"
		String cmd = tok.nextToken();
		if (tok.hasMoreTokens()) {
			consolePort = tok.nextToken();
		}
	}

	/**
	 *  Remembers that the -adaptor option has been requested.  Parses off the adaptor class
	 *  file name, the adaptor file name, and the size if they are there.
	 *
	 * @param command The rest of the -adaptor parameter string that contains the class file name,
	 * and possibly the adaptor file and file size.
	 */
	protected void _adaptor(String command) {
		Tokenizer tok = new Tokenizer(command);
		// first token is always "-adaptor"
		String cmd = tok.getToken(":"); //$NON-NLS-1$
		tok.getChar(); // advance to next token
		// and next token is either adaptor class name or ":" if we should use the default adaptor
		String adp = tok.getToken(":"); //$NON-NLS-1$
		if (adp.length() > 0) {
			adaptorClassName = adp;
		}

		// The following tokens are arguments to be processed by the adaptor implementation class.
		// They may be enclosed in quotes.
		// Store them in a vector until we know how many there are.
		Vector v = new Vector();
		parseloop: while (true) {
			tok.getChar(); // advance to next token
			String arg = tok.getString(":"); //$NON-NLS-1$
			if (arg == null) {
				break parseloop;
			} else {
				v.addElement(arg);
			}
		}
		// now that we know how many args there are, move args from vector to String[]
		if (v != null) {
			int numArgs = v.size();
			adaptorArgs = new String[numArgs];
			Enumeration e = v.elements();
			for (int i = 0; i < numArgs; i++) {
				adaptorArgs[i] = (String) e.nextElement();
			}
		}
	}

	/**
	 *  Processes the -adaptor command line argument.
	 * 
	 *  Parses the arguments to get the adaptor class name, the adaptor dir or filename,
	 *  and the adaptor file size.
	 *
	 *  @return a FrameworkAdaptor object
	 */
	protected FrameworkAdaptor doAdaptor() throws Exception {

		Class adaptorClass = Class.forName(adaptorClassName);
		Class[] constructorArgs = new Class[] {String[].class};
		Constructor constructor = adaptorClass.getConstructor(constructorArgs);
		return (FrameworkAdaptor) constructor.newInstance(new Object[] {adaptorArgs});
	}

	/**
	 * Creates the OSGi framework object.
	 *
	 * @param adaptor The FrameworkAdaptor object
	 */
	protected OSGi doOSGi(FrameworkAdaptor adaptor) {
		return new OSGi(adaptor);
	}

	/**
	 *  Invokes the OSGi Console on another thread
	 *
	 * @param osgi The current OSGi instance for the console to attach to
	 * @param consoleArgs An String array containing commands from the command line
	 * for the console to execute
	 */
	protected void doConsole(OSGi osgi, String[] consoleArgs) {

		Constructor consoleConstructor;
		Object osgiconsole;
		Class[] parameterTypes;
		Object[] parameters;

		try {
			Class osgiconsoleClass = Class.forName(osgiConsoleClazz);
			if (consolePort.length() == 0) {
				parameterTypes = new Class[] {OSGi.class, String[].class};
				parameters = new Object[] {osgi, consoleArgs};
			} else {
				parameterTypes = new Class[] {OSGi.class, int.class, String[].class};
				parameters = new Object[] {osgi, new Integer(consolePort), consoleArgs};
			}
			consoleConstructor = osgiconsoleClass.getConstructor(parameterTypes);
			osgiconsole = consoleConstructor.newInstance(parameters);

			Thread t = new Thread(((Runnable) osgiconsole), OSGI_CONSOLE_COMPONENT_NAME);
			t.start();
		} catch (NumberFormatException nfe) {
			System.err.println(Msg.formatter.getString("LAUNCHER_INVALID_PORT", consolePort)); //$NON-NLS-1$
		} catch (Exception ex) {
			informAboutMissingComponent(OSGI_CONSOLE_COMPONENT_NAME, OSGI_CONSOLE_COMPONENT);
		}

	}

	/**
	 * Informs the user about a missing component.
	 *
	 * @param component The name of the component
	 * @param jar The jar file that contains the component
	 */
	void informAboutMissingComponent(String component, String jar) {
		System.out.println();
		System.out.print(Msg.formatter.getString("LAUNCHER_COMPONENT_MISSING", component)); //$NON-NLS-1$
		System.out.println(Msg.formatter.getString("LAUNCHER_COMPONENT_JAR", jar)); //$NON-NLS-1$
		System.out.println();
	}
}