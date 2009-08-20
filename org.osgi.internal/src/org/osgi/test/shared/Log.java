/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * A log record used to send log information from the target to the director.
 */
public class Log implements Serializable {
	public final static long	serialVersionUID	= 1;
	Date						_date;
	String						_string;

	/**
	 * Create a new log record.
	 */
	public Log(Date date, String string) {
		_date = date;
		_string = string;
	}

	/**
	 * Answer the date for the log.
	 */
	public Date getDate() {
		return _date;
	}

	/**
	 * Answer the log string.
	 */
	public String getString() {
		return _string;
	}

	/**
	 * Convert to string.
	 */
	public String toString() {
		return _string;
	}

	final static int	SKIP	= 2;
	final static int	NORMAL	= 3;

	public static String cleanup(String s) {
		StringBuffer sb = new StringBuffer();
		int state = NORMAL;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (state) {
				case NORMAL :
					switch (c) {
						case '[' :
							state = SKIP;
							break;
						case '#' :
							return sb.toString();
						default :
							sb.append(c);
							break;
					}
					break;
				case SKIP :
					switch (c) {
						case ']' :
							state = NORMAL;
							c = 0;
							break;
					}
					break;
			}
		}
		return sb.toString().trim();
	}
}
