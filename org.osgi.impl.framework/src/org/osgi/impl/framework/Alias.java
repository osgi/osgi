/**
 * Copyright (c) 2001 Gatespace AB. All Rights Reserved.
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

/**
 * This class contains aliases for system properties.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Alias {
	/**
	 * List of processor aliases. The first entry is the true name.
	 */
	final public static String[][]	processorAliases	= {
			{"Ignite", "psc1k"}, {"PowerPC", "power", "ppc"},
			{"x86", "pentium", "i386", "i486", "i586", "i686"},};
	/**
	 * List of OS name aliases. The first entry is the true name.
	 */
	final public static String[][]	osNameAliases		= { {"OS2", "OS/2"},
			{"QNX", "procnto"}, {"Windows95", "Windows 95", "Win95"},
			{"Windows98", "Windows 98", "Win98"},
			{"WindowsNT", "Windows NT", "WinNT"},
			{"WindowsXP", "Windows XP", "WinXP"},
			{"WindowsCE", "Windows CE", "WinCE"},
			{"Windows2000", "Windows 2000", "Win2000"},	};

	/**
	 * Unify processor names.
	 * 
	 * @param name Processor name.
	 * @return The unified name.
	 */
	static public String unifyProcessor(String name) {
		for (int i = 0; i < processorAliases.length; i++) {
			for (int j = 0; j < processorAliases[i].length; j++) {
				if (name.equalsIgnoreCase(processorAliases[i][j])) {
					return processorAliases[i][0];
				}
			}
		}
		return name;
	}

	/**
	 * Unify OS names.
	 * 
	 * @param name OS name.
	 * @return The unified name.
	 */
	static public String unifyOsName(String name) {
		for (int i = 0; i < osNameAliases.length; i++) {
			for (int j = 0; j < osNameAliases[i].length; j++) {
				if (name.equalsIgnoreCase(osNameAliases[i][j])) {
					return osNameAliases[i][0];
				}
			}
		}
		return name;
	}
}
