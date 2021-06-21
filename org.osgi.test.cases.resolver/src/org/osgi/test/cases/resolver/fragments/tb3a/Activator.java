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

package org.osgi.test.cases.resolver.fragments.tb3a;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A bundle that registers a service with the marker interface
 * TBCService so it can be checked the exporter is correct.
 *
 * @author $Id$
 */
public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
	}
	public void stop(BundleContext context) throws Exception {
	}
}
