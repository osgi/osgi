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
/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tb6;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;

/**
 * 
 * 
 * @author Neviana Ducheva
 * @version
 * @since
 */
public class ProducerImpl implements BundleActivator, Producer {
	@Override
	public void start(BundleContext context) {
		Hashtable<String,Object> p = new Hashtable<>();
		p.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, new Class[] {
				String.class, Integer.class, Envelope.class});
		p.put(Constants.SERVICE_PID, "producer.ProducerImplC");
		p.put(WireConstants.WIREADMIN_PRODUCER_SCOPE, new String[] {"*"});
		context.registerService(Producer.class.getName(), this, p);
	}

	@Override
	public void stop(BundleContext context) {
		// service unregistered by framework
	}

	/**
	 * 
	 * 
	 * @param wires
	 */
	@Override
	public void consumersConnected(Wire[] wires) {
		// empty
	}

	/**
	 * 
	 * 
	 * @param wire
	 * @return
	 */
	@Override
	public Object polled(Wire wire) {
		return "";
	}
}
