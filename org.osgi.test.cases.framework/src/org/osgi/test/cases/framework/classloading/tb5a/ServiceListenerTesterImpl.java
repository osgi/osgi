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

package org.osgi.test.cases.framework.classloading.tb5a;

import org.osgi.framework.ServiceEvent;
import org.osgi.test.cases.framework.classloading.exports.listener.ServiceListenerTester;

/**
 * Service used to test the interface ServiceListener
 * 
 * @author $Id$
 */
public class ServiceListenerTesterImpl implements ServiceListenerTester {

	private ServiceEvent event;
	
	/**
	 * Creates a new instance of ServiceListenerTesterImpl
	 */
	public ServiceListenerTesterImpl() {
		
	}
	
    /**
     * Receives notification that a service has had a lifecycle change.
     *
     * @param event The <code>ServiceEvent</code> object.
     */
    public void serviceChanged(ServiceEvent _event) {
    	event = _event;
    }
	
	/**
	 * Return the service event delivered by the framework
	 * 
	 * @return the service event delivered by the framework
	 */
	public ServiceEvent getServiceEventDelivered() {
		ServiceEvent result = event;
		event = null;
		return result;
	}

	
}
