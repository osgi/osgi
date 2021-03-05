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
package org.osgi.test.cases.framework.secure.permissions.condition;

import java.util.Collection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condition.Condition;
import org.osgi.test.cases.framework.secure.permissions.util.PermissionsFilterException;
import org.osgi.test.cases.framework.secure.permissions.util.Util;

public class GetConditionActivator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		if (Util.debug)
			System.out.println("GETTER BUNDLE is going to start.");

		Collection<ServiceReference<Condition>> trueCondition = context
				.getServiceReferences(Condition.class,
						"(" + Condition.CONDITION_ID + "="
								+ Condition.CONDITION_ID_TRUE + ")");
		if (trueCondition.isEmpty()) {
			throw new PermissionsFilterException(
					"Fail to get ServiceReference of for true condition.");
		}
		else {
			if (Util.debug)
				System.out.println("Found true condition.");
		}

		Condition service = context.getService(trueCondition.iterator().next());
		if (service == null) {
			throw new PermissionsFilterException(
					"Fail to get service of true condition");
		}
		else {
			if (Util.debug)
				System.out.println("Got true condition service");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// empty
	}

}
