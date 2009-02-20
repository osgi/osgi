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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.osgi.framework.BundleContext;

public abstract class OSGiTestCase extends TestCase {
	private volatile BundleContext	context;

	/**
	 * This method is called by the JUnit runner for OSGi, and gives us a Bundle
	 * Context.
	 */
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Returns the current Bundle Context
	 */
	public BundleContext getContext() {
		BundleContext c = context;
		if (c == null) {
			fail("No Bundle context set, are you running in OSGi Test mode?");
		}
		return c;
	}

	/**
	 * Fail with cause t.
	 * 
	 * @param message Failure message.
	 * @param t Cause of the failure.
	 */
	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}
}
