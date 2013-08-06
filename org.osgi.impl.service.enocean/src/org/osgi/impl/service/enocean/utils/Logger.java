/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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


package org.osgi.impl.service.enocean.utils;

public final class Logger {

	static final boolean	DEBUG	= Boolean.getBoolean("org.osgi.service.enocean.loglevel.debug");
	static final boolean	WARN	= Boolean.getBoolean("org.osgi.service.enocean.loglevel.warn");
	static final boolean	INFO	= Boolean.getBoolean("org.osgi.service.enocean.loglevel.info");
	static final boolean	ERROR	= Boolean.getBoolean("org.osgi.service.enocean.loglevel.error");

	public static final void d(String tag, String msg) {
		if (DEBUG)
			System.out.println("[" + tag + "] " + msg);
	}

	public static final void i(String tag, String msg) {
		if (INFO)
			System.out.println("[" + tag + "] " + msg);
	}

	public static final void w(String tag, String msg) {
		if (WARN)
			System.out.println("[" + tag + "] " + msg);
	}

	public static final void e(String tag, String msg) {
		if (ERROR)
			System.out.println("[" + tag + "] " + msg);
	}

}
