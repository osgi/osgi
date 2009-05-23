/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

	private static final int	timeout;
	private static final int	scaling; 
	
	static {
		String timoutStr = System.getProperty("org.osgi.test.testcase.timeout",
				"60000");
		String scalingStr = System.getProperty(
				"org.osgi.test.testcase.scaling", "1");
		int t;
		int s;
		try {
			t = Integer.parseInt(timoutStr);
			if (t < 0) {
				t = 0;
			}
		}
		catch (Exception e) {
			t = 60000;
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

	public static int getTimeout() {
		return timeout;
	}

	public static int getScaling() {
		return scaling;
	}
}
