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

package org.osgi.test.cases.framework.startlevel.tb3;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.BundleStartLevel;

public class Activator implements BundleActivator {
	public void start(BundleContext bc) {
		Bundle b = bc.getBundle();
		BundleStartLevel bsl = b.adapt(BundleStartLevel.class);
		int bundleStartLevel = bsl.getStartLevel();
		bsl.setStartLevel(bundleStartLevel + 10);
	}

	public void stop(BundleContext bc) {
		// empty
	}
}
