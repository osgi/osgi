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

package org.osgi.test.cases.cm.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.SynchronizerImpl;
import org.osgi.test.cases.cm.shared.Synchronizer;

/**
 * 
 * This test bundle checks if the framework prevents a bundle without permission
 * from registering a configuration listener. The permission file associated
 * with this bundle (tb1.perm) does not include the permission to registering
 * configuration listeners (
 * <code>ServicePermission[ConfigurationListener,REGISTER]</code>).
 * 
 * @author Jorge Mascena
 */
public class Activator implements BundleActivator {

	/**
	 * Tries to register a <code>ConfigurationListener</code> instance. Since
	 * this bundle doesn't have
	 * <code>ServicePermission[ConfigurationListener,REGISTER]</code>, the
	 * framework should prevent it from doing so. An exception should be thrown.
	 * 
	 * @param context the execution environment where the bundle is executed
	 * @throws java.lang.Exception if the framework prevented the bundle from
	 *         registering a <code>ConfigurationListener</code>
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Synchronizer synchronizer = new SynchronizerImpl();
		ConfigurationListenerImpl cl = new ConfigurationListenerImpl(
				synchronizer);
		context
				.registerService(ConfigurationListener.class.getName(), cl,
						null);
	}

	/**
	 * Nothing special to be done when finished with this bundle.
	 * 
	 * @param context the execution environment where the bundle is executed
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
