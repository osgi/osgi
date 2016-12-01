/*
 * Copyright (c) IBM Corporation (2011). All Rights Reserved.
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

package org.osgi.test.cases.framework.secure.wiring.tb3;

import static junit.framework.TestCase.fail;

import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.FrameworkWiring;

public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		Bundle b = context.getBundle(0);
		FrameworkWiring fw = b.adapt(FrameworkWiring.class);
		try {
			fw.resolveBundles(Arrays.asList(context.getBundles()));
		}
		catch (SecurityException e) {
			fail("A security exception should not have been thrown");
		}
	}

	public void stop(BundleContext context) throws Exception {
	}
}
