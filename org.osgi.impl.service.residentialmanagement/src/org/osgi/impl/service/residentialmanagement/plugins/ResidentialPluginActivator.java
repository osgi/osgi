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
package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
/**
 * 
 * @author Koya MORI, Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class ResidentialPluginActivator implements BundleActivator {
	private FiltersPluginActivator filters;
	private FrameworkPluginActivator framework;
	private LogPluginActivator log;


	
	@Override
	public void start(BundleContext context) throws Exception {
		filters = new FiltersPluginActivator();
		filters.start(context);
		
		log = new LogPluginActivator();
		log.start(context);
		
		framework = new FrameworkPluginActivator();
		framework.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		framework.stop(context);
		log.stop(context);
		filters.stop(context);
	}

}
