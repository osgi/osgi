/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.framework.junit.adaptations;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;

public class BundleAdaptTests extends OSGiTestCase {

	public void testBundleContextAdapt() {
		doAdaptTest(BundleContext.class, getContext().getBundle());
	}

	public void testBundleRevisionAdapt() {
		doAdaptTest(BundleRevision.class, getContext().getBundle());
	}

	public void testBundleRevisionsAdapt() {
		doAdaptTest(BundleRevisions.class, getContext().getBundle());
	}

	public void testBundleStartLevelAdapt() {
		doAdaptTest(BundleStartLevel.class, getContext().getBundle());
	}

	public void testBundleWiringAdapt() {
		doAdaptTest(BundleWiring.class, getContext().getBundle());
	}

	public void testFrameworktAdapt() {
		doAdaptTest(Framework.class, getContext().getBundle(0));
		assertNull("Expecting null adapt for Framework type.", getContext().getBundle().adapt(Framework.class));
	}

	public void testFrameworktStartLevel() {
		doAdaptTest(FrameworkStartLevel.class, getContext().getBundle(0));
		assertNull("Expecting null adapt for Framework type.", getContext().getBundle().adapt(FrameworkStartLevel.class));
	}

	public void testFrameworktWiring() {
		doAdaptTest(FrameworkWiring.class, getContext().getBundle(0));
		assertNull("Expecting null adapt for Framework type.", getContext().getBundle().adapt(FrameworkWiring.class));
	}

	private <T> void doAdaptTest(Class<T> clazz, Bundle b) {
		T adapted = b.adapt(clazz);
		assertNotNull("Adapted type is null: " + clazz.getName(), adapted);	
		assertTrue("Wrong adapted type: " + adapted.getClass(), clazz.isAssignableFrom(adapted.getClass()));
	}
}
