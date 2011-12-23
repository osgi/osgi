/*
 * Copyright (c) OSGi Alliance (2009, 2011). All Rights Reserved.
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


package org.osgi.test.support;

public class OSGiTestCaseProperties {
	private OSGiTestCaseProperties() {
		// empty
	}

	private static final long	timeout;
	private static final int	scaling; 
	
	static {
		String timoutStr;
		String scalingStr;
		try {
			timoutStr = System.getProperty("org.osgi.test.testcase.timeout",
					"60000");
			scalingStr = System.getProperty("org.osgi.test.testcase.scaling",
					"1");
		}
		catch (SecurityException e) {
			timoutStr = "60000";
			scalingStr = "1";
		}
		long t;
		int s;
		try {
			t = Long.parseLong(timoutStr);
			if (t < 0) {
				t = 0;
			}
		}
		catch (Exception e) {
			t = 60000l;
		}

		try {
			s = Integer.parseInt(scalingStr);
			if (s < 0) {
				s = 0;
			}
		}
		catch (Exception e) {
			s = 1;
		}
		timeout = t;
		scaling = s;

	}

	public static long getTimeout() {
		return timeout;
	}

	public static int getScaling() {
		return scaling;
	}
}
