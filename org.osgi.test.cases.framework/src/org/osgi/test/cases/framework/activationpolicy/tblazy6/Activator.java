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

package org.osgi.test.cases.framework.activationpolicy.tblazy6;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Bundle[] bundles = context.getBundles();
		
		Bundle tblazy5 = null;
		for (int i = bundles.length - 1; i >= 0; i--) {
			String bsn = bundles[i].getSymbolicName();
			if (bsn != null && bsn.endsWith("tblazy5")) {
				tblazy5 = bundles[i];
				break;
			}
		}
		tblazy5.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy5.CircularityErrorTest");
	}

	public void stop(BundleContext context) throws Exception {
		// do nothing
	}

}
