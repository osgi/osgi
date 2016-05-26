/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee.util;

/**
 * ZigBee logger.
 * 
 * @author $Id$
 */
public final class Logger {

	static final boolean	DEBUG	= Boolean
			.getBoolean("org.osgi.service.enocean.loglevel.debug");
	static final boolean	WARN	= Boolean
			.getBoolean("org.osgi.service.enocean.loglevel.warn");
	static final boolean	INFO	= Boolean
			.getBoolean("org.osgi.service.enocean.loglevel.info");
	static final boolean	ERROR	= Boolean
			.getBoolean("org.osgi.service.enocean.loglevel.error");

	/**
	 * @return true if at least one of the following options has been set to
	 *         true: -Dorg.osgi.service.enocean.loglevel.debug
	 *         -Dorg.osgi.service.enocean.loglevel.warn
	 *         -Dorg.osgi.service.enocean.loglevel.info
	 *         -Dorg.osgi.service.enocean.loglevel.error, false otherwise (and
	 *         display a message via System.out.println(...)).
	 */
	public static final boolean checkConfig() {
		if (DEBUG || WARN || INFO || ERROR) {
			// Here, at least one of the option has been set to true.
			return true;
		} else {
			display("",
					"Here, no log option has been set to true. "
							+ "Feel free to use the following options if relevant: "
							+ "-Dorg.osgi.service.enocean.loglevel.debug=true -Dorg.osgi.service.enocean.loglevel.warn=true -Dorg.osgi.service.enocean.loglevel.info=true -Dorg.osgi.service.enocean.loglevel.error=true");
			return false;
		}
	}

	/**
	 * @param stringToBeDisplayed will be displayed via
	 *        System.out.println(stringToBeDisplayed);
	 */
	private static final void display(String tag, String msg) {
		System.out.println("[" + tag + "," + Thread.currentThread().getName()
				+ "] " + msg);
	}

	/**
	 * @param tag
	 * @param msg
	 */
	public static final void d(String tag, String msg) {
		if (DEBUG) {
			display(tag, msg);
		}
	}

	/**
	 * @param tag
	 * @param msg
	 */
	public static final void i(String tag, String msg) {
		if (INFO) {
			display(tag, msg);
		}
	}

	/**
	 * @param tag
	 * @param msg
	 */
	public static final void w(String tag, String msg) {
		if (WARN) {
			display(tag, msg);
		}
	}

	/**
	 * @param tag
	 * @param msg
	 */
	public static final void e(String tag, String msg) {
		if (ERROR) {
			display(tag, msg);
		}
	}

	/**
	 * @param tag
	 * @param msg
	 */
	public static final void e(String tag, String msg, Exception e) {
		if (ERROR) {
			display(tag, msg);
			e.printStackTrace();
		}
	}

}
