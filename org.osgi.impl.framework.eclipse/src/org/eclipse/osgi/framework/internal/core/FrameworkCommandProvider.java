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

package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.*;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.osgi.framework.launcher.Launcher;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.RequiredBundle;

/**
 * This class provides methods to execute commands from the command line.  It registers
 * itself as a CommandProvider so it can be invoked by a CommandInterpreter.  The
 * FrameworkCommandProvider registers itself with the highest ranking (Integer.MAXVALUE) so it will always be
 * called first.  Other CommandProviders should register with lower rankings.
 *
 * The commands provided by this class are:
        ---Controlling the OSGi framework---
        close - shutdown and exit
        exit - exit immediately (System.exit)
        gc - perform a garbage collection
        init - uninstall all bundles
        launch - start the Service Management Framework
        setprop <key>=<value> - set the OSGI property
        shutdown - shutdown the Service Management Framework
        ---Controlliing Bundles---
        install <url> {s[tart]} - install and optionally start bundle from the given URL
        refresh (<id>|<location>) - refresh the packages of the specified bundles
        start (<id>|<location>) - start the specified bundle(s)
        stop (<id>|<location>) - stop the specified bundle(s)
        uninstall (<id>|<location>) - uninstall the specified bundle(s)
        update (<id>|<location>|<*>) - update the specified bundle(s)
        ---Displaying Status---
        bundle (<id>|<location>) - display details for the specified bundle(s)
        bundles - display details for all installed bundles
        headers (<id>|<location>) - print bundle headers
        packages {<pkgname>|<id>|<location>} - display imported/exported package details
        props - display System properties
        services {filter} - display registered service details
        ss - display installed bundles (short status)
        status - display installed bundles and registered services
        threads - display threads and thread groups
        ---Log Commands---
        log {(<id>|<location>)} - display log entries
        ---Extras---
        exec <command> - execute a command in a separate process and wait
        fork <command> - execute a command in a separate process
        ---Controlling StartLevel---
        sl {(<id>|<location>)} - display the start level for the specified bundle, or for the framework if no bundle specified
	    setfwsl <start level> - set the framework start level
	    setbsl <start level> (<id>|<location>) - set the start level for the bundle(s)
	    setibsl <start level> - set the initial bundle start level
	    
 *
 *  There is a method for each command which is named '_'+method.  The methods are
 *  invoked by a CommandInterpreter's execute method.
 */
public class FrameworkCommandProvider implements CommandProvider {

	/** An instance of the OSGi framework */
	private OSGi osgi;
	/** The system bundle context */
	private org.osgi.framework.BundleContext context;
	/** The start level implementation */
	private StartLevelManager slImpl;

	/** Strings used to format other strings */
	private String tab = "\t"; //$NON-NLS-1$
	private String newline = "\r\n"; //$NON-NLS-1$

	/**
	 *  Constructor.
	 *
	 *  It registers itself as a CommandProvider with the highest ranking possible.
	 *
	 *  @param osgi The current instance of OSGi
	 */
	public FrameworkCommandProvider(OSGi osgi) {
		this.osgi = osgi;
		context = osgi.getBundleContext();
		slImpl = osgi.framework.startLevelManager;
		Dictionary props = new Hashtable();
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		context.registerService(CommandProvider.class.getName(), this, props);
	}

	/**
	    Answer a string (may be as many lines as you like) with help
	    texts that explain the command.  This getHelp() method uses the 
	    ConsoleMsg class to obtain the correct NLS data to display to the user.
	
	    @return The help string
	*/
	public String getHelp() {
		StringBuffer help = new StringBuffer(1024);
		help.append(newline);
		help.append(ConsoleMsg.formatter.getString("CONSOLE_HELP_VALID_COMMANDS_HEADER")); //$NON-NLS-1$
		help.append(newline);
		addHeader("CONSOLE_HELP_CONTROLLING_FRAMEWORK_HEADER", help); //$NON-NLS-1$
		addCommand("launch", "CONSOLE_HELP_LAUNCH_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("shutdown", "CONSOLE_HELP_SHUTDOWN_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("close", "CONSOLE_HELP_CLOSE_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("exit", "CONSOLE_HELP_EXIT_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("gc", "CONSOLE_HELP_GC_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("init", "CONSOLE_HELP_INIT_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("setprop", "CONSOLE_HELP_KEYVALUE_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_SETPROP_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addHeader("CONSOLE_HELP_CONTROLLING_BUNDLES_HEADER", help); //$NON-NLS-1$
		addCommand("install", "CONSOLE_HELP_INSTALL_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("uninstall", "CONSOLE_HELP_UNINSTALL_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("start", "CONSOLE_HELP_START_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("stop", "CONSOLE_HELP_STOP_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("refresh", "CONSOLE_HELP_REFRESH_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("update", "CONSOLE_HELP_UPDATE_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addHeader("CONSOLE_HELP_DISPLAYING_STATUS_HEADER", help); //$NON-NLS-1$
		addCommand("status", "CONSOLE_HELP_STATUS_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("ss", "CONSOLE_HELP_SS_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("services", "CONSOLE_HELP_FILTER_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_SERVICES_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("packages", "CONSOLE_HELP_PACKAGES_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_PACKAGES_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("bundles", "CONSOLE_HELP_BUNDLES_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$
		addCommand("bundle", "CONSOLE_HELP_IDLOCATION_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_BUNDLE_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("headers", "CONSOLE_HELP_IDLOCATION_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_HEADERS_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("log", "CONSOLE_HELP_IDLOCATION_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_LOG_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addHeader("CONSOLE_HELP_EXTRAS_HEADER", help); //$NON-NLS-1$
		addCommand("exec", "CONSOLE_HELP_COMMAND_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_EXEC_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("fork", "CONSOLE_HELP_COMMAND_ARGUMENT_DESCRIPTION", "CONSOLE_HELP_FORK_COMMAND_DESCRIPTION", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addHeader("STARTLEVEL_HELP_HEADING", help); //$NON-NLS-1$
		addCommand("sl", "CONSOLE_HELP_OPTIONAL_IDLOCATION_ARGUMENT_DESCRIPTION", "STARTLEVEL_HELP_SL", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("setfwsl", "STARTLEVEL_ARGUMENT_DESCRIPTION", "STARTLEVEL_HELP_SETFWSL", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("setbsl", "STARTLEVEL_IDLOCATION_ARGUMENT_DESCRIPTION", "STARTLEVEL_HELP_SETBSL", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addCommand("setibsl", "STARTLEVEL_ARGUMENT_DESCRIPTION", "STARTLEVEL_HELP_SETIBSL", help); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return help.toString();
	}

	/** Private helper method for getHelp.  Formats the help headers. */
	private void addHeader(String header, StringBuffer help) {
		help.append("---"); //$NON-NLS-1$
		help.append(ConsoleMsg.formatter.getString(header));
		help.append("---"); //$NON-NLS-1$
		help.append(newline);
	}

	/** Private helper method for getHelp.  Formats the command descriptions. */
	private void addCommand(String command, String description, StringBuffer help) {
		help.append(tab);
		help.append(command);
		help.append(" - "); //$NON-NLS-1$
		help.append(ConsoleMsg.formatter.getString(description));
		help.append(newline);
	}

	/** Private helper method for getHelp.  Formats the command descriptions with command arguements. */
	private void addCommand(String command, String parameters, String description, StringBuffer help) {
		help.append(tab);
		help.append(command);
		help.append(" "); //$NON-NLS-1$
		help.append(ConsoleMsg.formatter.getString(parameters));
		help.append(" - "); //$NON-NLS-1$
		help.append(ConsoleMsg.formatter.getString(description));
		help.append(newline);
	}

	/**
	 *  Handle the exit command.  Exit immediately (System.exit)
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _exit(CommandInterpreter intp) throws Exception {
		intp.println();
		System.exit(0);
	}

	/**
	 *  Handle the launch command.  Start the OSGi framework.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _launch(CommandInterpreter intp) throws Exception {
		osgi.launch();
	}

	/**
	 *  Handle the shutdown command.  Shutdown the OSGi framework.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _shutdown(CommandInterpreter intp) throws Exception {
		osgi.shutdown();
	}

	/**
	 *  Handle the start command's abbreviation.  Invoke _start()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _sta(CommandInterpreter intp) throws Exception {
		_start(intp);
	}

	/**
	 *  Handle the start command.  Start the specified bundle(s).
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _start(CommandInterpreter intp) throws Exception {
		String nextArg = intp.nextArgument();
		if (nextArg == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLE_SPECIFIED_ERROR")); //$NON-NLS-1$
		}
		while (nextArg != null) {
			AbstractBundle bundle = getBundleFromToken(intp, nextArg, true);
			if (bundle != null) {
				bundle.start();
			}
			nextArg = intp.nextArgument();
		}
	}

	/**
	 *  Handle the stop command's abbreviation.  Invoke _stop()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _sto(CommandInterpreter intp) throws Exception {
		_stop(intp);
	}

	/**
	 *  Handle the stop command.  Stop the specified bundle(s).
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _stop(CommandInterpreter intp) throws Exception {
		String nextArg = intp.nextArgument();
		if (nextArg == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLE_SPECIFIED_ERROR")); //$NON-NLS-1$
		}
		while (nextArg != null) {
			AbstractBundle bundle = getBundleFromToken(intp, nextArg, true);
			if (bundle != null) {
				bundle.stop();
			}
			nextArg = intp.nextArgument();
		}
	}

	/**
	 *  Handle the install command's abbreviation.  Invoke _install()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _i(CommandInterpreter intp) throws Exception {
		_install(intp);
	}

	/**
	 *  Handle the install command.  Install and optionally start bundle from the given URL\r\n"
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _install(CommandInterpreter intp) throws Exception {
		String url = intp.nextArgument();
		if (url == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NOTHING_TO_INSTALL_ERROR")); //$NON-NLS-1$
		} else {
			AbstractBundle bundle = (AbstractBundle) context.installBundle(url);
			intp.print(ConsoleMsg.formatter.getString("CONSOLE_BUNDLE_ID_MESSAGE")); //$NON-NLS-1$
			intp.println(new Long(bundle.getBundleId()));

			String nextArg = intp.nextArgument();
			if (nextArg != null) {
				String start = nextArg.toLowerCase();

				if (Launcher.matchCommand("start", start, 1)) { //$NON-NLS-1$
					bundle.start();
				}
			}
		}

	}

	/**
	 *  Handle the update command's abbreviation.  Invoke _update()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _up(CommandInterpreter intp) throws Exception {
		_update(intp);
	}

	/**
	 *  Handle the update command.  Update the specified bundle(s).
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _update(CommandInterpreter intp) throws Exception {
		String token = intp.nextArgument();
		if (token == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLE_SPECIFIED_ERROR")); //$NON-NLS-1$
		}
		while (token != null) {

			if ("*".equals(token)) { //$NON-NLS-1$
				AbstractBundle[] bundles = (AbstractBundle[]) context.getBundles();

				int size = bundles.length;

				if (size > 0) {
					for (int i = 0; i < size; i++) {
						AbstractBundle bundle = bundles[i];

						if (bundle.getBundleId() != 0) {
							try {
								bundle.update();
							} catch (BundleException e) {
								intp.printStackTrace(e);
							}
						}
					}
				} else {
					intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_INSTALLED_BUNDLES_ERROR")); //$NON-NLS-1$
				}
			} else {
				AbstractBundle bundle = getBundleFromToken(intp, token, true);
				if (bundle != null) {
					String source = intp.nextArgument();
					try {
						if (source != null) {
							bundle.update(new URL(source).openStream());
						} else {
							bundle.update();
						}
					} catch (BundleException e) {
						intp.printStackTrace(e);
					}
				}
			}
			token = intp.nextArgument();
		}
	}

	/**
	 *  Handle the uninstall command's abbreviation.  Invoke _uninstall()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _un(CommandInterpreter intp) throws Exception {
		_uninstall(intp);
	}

	/**
	 *  Handle the uninstall command.  Uninstall the specified bundle(s).
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _uninstall(CommandInterpreter intp) throws Exception {
		String nextArg = intp.nextArgument();
		if (nextArg == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLE_SPECIFIED_ERROR")); //$NON-NLS-1$
		}
		while (nextArg != null) {
			AbstractBundle bundle = getBundleFromToken(intp, nextArg, true);
			if (bundle != null) {
				bundle.uninstall();
			}
			nextArg = intp.nextArgument();
		}
	}

	/**
	 *  Handle the status command's abbreviation.  Invoke _status()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _s(CommandInterpreter intp) throws Exception {
		_status(intp);
	}

	/**
	 *  Handle the status command.  Display installed bundles and registered services.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _status(CommandInterpreter intp) throws Exception {
		if (osgi.isActive()) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_FRAMEWORK_IS_LAUNCHED_MESSAGE")); //$NON-NLS-1$
		} else {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_FRAMEWORK_IS_SHUTDOWN_MESSAGE")); //$NON-NLS-1$
		}
		intp.println();

		AbstractBundle[] bundles = (AbstractBundle[]) context.getBundles();
		int size = bundles.length;

		if (size == 0) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_INSTALLED_BUNDLES_ERROR")); //$NON-NLS-1$
			return;
		}
		intp.print(ConsoleMsg.formatter.getString("CONSOLE_ID")); //$NON-NLS-1$
		intp.print(tab);
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_BUNDLE_LOCATION_MESSAGE")); //$NON-NLS-1$
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_STATE_BUNDLE_FILE_NAME_HEADER")); //$NON-NLS-1$
		for (int i = 0; i < size; i++) {
			AbstractBundle bundle = bundles[i];
			intp.print(new Long(bundle.getBundleId()));
			intp.print(tab);
			intp.println(bundle.getLocation());
			intp.print("  "); //$NON-NLS-1$
			intp.print(getStateName(bundle.getState()));
			intp.println(bundle.bundledata);
		}

		ServiceReference[] services = (ServiceReference[]) context.getServiceReferences(null, null);
		if (services != null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_REGISTERED_SERVICES_MESSAGE")); //$NON-NLS-1$
			size = services.length;
			for (int i = 0; i < size; i++) {
				intp.println(services[i]);
			}
		}
	}

	/**
	 *  Handle the services command's abbreviation.  Invoke _services()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _se(CommandInterpreter intp) throws Exception {
		_services(intp);
	}

	/**
	 *  Handle the services command.  Display registered service details.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _services(CommandInterpreter intp) throws Exception {
		String filter = null;

		String nextArg = intp.nextArgument();
		if (nextArg != null) {
			StringBuffer buf = new StringBuffer();
			while (nextArg != null) {
				buf.append(' ');
				buf.append(nextArg);
				nextArg = intp.nextArgument();
			}
			filter = buf.toString();
		}

		ServiceReference[] services = (ServiceReference[]) context.getServiceReferences(null, filter);
		if (services != null) {
			int size = services.length;
			if (size > 0) {
				for (int j = 0; j < size; j++) {
					ServiceReference service = services[j];
					intp.println(service);
					intp.print("  "); //$NON-NLS-1$
					intp.print(ConsoleMsg.formatter.getString("CONSOLE_REGISTERED_BY_BUNDLE_MESSAGE")); //$NON-NLS-1$
					intp.print(" "); //$NON-NLS-1$
					intp.println(service.getBundle());
					AbstractBundle[] users = (AbstractBundle[]) service.getUsingBundles();
					if (users != null) {
						intp.print("  "); //$NON-NLS-1$
						intp.println(ConsoleMsg.formatter.getString("CONSOLE_BUNDLES_USING_SERVICE_MESSAGE")); //$NON-NLS-1$
						for (int k = 0; k < users.length; k++) {
							intp.print("    "); //$NON-NLS-1$
							intp.println(users[k]);
						}
					} else {
						intp.print("  "); //$NON-NLS-1$
						intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLES_USING_SERVICE_MESSAGE")); //$NON-NLS-1$
					}
				}
				return;
			}
		}
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_REGISTERED_SERVICES_MESSAGE")); //$NON-NLS-1$
	}

	/**
	 *  Handle the packages command's abbreviation.  Invoke _packages()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _p(CommandInterpreter intp) throws Exception {
		_packages(intp);
	}

	/**
	 *  Handle the packages command.  Display imported/exported package details.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _packages(CommandInterpreter intp) throws Exception {
		org.osgi.framework.Bundle bundle = null;

		String token = intp.nextArgument();
		if (token != null) {
			bundle = getBundleFromToken(intp, token, false);
		}

		org.osgi.framework.ServiceReference packageAdminRef = context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					org.osgi.service.packageadmin.ExportedPackage[] packages = null;

					if ((token != null) && (bundle == null)) {
						org.osgi.service.packageadmin.ExportedPackage pkg = packageAdmin.getExportedPackage(token);

						if (pkg != null) {
							packages = new org.osgi.service.packageadmin.ExportedPackage[] { pkg };
						}
					} else {
						packages = packageAdmin.getExportedPackages(bundle);
					}

					if (packages == null) {
						intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_EXPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
					} else {
						for (int i = 0; i < packages.length; i++) {
							org.osgi.service.packageadmin.ExportedPackage pkg = packages[i];
							intp.print(pkg);

							boolean removalPending = pkg.isRemovalPending();
							if (removalPending) {
								intp.print("("); //$NON-NLS-1$
								intp.print(ConsoleMsg.formatter.getString("CONSOLE_REMOVAL_PENDING_MESSAGE")); //$NON-NLS-1$
								intp.println(")"); //$NON-NLS-1$
							}

							org.osgi.framework.Bundle exporter = pkg.getExportingBundle();
							if (exporter != null) {
								intp.print("<"); //$NON-NLS-1$
								intp.print(exporter);
								intp.println(">"); //$NON-NLS-1$

								org.osgi.framework.Bundle[] importers = pkg.getImportingBundles();
								for (int j = 0; j < importers.length; j++) {
									intp.print("  "); //$NON-NLS-1$
									intp.print(importers[j]);
									intp.print(" "); //$NON-NLS-1$
									intp.println(ConsoleMsg.formatter.getString("CONSOLE_IMPORTS_MESSAGE")); //$NON-NLS-1$
								}
							} else {
								intp.print("<"); //$NON-NLS-1$
								intp.print(ConsoleMsg.formatter.getString("CONSOLE_STALE_MESSAGE")); //$NON-NLS-1$
								intp.println(">"); //$NON-NLS-1$
							}

						}
					}
				} finally {
					context.ungetService(packageAdminRef);
				}
			}
		} else {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_EXPORTED_PACKAGES_NO_PACKAGE_ADMIN_MESSAGE")); //$NON-NLS-1$
		}
	}

	/**
	 *  Handle the bundles command.  Display details for all installed bundles.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _bundles(CommandInterpreter intp) throws Exception {
		AbstractBundle[] bundles = (AbstractBundle[]) context.getBundles();
		int size = bundles.length;

		if (size == 0) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_INSTALLED_BUNDLES_ERROR")); //$NON-NLS-1$
			return;
		}

		for (int i = 0; i < size; i++) {
			AbstractBundle bundle = bundles[i];
			long id = bundle.getBundleId();
			intp.println(bundle);
			intp.print("  "); //$NON-NLS-1$
			intp.print(ConsoleMsg.formatter.getString("CONSOLE_ID_MESSAGE", String.valueOf(id))); //$NON-NLS-1$
			intp.print(", "); //$NON-NLS-1$
			intp.print(ConsoleMsg.formatter.getString("CONSOLE_STATUS_MESSAGE", getStateName(bundle.getState()))); //$NON-NLS-1$
			if (id != 0) {
				File dataRoot = osgi.framework.getDataFile(bundle, ""); //$NON-NLS-1$

				String root = (dataRoot == null) ? null : dataRoot.getAbsolutePath();

				intp.print(ConsoleMsg.formatter.getString("CONSOLE_DATA_ROOT_MESSAGE", root)); //$NON-NLS-1$
			} else {
				intp.println();
			}

			ServiceReference[] services = (ServiceReference[]) bundle.getRegisteredServices();
			if (services != null) {
				intp.print("  "); //$NON-NLS-1$
				intp.println(ConsoleMsg.formatter.getString("CONSOLE_REGISTERED_SERVICES_MESSAGE")); //$NON-NLS-1$
				for (int j = 0; j < services.length; j++) {
					intp.print("    "); //$NON-NLS-1$
					intp.println(services[j]);
				}
			} else {
				intp.print("  "); //$NON-NLS-1$
				intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_REGISTERED_SERVICES_MESSAGE")); //$NON-NLS-1$
			}

			services = (ServiceReference[]) bundle.getServicesInUse();
			if (services != null) {
				intp.print("  "); //$NON-NLS-1$
				intp.println(ConsoleMsg.formatter.getString("CONSOLE_SERVICES_IN_USE_MESSAGE")); //$NON-NLS-1$
				for (int j = 0; j < services.length; j++) {
					intp.print("    "); //$NON-NLS-1$
					intp.println(services[j]);
				}
			} else {
				intp.print("  "); //$NON-NLS-1$
				intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_SERVICES_IN_USE_MESSAGE")); //$NON-NLS-1$
			}
		}
	}

	/**
	 *  Handle the bundle command's abbreviation.  Invoke _bundle()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _b(CommandInterpreter intp) throws Exception {
		_bundle(intp);
	}

	/**
	 *  Handle the bundle command.  Display details for the specified bundle(s).
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _bundle(CommandInterpreter intp) throws Exception {
		String nextArg = intp.nextArgument();
		if (nextArg == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLE_SPECIFIED_ERROR")); //$NON-NLS-1$
		}
		while (nextArg != null) {
			AbstractBundle bundle = getBundleFromToken(intp, nextArg, true);
			if (bundle != null) {
				long id = bundle.getBundleId();
				intp.println(bundle);
				intp.print("  "); //$NON-NLS-1$
				intp.print(ConsoleMsg.formatter.getString("CONSOLE_ID_MESSAGE", String.valueOf(id))); //$NON-NLS-1$
				intp.print(", "); //$NON-NLS-1$
				intp.print(ConsoleMsg.formatter.getString("CONSOLE_STATUS_MESSAGE", getStateName(bundle.getState()))); //$NON-NLS-1$
				if (id != 0) {
					File dataRoot = osgi.framework.getDataFile(bundle, ""); //$NON-NLS-1$

					String root = (dataRoot == null) ? null : dataRoot.getAbsolutePath();

					intp.print(ConsoleMsg.formatter.getString("CONSOLE_DATA_ROOT_MESSAGE", root)); //$NON-NLS-1$
				} else {
					intp.println();
				}

				ServiceReference[] services = (ServiceReference[]) bundle.getRegisteredServices();
				if (services != null) {
					intp.print("  "); //$NON-NLS-1$
					intp.println(ConsoleMsg.formatter.getString("CONSOLE_REGISTERED_SERVICES_MESSAGE")); //$NON-NLS-1$
					for (int j = 0; j < services.length; j++) {
						intp.print("    "); //$NON-NLS-1$
						intp.println(services[j]);
					}
				} else {
					intp.print("  "); //$NON-NLS-1$
					intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_REGISTERED_SERVICES_MESSAGE")); //$NON-NLS-1$
				}

				services = (ServiceReference[]) bundle.getServicesInUse();
				if (services != null) {
					intp.print("  "); //$NON-NLS-1$
					intp.println(ConsoleMsg.formatter.getString("CONSOLE_SERVICES_IN_USE_MESSAGE")); //$NON-NLS-1$
					for (int j = 0; j < services.length; j++) {
						intp.print("    "); //$NON-NLS-1$
						intp.println(services[j]);
					}
				} else {
					intp.print("  "); //$NON-NLS-1$
					intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_SERVICES_IN_USE_MESSAGE")); //$NON-NLS-1$
				}

				org.osgi.framework.ServiceReference packageAdminRef = context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
				if (packageAdminRef != null) {
					org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context.getService(packageAdminRef);
					if (packageAdmin != null) {
						try {
							org.osgi.service.packageadmin.ExportedPackage exportedpkgs[] = packageAdmin.getExportedPackages((org.osgi.framework.Bundle)null);

							if (exportedpkgs == null) {
								intp.print("  "); //$NON-NLS-1$
								intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_EXPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
								intp.print("  "); //$NON-NLS-1$
								intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_IMPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
							} else {
								boolean title = true;

								for (int i = 0; i < exportedpkgs.length; i++) {
									org.osgi.service.packageadmin.ExportedPackage exportedpkg = exportedpkgs[i];

									if (exportedpkg.getExportingBundle() == bundle) {
										if (title) {
											intp.print("  "); //$NON-NLS-1$
											intp.println(ConsoleMsg.formatter.getString("CONSOLE_EXPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
											title = false;
										}
										intp.print("    "); //$NON-NLS-1$
										intp.print(exportedpkg);
										if (exportedpkg.isRemovalPending()) {
											intp.println(ConsoleMsg.formatter.getString("CONSOLE_EXPORTED_REMOVAL_PENDING_MESSAGE")); //$NON-NLS-1$
										} else {
											intp.println(ConsoleMsg.formatter.getString("CONSOLE_EXPORTED_MESSAGE")); //$NON-NLS-1$
										}
									}
								}

								if (title) {
									intp.print("  "); //$NON-NLS-1$
									intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_EXPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
								}

								title = true;

								for (int i = 0; i < exportedpkgs.length; i++) {
									org.osgi.service.packageadmin.ExportedPackage exportedpkg = exportedpkgs[i];

									org.osgi.framework.Bundle[] importers = exportedpkg.getImportingBundles();
									for (int j = 0; j < importers.length; j++) {
										if (importers[j] == bundle) {
											if (title) {
												intp.print("  "); //$NON-NLS-1$
												intp.println(ConsoleMsg.formatter.getString("CONSOLE_IMPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
												title = false;
											}
											intp.print("    "); //$NON-NLS-1$
											intp.print(exportedpkg);
											org.osgi.framework.Bundle exporter = exportedpkg.getExportingBundle();
											if (exporter != null) {
												intp.print("<"); //$NON-NLS-1$
												intp.print(exporter);
												intp.println(">"); //$NON-NLS-1$
											} else {
												intp.print("<"); //$NON-NLS-1$
												intp.print(ConsoleMsg.formatter.getString("CONSOLE_STALE_MESSAGE")); //$NON-NLS-1$
												intp.println(">"); //$NON-NLS-1$
											}

											break;
										}
									}
								}

								if (title) {
									intp.print("  "); //$NON-NLS-1$
									intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_IMPORTED_PACKAGES_MESSAGE")); //$NON-NLS-1$
								}

								intp.print("  "); //$NON-NLS-1$
                    			if ((packageAdmin.getBundleType(bundle) & PackageAdminImpl.BUNDLE_TYPE_FRAGMENT) > 0) {
                    				org.osgi.framework.Bundle[] hosts = packageAdmin.getHosts(bundle);
                    				if (hosts != null) {
                    					intp.println(ConsoleMsg.formatter.getString("CONSOLE_HOST_MESSAGE")); //$NON-NLS-1$
                    					for (int i=0; i<hosts.length; i++) {
                    						intp.print("    "); //$NON-NLS-1$
                    						intp.println(hosts[i]);
                    					}
                    				}
                    				else {
                    					intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_HOST_MESSAGE")); //$NON-NLS-1$
                    				}
                    			}
                    			else {
                    				org.osgi.framework.Bundle[] fragments = packageAdmin.getFragments(bundle);
                    				if (fragments != null) {
                    					intp.println(ConsoleMsg.formatter.getString("CONSOLE_FRAGMENT_MESSAGE")); //$NON-NLS-1$
                    					for (int i=0; i<fragments.length; i++) {
                    						intp.print("    "); //$NON-NLS-1$
                    						intp.println(fragments[i]);
                    					}
                    				}
                    				else {
                    					intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_FRAGMENT_MESSAGE")); //$NON-NLS-1$
                    				}
                    			}

                    			RequiredBundle[] requiredBundles = packageAdmin.getRequiredBundles(null);
								RequiredBundle requiredBundle = null;
								if (requiredBundles != null) {
									for (int i=0; i<requiredBundles.length; i++) {
										if (requiredBundles[i].getBundle() == bundle) {
											requiredBundle = requiredBundles[i];
											break;
										}
									}
								}

								if (requiredBundle == null) {
									intp.print("  "); //$NON-NLS-1$
									intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_NAMED_CLASS_SPACES_MESSAGE")); //$NON-NLS-1$
								} else {
									intp.print("  "); //$NON-NLS-1$
									intp.println(ConsoleMsg.formatter.getString("CONSOLE_NAMED_CLASS_SPACE_MESSAGE")); //$NON-NLS-1$
	                    			intp.print("    "); //$NON-NLS-1$
	                    			intp.print(requiredBundle);
	                    			if (requiredBundle.isRemovalPending()) {
	                    				intp.println(ConsoleMsg.formatter.getString("CONSOLE_REMOVAL_PENDING_MESSAGE")); //$NON-NLS-1$
	                    			} else {
	                    				intp.println(ConsoleMsg.formatter.getString("CONSOLE_PROVIDED_MESSAGE")); //$NON-NLS-1$
	                    			}
								}

								title = true;
								for(int i=0; i<requiredBundles.length; i++) {
									if (requiredBundles[i] == requiredBundle)
										continue;

									org.osgi.framework.Bundle[] depBundles =  requiredBundles[i].getRequiringBundles();
									if (depBundles == null)
										continue;

									for (int j=0; j<depBundles.length; j++) {
										if (depBundles[j] == bundle) {
											if (title) {
												intp.print("  "); //$NON-NLS-1$
												intp.println(ConsoleMsg.formatter.getString("CONSOLE_REQUIRED_BUNDLES_MESSAGE")); //$NON-NLS-1$
												title = false;
											}
											intp.print("    "); //$NON-NLS-1$
			                    			intp.print(requiredBundles[i]);

			                    			org.osgi.framework.Bundle provider = requiredBundles[i].getBundle();
											intp.print("<"); //$NON-NLS-1$
											intp.print(provider);
											intp.println(">"); //$NON-NLS-1$
										}
									}
								}
								if (title) {
									intp.print("  "); //$NON-NLS-1$
									intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_REQUIRED_BUNDLES_MESSAGE")); //$NON-NLS-1$
								}

							}
						} finally {
							context.ungetService(packageAdminRef);
						}
					}
				} else {
					intp.print("  "); //$NON-NLS-1$
					intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_EXPORTED_PACKAGES_NO_PACKAGE_ADMIN_MESSAGE")); //$NON-NLS-1$
				}

				SecurityManager sm = System.getSecurityManager();
				if (sm != null) {
					ProtectionDomain domain = bundle.getProtectionDomain();

					intp.println(domain);
				}
			}
			nextArg = intp.nextArgument();
		}
	}

	/**
	 *  Handle the log command's abbreviation.  Invoke _log()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _l(CommandInterpreter intp) throws Exception {
		_log(intp);
	}

	/**
	 *  Handle the log command.  Display log entries.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _log(CommandInterpreter intp) throws Exception {
		long logid = -1;
		String token = intp.nextArgument();
		if (token != null) {
			AbstractBundle bundle = getBundleFromToken(intp, token, false);

			if (bundle == null) {
				try {
					logid = Long.parseLong(token);
				} catch (NumberFormatException e) {
					return;
				}
			} else {
				logid = bundle.getBundleId();
			}
		}

		org.osgi.framework.ServiceReference logreaderRef = context.getServiceReference("org.osgi.service.log.LogReaderService"); //$NON-NLS-1$
		if (logreaderRef != null) {
			Object logreader = context.getService(logreaderRef);
			if (logreader != null) {
				try {
					Enumeration logentries = (Enumeration) (logreader.getClass().getMethod("getLog", null).invoke(logreader, null)); //$NON-NLS-1$

					if (logentries.hasMoreElements()) {
						Object logentry = logentries.nextElement();
						Class clazz = logentry.getClass();
						Method getBundle = clazz.getMethod("getBundle", null); //$NON-NLS-1$
						Method getLevel = clazz.getMethod("getLevel", null); //$NON-NLS-1$
						Method getMessage = clazz.getMethod("getMessage", null); //$NON-NLS-1$
						Method getServiceReference = clazz.getMethod("getServiceReference", null); //$NON-NLS-1$
						Method getException = clazz.getMethod("getException", null); //$NON-NLS-1$

						while (true) {
							AbstractBundle bundle = (AbstractBundle) getBundle.invoke(logentry, null);

							if ((logid == -1) || ((bundle != null) && (logid == bundle.getBundleId()))) {
								Integer level = (Integer) getLevel.invoke(logentry, null);
								switch (level.intValue()) {
									case 4 :
										intp.print(">"); //$NON-NLS-1$
										intp.print(ConsoleMsg.formatter.getString("CONSOLE_DEBUG_MESSAGE")); //$NON-NLS-1$
										intp.print(" "); //$NON-NLS-1$
										break;
									case 3 :
										intp.print(">"); //$NON-NLS-1$
										intp.print(ConsoleMsg.formatter.getString("CONSOLE_INFO_MESSAGE")); //$NON-NLS-1$
										intp.print(" "); //$NON-NLS-1$
										break;
									case 2 :
										intp.print(">"); //$NON-NLS-1$
										intp.print(ConsoleMsg.formatter.getString("CONSOLE_WARNING_MESSAGE")); //$NON-NLS-1$
										intp.print(" "); //$NON-NLS-1$
										break;
									case 1 :
										intp.print(">"); //$NON-NLS-1$
										intp.print(ConsoleMsg.formatter.getString("CONSOLE_ERROR_MESSAGE")); //$NON-NLS-1$
										intp.print(" "); //$NON-NLS-1$
										break;
									default :
										intp.print(">"); //$NON-NLS-1$
										intp.print(level);
										intp.print(" "); //$NON-NLS-1$
										break;
								}

								if (bundle != null) {
									intp.print("["); //$NON-NLS-1$
									intp.print(new Long(bundle.getBundleId()));
									intp.print("] "); //$NON-NLS-1$
								}

								intp.print(getMessage.invoke(logentry, null));
								intp.print(" "); //$NON-NLS-1$

								ServiceReferenceImpl svcref = (ServiceReferenceImpl) getServiceReference.invoke(logentry, null);
								if (svcref != null) {
									intp.print("{"); //$NON-NLS-1$
									intp.print(Constants.SERVICE_ID);
									intp.print("="); //$NON-NLS-1$
									intp.print(svcref.getProperty(Constants.SERVICE_ID).toString());
									intp.println("}"); //$NON-NLS-1$
								} else {
									if (bundle != null) {
										intp.println(bundle.getLocation());
									} else {
										intp.println();
									}
								}

								Throwable t = (Throwable) getException.invoke(logentry, null);
								if (t != null) {
									intp.printStackTrace(t);
								}
							}

							if (logentries.hasMoreElements()) {
								logentry = logentries.nextElement();
							} else {
								break;
							}
						}
					}
				} finally {
					context.ungetService(logreaderRef);
				}
				return;
			}
		}

		intp.println(ConsoleMsg.formatter.getString("CONSOLE_LOGSERVICE_NOT_REGISTERED_MESSAGE")); //$NON-NLS-1$
	}

	/**
	 *  Handle the gc command.  Perform a garbage collection.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _gc(CommandInterpreter intp) throws Exception {
		long before = Runtime.getRuntime().freeMemory();

		/* Let the finilizer finish its work and remove objects from its queue */
		System.gc(); /* asyncronous garbage collector might already run */
		System.gc(); /* to make sure it does a full gc call it twice */
		System.runFinalization();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		long after = Runtime.getRuntime().freeMemory();
		intp.print(ConsoleMsg.formatter.getString("CONSOLE_TOTAL_MEMORY_MESSAGE")); //$NON-NLS-1$
		intp.println(String.valueOf(Runtime.getRuntime().totalMemory()));
		intp.print(ConsoleMsg.formatter.getString("CONSOLE_FREE_MEMORY_BEFORE_GARBAGE_COLLECTION_MESSAGE")); //$NON-NLS-1$
		intp.println(String.valueOf(before));
		intp.print(ConsoleMsg.formatter.getString("CONSOLE_FREE_MEMORY_AFTER_GARBAGE_COLLECTION_MESSAGE")); //$NON-NLS-1$
		intp.println(String.valueOf(after));
		intp.print(ConsoleMsg.formatter.getString("CONSOLE_MEMORY_GAINED_WITH_GARBAGE_COLLECTION_MESSAGE")); //$NON-NLS-1$
		intp.println(String.valueOf(after - before));
	}

	/**
	 *  Handle the init command.  Uninstall all bundles.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _init(CommandInterpreter intp) throws Exception {
		if (osgi.isActive()) {
			intp.print(newline);
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_FRAMEWORK_LAUNCHED_PLEASE_SHUTDOWN_MESSAGE")); //$NON-NLS-1$
			return;
		}

		AbstractBundle[] bundles = (AbstractBundle[]) context.getBundles();

		int size = bundles.length;

		if (size > 0) {
			for (int i = 0; i < size; i++) {
				AbstractBundle bundle = bundles[i];

				if (bundle.getBundleId() != 0) {
					try {
						bundle.uninstall();
					} catch (BundleException e) {
						intp.printStackTrace(e);
					}
				}
			}
		} else {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_INSTALLED_BUNDLES_ERROR")); //$NON-NLS-1$
		}
	}

	/**
	 *  Handle the close command.  Shutdown and exit.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _close(CommandInterpreter intp) throws Exception {
		intp.println();
		osgi.close();
		System.exit(0);
	}

	/**
	 *  Handle the refresh command's abbreviation.  Invoke _refresh()
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _r(CommandInterpreter intp) throws Exception {
		_refresh(intp);
	}

	/**
	 *  Handle the refresh command.  Refresh the packages of the specified bundles.
	 *
	 *  @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _refresh(CommandInterpreter intp) throws Exception {
		org.osgi.framework.ServiceReference packageAdminRef = context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					AbstractBundle[] refresh = null;

					String token = intp.nextArgument();
					if (token != null) {
						Vector bundles = new Vector();

						while (token != null) {
							AbstractBundle bundle = getBundleFromToken(intp, token, true);

							if (bundle != null) {
								bundles.addElement(bundle);
							}
							token = intp.nextArgument();
						}

						int size = bundles.size();

						if (size == 0) {
							intp.println(ConsoleMsg.formatter.getString("CONSOLE_INVALID_BUNDLE_SPECIFICATION_ERROR")); //$NON-NLS-1$
							return;
						}

						refresh = new AbstractBundle[size];
						bundles.copyInto(refresh);
					}

					packageAdmin.refreshPackages(refresh);
				} finally {
					context.ungetService(packageAdminRef);
				}
			}
		} else {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_CAN_NOT_REFRESH_NO_PACKAGE_ADMIN_ERROR")); //$NON-NLS-1$
		}
	}

	/**
	 * Executes the given system command in a separate system process
	 * and waits for it to finish.
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _exec(CommandInterpreter intp) throws Exception {
		String command = intp.nextArgument();
		if (command == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_COMMAND_SPECIFIED_ERROR")); //$NON-NLS-1$
			return;
		}

		Process p = Runtime.getRuntime().exec(command);

		intp.println(ConsoleMsg.formatter.getString("CONSOLE_STARTED_IN_MESSAGE", command, String.valueOf(p))); //$NON-NLS-1$
		int result = p.waitFor();
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_EXECUTED_RESULT_CODE_MESSAGE", command, String.valueOf(result))); //$NON-NLS-1$
	}

	/**
	 * Executes the given system command in a separate system process.  It does
	 * not wait for a result.
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _fork(CommandInterpreter intp) throws Exception {
		String command = intp.nextArgument();
		if (command == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_COMMAND_SPECIFIED_ERROR")); //$NON-NLS-1$
			return;
		}

		Process p = Runtime.getRuntime().exec(command);
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_STARTED_IN_MESSAGE", command, String.valueOf(p))); //$NON-NLS-1$
	}

	/**
	 * Handle the headers command's abbreviation.  Invoke _headers()
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _h(CommandInterpreter intp) throws Exception {
		_headers(intp);
	}

	/**
	 * Handle the headers command.  Display headers for the specified bundle(s).
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _headers(CommandInterpreter intp) throws Exception {

		String nextArg = intp.nextArgument();
		if (nextArg == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_BUNDLE_SPECIFIED_ERROR")); //$NON-NLS-1$
		}
		while (nextArg != null) {
			AbstractBundle bundle = getBundleFromToken(intp, nextArg, true);
			if (bundle != null) {
				intp.printDictionary(bundle.getHeaders(), ConsoleMsg.formatter.getString("CONSOLE_BUNDLE_HEADERS_TITLE")); //$NON-NLS-1$
			}
			nextArg = intp.nextArgument();
		}
	}

	/**
	 * Handles the props command's abbreviation.  Invokes _props()
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _pr(CommandInterpreter intp) throws Exception {
		_props(intp);
	}

	/**
	 * Handles the _props command.  Prints the system properties sorted.
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _props(CommandInterpreter intp) throws Exception {
		intp.printDictionary(System.getProperties(), ConsoleMsg.formatter.getString("CONSOLE_SYSTEM_PROPERTIES_TITLE")); //$NON-NLS-1$
	}

	/**
	 * Handles the setprop command's abbreviation.  Invokes _setprop()
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _setp(CommandInterpreter intp) throws Exception {
		_setprop(intp);
	}

	/**
	 * Handles the setprop command.  Sets the CDS property in the given argument.
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _setprop(CommandInterpreter intp) throws Exception {
		String argument = intp.nextArgument();
		if (argument == null) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_PARAMETERS_SPECIFIED_TITLE")); //$NON-NLS-1$
			_props(intp);
		} else {
			InputStream in = new ByteArrayInputStream(argument.getBytes());
			try {
				Properties sysprops = System.getProperties();
				Properties newprops = new Properties();
				newprops.load(in);
				intp.println(ConsoleMsg.formatter.getString("CONSOLE_SETTING_PROPERTIES_TITLE")); //$NON-NLS-1$
				Enumeration keys = newprops.propertyNames();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					String value = (String) newprops.get(key);
					sysprops.put(key, value);
					intp.println(tab + key + " = " + value); //$NON-NLS-1$
				}
			} catch (IOException e) {
			} finally {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Prints the short version of the status.
	 * For the long version use "status".
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _ss(CommandInterpreter intp) throws Exception {
		if (osgi.isActive()) {
			intp.println();
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_FRAMEWORK_IS_LAUNCHED_MESSAGE")); //$NON-NLS-1$
		} else {
			intp.println();
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_FRAMEWORK_IS_SHUTDOWN_MESSAGE")); //$NON-NLS-1$
		}

		AbstractBundle[] bundles = (AbstractBundle[]) context.getBundles();
		if (bundles.length == 0) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_INSTALLED_BUNDLES_ERROR")); //$NON-NLS-1$
		} else {
			intp.print(newline);
			intp.print(ConsoleMsg.formatter.getString("CONSOLE_ID")); //$NON-NLS-1$
			intp.print(tab);
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_STATE_BUNDLE_TITLE")); //$NON-NLS-1$
			for (int i = 0; i < bundles.length; i++) {
				AbstractBundle b = bundles[i];
				String label = b.getSymbolicName();
				if (label == null || label.length() == 0)
					label = b.toString();
				else
					label = label + "_" + b.getVersion(); //$NON-NLS-1$
				intp.println(b.getBundleId() + "\t" + getStateName(b.getState()) + label); //$NON-NLS-1$ 
				if (b.isFragment()) {
					BundleLoaderProxy[] hosts = b.getHosts();
					if (hosts != null)
						for (int j = 0; j < hosts.length; j++)
							intp.println("\t            Master=" + hosts[j].getBundleHost().getBundleId()); //$NON-NLS-1$
				} else {
					org.osgi.framework.Bundle fragments[] = b.getFragments();
					if (fragments != null) {
						intp.print("\t            Fragments="); //$NON-NLS-1$
						for (int f = 0; f < fragments.length; f++) {
							AbstractBundle fragment = (AbstractBundle) fragments[f];
							intp.print((f > 0 ? ", " :"") + fragment.getBundleId()); //$NON-NLS-1$ //$NON-NLS-2$
						}
						intp.println();
					}
				}
			}
		}
	}

	/**
	 * Handles the threads command abbreviation.  Invokes _threads().
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _t(CommandInterpreter intp) throws Exception {
		_threads(intp);
	}

	/**
	 * Prints the information about the currently running threads
	 * in the embedded system.
	 *
	 * @param intp A CommandInterpreter object containing the command
	 * and it's arguments.
	 */
	public void _threads(CommandInterpreter intp) throws Exception {

		ThreadGroup[] threadGroups = getThreadGroups();
		Util.sort(threadGroups);

		ThreadGroup tg = getTopThreadGroup();
		Thread[] threads = new Thread[tg.activeCount()];
		int count = tg.enumerate(threads, true);
		Util.sort(threads);

		StringBuffer sb = new StringBuffer(120);
		intp.println();
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_THREADGROUP_TITLE")); //$NON-NLS-1$
		for (int i = 0; i < threadGroups.length; i++) {
			tg = threadGroups[i];
			int all = tg.activeCount(); //tg.allThreadsCount();
			int local = tg.enumerate(new Thread[all], false); //tg.threadsCount();
			ThreadGroup p = tg.getParent();
			String parent = (p == null) ? "-none-" : p.getName(); //$NON-NLS-1$
			sb.setLength(0);
			sb.append(Util.toString(simpleClassName(tg), 18)).append(" ").append(Util.toString(tg.getName(), 21)).append(" ").append(Util.toString(parent, 16)).append(Util.toString(new Integer(tg.getMaxPriority()), 3)).append(Util.toString(new Integer(local), 4)).append("/").append(Util.toString(String.valueOf(all), 6)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			intp.println(sb.toString());
		}
		intp.print(newline);
		intp.println(ConsoleMsg.formatter.getString("CONSOLE_THREADTYPE_TITLE")); //$NON-NLS-1$
		for (int j = 0; j < count; j++) {
			Thread t = threads[j];
			if (t != null) {
				sb.setLength(0);
				sb.append(Util.toString(simpleClassName(t), 18)).append(" ").append(Util.toString(t.getName(), 21)).append(" ").append(Util.toString(t.getThreadGroup().getName(), 16)).append(Util.toString(new Integer(t.getPriority()), 3)); //$NON-NLS-1$ //$NON-NLS-2$
				intp.println(sb.toString());
			}
		}
	}

	/**
	 * Handles the sl (startlevel) command. 
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _sl(CommandInterpreter intp) throws Exception {
		if (isStartLevelSvcPresent(intp)) {
			org.osgi.framework.Bundle bundle = null;
			String token = intp.nextArgument();
			int value = 0;
			if (token != null) {
				bundle = getBundleFromToken(intp, token, true);
				if (bundle == null) {
					return;
				}
			}
			if (bundle == null) { // must want framework startlevel
				value = slImpl.getStartLevel();
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_FRAMEWORK_ACTIVE_STARTLEVEL", value)); //$NON-NLS-1$
			} else { // must want bundle startlevel
				value = slImpl.getBundleStartLevel(bundle);
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_BUNDLE_STARTLEVEL", new Long(bundle.getBundleId()), new Integer(value))); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Handles the setfwsl (set framework startlevel) command. 
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _setfwsl(CommandInterpreter intp) throws Exception {
		if (isStartLevelSvcPresent(intp)) {
			int value = 0;
			String token = intp.nextArgument();
			if (token == null) {
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_NO_STARTLEVEL_GIVEN")); //$NON-NLS-1$
				value = slImpl.getStartLevel();
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_FRAMEWORK_ACTIVE_STARTLEVEL", value)); //$NON-NLS-1$
			} else {
				value = this.getStartLevelFromToken(intp, token);
				if (value > 0) {
					try {
						slImpl.setStartLevel(value);
						intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_FRAMEWORK_ACTIVE_STARTLEVEL", value)); //$NON-NLS-1$
					} catch (IllegalArgumentException e) {
						intp.println(e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Handles the setbsl (set bundle startlevel) command. 
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _setbsl(CommandInterpreter intp) throws Exception {
		if (isStartLevelSvcPresent(intp)) {
			String token;
			AbstractBundle bundle = null;
			token = intp.nextArgument();
			if (token == null) {
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_NO_STARTLEVEL_OR_BUNDLE_GIVEN")); //$NON-NLS-1$
				return;
			}

			int newSL = this.getStartLevelFromToken(intp, token);

			token = intp.nextArgument();
			if (token == null) {
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_NO_STARTLEVEL_OR_BUNDLE_GIVEN")); //$NON-NLS-1$
				return;
			}
			while (token != null) {
				bundle = getBundleFromToken(intp, token, true);
				if (bundle != null) {
					try {
						slImpl.setBundleStartLevel(bundle, newSL);
						intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_BUNDLE_STARTLEVEL", new Long(bundle.getBundleId()), new Integer(newSL))); //$NON-NLS-1$
					} catch (IllegalArgumentException e) {
						intp.println(e.getMessage());
					}
				}
				token = intp.nextArgument();
			}
		}
	}

	/**
	 * Handles the setibsl (set initial bundle startlevel) command. 
	 *
	 * @param intp A CommandInterpreter object containing the command and it's arguments.
	 */
	public void _setibsl(CommandInterpreter intp) throws Exception {
		if (isStartLevelSvcPresent(intp)) {
			int value = 0;
			String token = intp.nextArgument();
			if (token == null) {
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_NO_STARTLEVEL_GIVEN")); //$NON-NLS-1$
				value = slImpl.getInitialBundleStartLevel();
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_INITIAL_BUNDLE_STARTLEVEL", value)); //$NON-NLS-1$
			} else {
				value = this.getStartLevelFromToken(intp, token);
				if (value > 0) {
					try {
						slImpl.setInitialBundleStartLevel(value);
						intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_INITIAL_BUNDLE_STARTLEVEL", value)); //$NON-NLS-1$
					} catch (IllegalArgumentException e) {
						intp.println(e.getMessage());
					}
				}
			}
		}
	}

	public void _requiredBundles(CommandInterpreter intp) {
		_classSpaces(intp);
	}

	public void _classSpaces(CommandInterpreter intp) {

		String token = intp.nextArgument();

		org.osgi.framework.ServiceReference packageAdminRef = context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					org.osgi.service.packageadmin.RequiredBundle[] symBundles = null;

					symBundles = packageAdmin.getRequiredBundles(token);

					if (symBundles == null) {
						intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_NAMED_CLASS_SPACES_MESSAGE")); //$NON-NLS-1$
					} else {
						for (int i = 0; i < symBundles.length; i++) {
							org.osgi.service.packageadmin.RequiredBundle symBundle = symBundles[i];
							intp.print(symBundle);

							boolean removalPending = symBundle.isRemovalPending();
							if (removalPending) {
								intp.print("("); //$NON-NLS-1$
								intp.print(ConsoleMsg.formatter.getString("CONSOLE_REMOVAL_PENDING_MESSAGE")); //$NON-NLS-1$
								intp.println(")"); //$NON-NLS-1$
							}

							org.osgi.framework.Bundle provider = symBundle.getBundle();
							if (provider != null) {
								intp.print("<"); //$NON-NLS-1$
								intp.print(provider);
								intp.println(">"); //$NON-NLS-1$

								org.osgi.framework.Bundle[] requiring = symBundle.getRequiringBundles();
								if (requiring != null)
									for (int j = 0; j < requiring.length; j++) {
										intp.print("  "); //$NON-NLS-1$
										intp.print(requiring[j]);
										intp.print(" "); //$NON-NLS-1$
										intp.println(ConsoleMsg.formatter.getString("CONSOLE_REQUIRES_MESSAGE")); //$NON-NLS-1$
									}
							} else {
								intp.print("<"); //$NON-NLS-1$
								intp.print(ConsoleMsg.formatter.getString("CONSOLE_STALE_MESSAGE")); //$NON-NLS-1$
								intp.println(">"); //$NON-NLS-1$
							}

						}
					}
				} finally {
					context.ungetService(packageAdminRef);
				}
			}
		} else {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_NO_EXPORTED_PACKAGES_NO_PACKAGE_ADMIN_MESSAGE")); //$NON-NLS-1$
		}
	}


	/**
	 * Checks for the presence of the StartLevel Service.  Outputs a message if it is not present.
	 * @param intp The CommandInterpreter object to be used to write to the console
	 * @return true or false if service is present or not
	 */
	protected boolean isStartLevelSvcPresent(CommandInterpreter intp) {
		boolean retval = false;
		org.osgi.framework.ServiceReference slSvcRef = context.getServiceReference("org.osgi.service.startlevel.StartLevel"); //$NON-NLS-1$
		if (slSvcRef != null) {
			org.osgi.service.startlevel.StartLevel slSvc = (org.osgi.service.startlevel.StartLevel) context.getService(slSvcRef);
			if (slSvc != null) {
				retval = true;
			}
		} else {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_CAN_NOT_USE_STARTLEVEL_NO_STARTLEVEL_SVC_ERROR")); //$NON-NLS-1$
		}
		return retval;
	}

	/**
	 *  Given a number, retrieve the Bundle object with that id.
	 *
	 *	@param intp The CommandInterpreter
	 *  @param token A string containing a potential bundle it
	 *  @param error A boolean indicating whether or not to output a message
	 *  @return The requested Bundle object
	 */
	protected AbstractBundle getBundleFromToken(CommandInterpreter intp, String token, boolean error) {
		AbstractBundle bundle;
		try {
			long id = Long.parseLong(token);
			bundle = (AbstractBundle) context.getBundle(id);
		} catch (NumberFormatException nfe) {
			bundle = ((BundleContextImpl) context).getBundleByLocation(token);
		}

		if ((bundle == null) && error) {
			intp.println(ConsoleMsg.formatter.getString("CONSOLE_CANNOT_FIND_BUNDLE_ERROR", token)); //$NON-NLS-1$
		}

		return (bundle);
	}

	/**
		 *  Given a string containing a startlevel value, validate it and convert it to an int
		 * 
		*  @param intp A CommandInterpreter object used for printing out error messages
		*  @param value A string containing a potential startlevel
		*  @return The start level or an int <0 if it was invalid
		 */
	protected int getStartLevelFromToken(CommandInterpreter intp, String value) {
		int retval = -1;
		try {
			retval = Integer.parseInt(value);
			if (Integer.parseInt(value) <= 0) {
				intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_POSITIVE_INTEGER")); //$NON-NLS-1$
			}
		} catch (NumberFormatException nfe) {
			intp.println(ConsoleMsg.formatter.getString("STARTLEVEL_POSITIVE_INTEGER")); //$NON-NLS-1$
		}
		return retval;
	}

	/**
	 *  Given a state value, return the string describing that state.
	 *
	 *  @param state An int containing a state value
	 *  @return A String describing the state
	 */
	protected String getStateName(int state) {
		switch (state) {
			case AbstractBundle.UNINSTALLED :
				return (ConsoleMsg.formatter.getString("CONSOLE_UNINSTALLED_MESSAGE")); //$NON-NLS-1$

			case AbstractBundle.INSTALLED :
				return (ConsoleMsg.formatter.getString("CONSOLE_INSTALLED_MESSAGE")); //$NON-NLS-1$

			case AbstractBundle.RESOLVED :
				return (ConsoleMsg.formatter.getString("CONSOLE_RESOLVED_MESSAGE")); //$NON-NLS-1$

			case AbstractBundle.STARTING :
				return (ConsoleMsg.formatter.getString("CONSOLE_STARTING_MESSAGE")); //$NON-NLS-1$

			case AbstractBundle.STOPPING :
				return (ConsoleMsg.formatter.getString("CONSOLE_STOPPING_MESSAGE")); //$NON-NLS-1$

			case AbstractBundle.ACTIVE :
				return (ConsoleMsg.formatter.getString("CONSOLE_ACTIVE_MESSAGE")); //$NON-NLS-1$

			default :
				return (Integer.toHexString(state));
		}
	}

	/**
	 * Answers all thread groups in the system.
	 *
	 * @return	An array of all thread groups.
	 */
	protected ThreadGroup[] getThreadGroups() {
		ThreadGroup tg = getTopThreadGroup();
		ThreadGroup[] groups = new ThreadGroup[tg.activeGroupCount()];
		int count = tg.enumerate(groups, true);
		if (count == groups.length) {
			return groups;
		}
		// get rid of null entries
		ThreadGroup[] ngroups = new ThreadGroup[count];
		System.arraycopy(groups, 0, ngroups, 0, count);
		return ngroups;
	}

	/**
	 * Answers the top level group of the current thread.
	 * <p>
	 * It is the 'system' or 'main' thread group under
	 * which all 'user' thread groups are allocated.
	 *
	 * @return	The parent of all user thread groups.
	 */
	protected ThreadGroup getTopThreadGroup() {
		ThreadGroup topGroup = Thread.currentThread().getThreadGroup();
		if (topGroup != null) {
			while (topGroup.getParent() != null) {
				topGroup = topGroup.getParent();
			}
		}
		return topGroup;
	}

	/**
	 * Returns the simple class name of an object.
	 *
	 * @param o The object for which a class name is requested
	 * @return	The simple class name.
	 */
	public String simpleClassName(Object o) {
		java.util.StringTokenizer t = new java.util.StringTokenizer(o.getClass().getName(), "."); //$NON-NLS-1$
		int ct = t.countTokens();
		for (int i = 1; i < ct; i++) {
			t.nextToken();
		}
		return t.nextToken();
	}
}
