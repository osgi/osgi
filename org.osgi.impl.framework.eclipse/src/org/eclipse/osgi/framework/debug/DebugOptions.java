/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.debug;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class DebugOptions implements org.eclipse.osgi.service.debug.DebugOptions {
	Properties options = null;

	private static DebugOptions singleton = null;
	private static boolean debugEnabled = true;
	private static final String OPTIONS = ".options"; //$NON-NLS-1$

	public static DebugOptions getDefault() {
		if (singleton == null && debugEnabled) {
			DebugOptions result = new DebugOptions();
			debugEnabled = result.isDebugEnabled();
			if (debugEnabled)
				singleton = result;
		}
		return singleton;
	}

	public static URL buildURL(String spec, boolean trailingSlash) {
		if (spec == null)
			return null;
		boolean isFile = spec.startsWith("file:"); //$NON-NLS-1$
		try {
			if (isFile)
				return adjustTrailingSlash(new File(spec.substring(5)).toURL(), trailingSlash);
			else
				return new URL(spec);
		} catch (MalformedURLException e) {
			// if we failed and it is a file spec, there is nothing more we can do
			// otherwise, try to make the spec into a file URL.
			if (isFile)
				return null;
			try {
				return adjustTrailingSlash(new File(spec).toURL(), trailingSlash);
			} catch (MalformedURLException e1) {
				return null;
			}
		}
	}

	private static URL adjustTrailingSlash(URL url, boolean trailingSlash) throws MalformedURLException {
		String file = url.getFile();
		if (trailingSlash == (file.endsWith("/"))) //$NON-NLS-1$
			return url;
		file = trailingSlash ? file + "/" : file.substring(0, file.length() - 1); //$NON-NLS-1$
		return new URL(url.getProtocol(), url.getHost(), file);
	}

	private DebugOptions() {
		super();
		loadOptions();
	}

	public boolean getBooleanOption(String option, boolean defaultValue) {
		String optionValue = getOption(option);
		return (optionValue != null && optionValue.equalsIgnoreCase("true")) || defaultValue; //$NON-NLS-1$
	}

	public String getOption(String option) {
		return options != null ? options.getProperty(option) : null;
	}

	public String getOption(String option, String defaultValue) {
		return options != null ? options.getProperty(option, defaultValue) : defaultValue;
	}

	public int getIntegerOption(String option, int defaultValue) {
		String value = getOption(option);
		try {
			return value == null ? defaultValue : Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public void setOption(String option, String value) {
		if (options != null)
			options.put(option, value.trim());
	}

	public boolean isDebugEnabled() {
		return options != null;
	}

	private void loadOptions() {
		// if no debug option was specified, don't even bother to try.
		// Must ensure that the options slot is null as this is the signal to the
		// platform that debugging is not enabled.
		String debugOptionsFilename = System.getProperty("osgi.debug"); //$NON-NLS-1$
		if (debugOptionsFilename == null)
			return;
		options = new Properties();
		URL optionsFile;
		if (debugOptionsFilename.length() == 0) {
			// default options location is user.dir (install location may be r/o so
			// is not a good candidate for a trace options that need to be updatable by
			// by the user)
			String userDir = System.getProperty("user.dir").replace(File.separatorChar, '/'); //$NON-NLS-1$
			if (!userDir.endsWith("/")) //$NON-NLS-1$
				userDir += "/"; //$NON-NLS-1$
			debugOptionsFilename = new File(userDir, OPTIONS).toString();
		}
		optionsFile = buildURL(debugOptionsFilename, false);
		if (optionsFile == null) {
			System.out.println("Unable to construct URL for options file: " + debugOptionsFilename); //$NON-NLS-1$
			return;
		}
		System.out.print("Debug options:\n    " + optionsFile.toExternalForm()); //$NON-NLS-1$
		try {
			InputStream input = optionsFile.openStream();
			try {
				options.load(input);
				System.out.println(" loaded"); //$NON-NLS-1$
			} finally {
				input.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println(" not found"); //$NON-NLS-1$
		} catch (IOException e) {
			System.out.println(" did not parse"); //$NON-NLS-1$
			e.printStackTrace(System.out);
		}
		// trim off all the blanks since properties files don't do that.
		for (Iterator i = options.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			options.put(key, ((String) options.get(key)).trim());
		}
		if (options.size() == 0)
			options = null;
	}
}
