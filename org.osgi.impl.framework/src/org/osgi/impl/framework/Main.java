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

import java.security.*;
import org.osgi.framework.BundleException;

/**
 * This is a simple main that can start framework and does some simple
 * operations install, start, stop, uninstall and update.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Main {
	static Framework	framework;
	static FileTree		dir	= new FileTree(System.getProperty(
									"org.osgi.framework.dir", "fwdir"));

	/**
	 * Example of a main method for starting the OSGi reference framework.
	 */
	public static void main(String[] args) {
		System.out.println("OSGi Framework, specification version "
				+ Framework.getProperty("org.osgi.framework.version"));
		System.out
				.println("This is a reference implementation of the OSGi Framework.");
		System.out
				.println("It is written to be correct and easy to understand and is");
		System.out
				.println("intended only to be used for test and reference purposes.");
		System.out
				.println("Copyright (c) 1999 - 2002 Gatespace AB. All Rights Reserved.");
		String base = System.getProperty("org.osgi.framework.install.base",
				"file:");
		int i = 0;
		if (args.length > 0 && "-init".equals(args[i])) {
			dir.delete();
			System.out.println("Removed all existing bundles.");
			i++;
		}
		try {
			framework = new Framework(dir);
		}
		catch (Exception e) {
			e.printStackTrace();
			error("New Framework failed: " + e);
		}
		if (args.length == 0) {
			args = new String[] {"-launch"};
		}
		// TODO: This is too large now, rewrite.
		for (; i < args.length; i++) {
			try {
				if ("-exit".equals(args[i])) {
					System.out.println("Exit.");
					System.exit(0);
				}
				else
					if ("-help".equals(args[i])) {
						printHelp();
					}
					else
						if ("-install".equals(args[i])) {
							if (i + 1 < args.length) {
								String bundle = args[i + 1];
								long id;
								if (bundle.indexOf(":") != -1) {
									id = framework.installBundle(bundle, null);
								}
								else {
									id = framework.installBundle(base + bundle,
											null);
								}
								System.out.println("Installed: "
										+ framework.getBundleLocation(id)
										+ " (id#" + id + ")");
								i++;
							}
							else {
								error("No URL for install command");
							}
						}
						else
							if ("-istart".equals(args[i])) {
								if (i + 1 < args.length) {
									String bundle = args[i + 1];
									long id;
									if (bundle.indexOf(":") != -1) {
										id = framework.installBundle(bundle,
												null);
									}
									else {
										id = framework.installBundle(base
												+ bundle, null);
									}
									framework.startBundle(id);
									System.out
											.println("Installed and started: "
													+ framework
															.getBundleLocation(id)
													+ " (id#" + id + ")");
									i++;
								}
								else {
									error("No URL for install command");
								}
							}
							else
								if ("-launch".equals(args[i])) {
									int startlevel = Framework.DEFAULT_START_LEVEL;
									if (i + 1 < args.length) {
										try {
											startlevel = Integer
													.parseInt(args[i + 1]);
											i++;
										}
										catch (NumberFormatException nfe) {
										}
									}
									framework.launch(startlevel);
									System.out.println("Framework launched");
								}
								else
									if ("-shutdown".equals(args[i])) {
										shutdown();
										System.out
												.println("Framework shutdown");
									}
									else
										if ("-sleep".equals(args[i])) {
											if (i + 1 < args.length) {
												long t = Long
														.parseLong(args[i + 1]);
												try {
													System.out
															.println("Sleeping...");
													Thread.sleep(t * 1000);
												}
												catch (InterruptedException e) {
													error("Sleep interrupted.");
												}
												i++;
											}
											else {
												error("No time for sleep command");
											}
										}
										else
											if ("-start".equals(args[i])) {
												if (i + 1 < args.length) {
													long id = Long
															.parseLong(args[i + 1]);
													framework.startBundle(id);
													System.out
															.println("Started: "
																	+ framework
																			.getBundleLocation(id)
																	+ " (id#"
																	+ id + ")");
													i++;
												}
												else {
													error("No ID for start command");
												}
											}
											else
												if ("-stop".equals(args[i])) {
													if (i + 1 < args.length) {
														long id = Long
																.parseLong(args[i + 1]);
														framework
																.stopBundle(id);
														System.out
																.println("Stopped: "
																		+ framework
																				.getBundleLocation(id)
																		+ " (id#"
																		+ id
																		+ ")");
														i++;
													}
													else {
														error("No ID for stop command");
													}
												}
												else
													if ("-uninstall"
															.equals(args[i])) {
														if (i + 1 < args.length) {
															long id = Long
																	.parseLong(args[i + 1]);
															String loc = framework
																	.getBundleLocation(id);
															framework
																	.uninstallBundle(id);
															System.out
																	.println("Uninstalled: "
																			+ loc
																			+ " (id#"
																			+ id
																			+ ")");
															i++;
														}
														else {
															error("No id for uninstall command");
														}
													}
													else
														if ("-update"
																.equals(args[i])) {
															if (i + 1 < args.length) {
																long id = Long
																		.parseLong(args[i + 1]);
																framework
																		.updateBundle(id);
																System.out
																		.println("Updated: "
																				+ framework
																						.getBundleLocation(id)
																				+ " (id#"
																				+ id
																				+ ")");
																i++;
															}
															else {
																error("No id for update command");
															}
														}
														else {
															error("Unknown option: "
																	+ args[i]);
														}
			}
			catch (BundleException e) {
				Throwable ne = e.getNestedException();
				if (ne != null) {
					e.getNestedException().printStackTrace(System.err);
				}
				else {
					e.printStackTrace(System.err);
				}
				error("Command " + args[i] + " failed, " + e.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
				error("Command " + args[i] + " failed.");
			}
		}
	}

	/**
	 * Restart framework in seperate thread.
	 */
	static public void restart() {
		framework.checkAdminPermission();
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				Thread t = new Thread() {
					public void run() {
						framework.shutdown();
						try {
							framework.launch();
						}
						catch (Exception e) {
							error("Framework restart failed: " + e);
						}
					}
				};
				t.setDaemon(false);
				t.start();
				return null;
			}
		});
	}

	/**
	 * Shutdown framework in seperate thread.
	 */
	static public void shutdown() {
		framework.checkAdminPermission();
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				Thread t = new Thread() {
					public void run() {
						framework.shutdown();
					}
				};
				t.setDaemon(false);
				t.start();
				return null;
			}
		});
	}

	/**
	 * Print help for starting the platform.
	 */
	static void printHelp() {
		System.out
				.println("Usage:  java [properties]  org.osgi.impl.framework.Main [-init] [options]");
		System.out.println("Options:");
		System.out.println("  -exit         Exit the JVM process");
		System.out.println("  -help         Print this text");
		System.out.println("  -init         Start an empty platform");
		System.out.println("  -install URL  Install a bundle");
		System.out.println("  -istart URL   Install and start bundle");
		System.out
				.println("  -launch [SL]  Launch framework at start level SL (Default: 1)");
		System.out.println("  -sleep SEC    Sleep a while before next command");
		System.out.println("  -shutdown     Shutdown framework");
		System.out.println("  -start ID     Start bundle");
		System.out.println("  -stop ID      Stop bundle");
		System.out.println("  -uninstall ID Uninstall a bundle");
		System.out.println("");
		System.out.println("Properties:");
		System.out.println("  org.osgi.framework.dir -");
		System.out.println("    Where we store persistent data");
		System.out.println("    (Default: ./fwdir)");
		System.out.println("  org.osgi.framework.system.packages -");
		System.out
				.println("    List of packages exported from system classloader,");
		System.out
				.println("    all none java.* and org.osgi.framework in classpath");
		System.out.println("  org.osgi.framework.install.base -");
		System.out.println("    Base URL for relative install commands");
		System.out.println("    (Default: file:)");
	}

	/**
	 * Report error and exit.
	 */
	static void error(String s) {
		System.err.println("Error: " + s);
		System.exit(1);
	}
}
