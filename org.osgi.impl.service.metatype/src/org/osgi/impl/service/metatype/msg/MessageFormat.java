/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.metatype.msg;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * This class retrieves strings from a resource bundle and returns them,
 * formatting them with MessageFormat when required.
 * <p>
 * It is used by the system classes to provide national language support, by
 * looking up messages in the <code>
 *    com.ibm.osg.smf.ExternalMessages
 * </code>
 * resource bundle. Note that if this file is not available, or an invalid key
 * is looked up, or resource bundle support is not available, the key itself
 * will be returned as the associated message. This means that the <em>KEY</em>
 * should be a reasonable human-readable (english) string.
 * 
 * @author
 * @version 1.0
 */
public class MessageFormat {

	// ResourceBundle holding the messages.
	private ResourceBundle	bundle;
	private Locale			locale;

	public MessageFormat(String bundleName) {
		init(bundleName, Locale.getDefault(), this.getClass());
	}

	public MessageFormat(String bundleName, Locale locale) {
		init(bundleName, locale, this.getClass());
	}

	public MessageFormat(String bundleName, Locale locale, Class clazz) {
		init(bundleName, locale, clazz);
	}

	protected void init(final String bundleName, final Locale locale,
			final Class clazz) {
		bundle = (ResourceBundle) AccessController
				.doPrivileged(new PrivilegedAction() {
					public Object run() {
						ClassLoader loader = clazz.getClassLoader();

						if (loader == null) {
							loader = ClassLoader.getSystemClassLoader();
						}

						try {
							return ResourceBundle.getBundle(bundleName, locale,
									loader);
						}
						catch (MissingResourceException e) {
							return null;
						}
					}
				});

		this.locale = locale;
	}

	/**
	 * Return the Locale object used for this MessageFormat object.
	 * 
	 * @author BJ Hargrave (hargrave@us.ibm.com)
	 * @version 1.0
	 * @return Locale of this object.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Retrieves a message which has no arguments.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param msg String the key to look up.
	 * @return String the message for that key in the system message bundle.
	 */
	public String getString(String msg) {

		if (bundle == null) {
			return msg;
		}

		try {
			return bundle.getString(msg);
		}
		catch (MissingResourceException e) {
			return msg;
		}
	}

	/**
	 * Retrieves a message which takes 1 argument.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param msg String the key to look up.
	 * @param arg Object the object to insert in the formatted output.
	 * @return String the message for that key in the system message bundle.
	 */
	public String getString(String msg, Object arg) {
		return getString(msg, new Object[] {arg});
	}

	/**
	 * Retrieves a message which takes 1 integer argument.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param msg String the key to look up.
	 * @param arg int the integer to insert in the formatted output.
	 * @return String the message for that key in the system message bundle.
	 */
	public String getString(String msg, int arg) {
		return getString(msg, new Object[] {Integer.toString(arg)});
	}

	/**
	 * Retrieves a message which takes 1 character argument.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param msg String the key to look up.
	 * @param arg char the character to insert in the formatted output.
	 * @return String the message for that key in the system message bundle.
	 */
	public String getString(String msg, char arg) {
		return getString(msg, new Object[] {String.valueOf(arg)});
	}

	/**
	 * Retrieves a message which takes 2 arguments.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param msg String the key to look up.
	 * @param arg1 Object an object to insert in the formatted output.
	 * @param arg2 Object another object to insert in the formatted output.
	 * @return String the message for that key in the system message bundle.
	 */
	public String getString(String msg, Object arg1, Object arg2) {
		return getString(msg, new Object[] {arg1, arg2});
	}

	/**
	 * Retrieves a message which takes several arguments.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param msg String the key to look up.
	 * @param args Object[] the objects to insert in the formatted output.
	 * @return String the message for that key in the system message bundle.
	 */
	public String getString(String msg, Object[] args) {

		String format = msg;

		if (bundle != null) {
			try {
				format = bundle.getString(msg);
			}
			catch (MissingResourceException e) {
			}
		}

		return format(format, args);
	}

	/**
	 * Generates a formatted text string given a source string containing
	 * "argument markers" of the form "{argNum}" where each argNum must be in
	 * the range 0..9. The result is generated by inserting the toString of each
	 * argument into the position indicated in the string.
	 * <p>
	 * To insert the "{" character into the output, use a single backslash
	 * character to escape it (i.e. "\{"). The "}" character does not need to be
	 * escaped.
	 * 
	 * @author OTI
	 * @version initial
	 * 
	 * @param format String the format to use when printing.
	 * @param args Object[] the arguments to use.
	 * @return String the formatted message.
	 */
	public static String format(String format, Object[] args) {
		StringBuffer answer = new StringBuffer();
		String[] argStrings = new String[args.length];

		for (int i = 0; i < args.length; ++i) {
			if (args[i] == null)
				argStrings[i] = "<null>"; //$NON-NLS-1$
			else
				argStrings[i] = args[i].toString();
		}

		int lastI = 0;

		for (int i = format.indexOf('{', 0); i >= 0; i = format.indexOf('{',
				lastI)) {
			if (i != 0 && format.charAt(i - 1) == '\\') {
				// It's escaped, just print and loop.
				if (i != 1) {
					answer.append(format.substring(lastI, i - 1));
				}
				answer.append('{');
				lastI = i + 1;
			}
			else {
				// It's a format character.
				if (i > format.length() - 3) {
					// Bad format, just print and loop.
					answer.append(format.substring(lastI, format.length()));
					lastI = format.length();
				}
				else {
					int argnum = (byte) Character.digit(format.charAt(i + 1),
							10);
					if (argnum < 0 || format.charAt(i + 2) != '}') {
						// Bad format, just print and loop.
						answer.append(format.substring(lastI, i + 1));
						lastI = i + 1;
					}
					else {
						// Got a good one!
						answer.append(format.substring(lastI, i));
						if (argnum >= argStrings.length) {
							answer.append("<missing argument>"); //$NON-NLS-1$
						}
						else {
							answer.append(argStrings[argnum]);
						}
						lastI = i + 3;
					}
				}
			}
		}

		if (lastI < format.length()) {
			answer.append(format.substring(lastI, format.length()));
		}

		return answer.toString();
	}
}
