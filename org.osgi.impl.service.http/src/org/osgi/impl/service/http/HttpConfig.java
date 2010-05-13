/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

import java.util.Properties;

//  ******************** HttpConfig ********************
/**
 * * The HttpConfig class holds all the configuration for the * web server, e.g.
 * mime types and ports to use. *
 * <p>* The classes is declares abstract and contain a set of * static fields
 * and methods for getting configuration values and * properties. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public final class HttpConfig {
	private final static Properties	props				= new Properties();
	// Some string constants
	public final static String		DEFAULT_MIME_TYPE	= "application/octet-stream";
	public final static String		OSGIREF_SESSION		= "osgirefsession";
	public static boolean			useErrorTrailer		= true;						// Add
																						   // a
																						   // stylish
																						   // trailer
																						   // at
																						   // the
																						   // end
																						   // of
																						   // an
																						   // error
																						   // response
	static {
		// HTTP port
		props.setProperty("port.primary", "80");
		props.setProperty("port.secondary", "8080");
		// Mime-Type
		props.setProperty("mimetype.jpg", "image/jpg");
		props.setProperty("mimetype.jpeg", "image/jpg");
		props.setProperty("mimetype.jpe", "image/jpg");
		props.setProperty("mimetype.gif", "image/gif");
		props.setProperty("mimetype.bmp", "image/bmp");
		props.setProperty("mimetype.htm", "text/html");
		props.setProperty("mimetype.html", "text/html");
		props.setProperty("mimetype.txt", "text/plain");
		props.setProperty("mimetype.qt", "video/quicktime");
		props.setProperty("mimetype.mov", "video/quicktime");
		props.setProperty("mimetype.class", "application/octet-stream");
		props.setProperty("mimetype.jar", "application/octet-stream");
		props.setProperty("mimetype.mpg", "video/mpeg");
		props.setProperty("mimetype.mpeg", "video/mpeg");
		props.setProperty("mimetype.mpe", "video/mpeg");
		props.setProperty("mimetype.au", "audio/basic");
		props.setProperty("mimetype.snd", "audio/basic");
		props.setProperty("mimetype.wav", "audio/x-wave");
		props.setProperty("mimetype.css", "text/css");
		props.setProperty("mimetype.wml", "text/vnd.wap.wml");
		props.setProperty("mimetype.wbmp", "image/vnd.wap.wbmp; level=0");
		props.setProperty("mimetype.shtml", "text/html");
	}

	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
}
