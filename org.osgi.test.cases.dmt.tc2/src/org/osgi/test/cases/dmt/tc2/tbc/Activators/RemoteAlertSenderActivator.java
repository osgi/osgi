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
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement sendAlert
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tbc.Activators;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.notification.spi.RemoteAlertSender;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;


/**
 * @author Luiz Felipe Guimaraes
 *
 */
public class RemoteAlertSenderActivator implements BundleActivator {

	private ServiceRegistration<RemoteAlertSender>	servReg;
	
	
	public static final int RANKING = 5; 

	private RemoteAlertSenderImpl testRemoteAlertSenderImpl;

	public RemoteAlertSenderActivator() {
	}
	
	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testRemoteAlertSenderImpl = new RemoteAlertSenderImpl();
		
		Dictionary<String,Object> ht = new Hashtable<>();
		ht.put("principals", new String[] { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 });
		ht.put(Constants.SERVICE_RANKING, Integer.valueOf(RemoteAlertSenderActivator.RANKING));	
		servReg = bc.registerService(RemoteAlertSender.class,
				testRemoteAlertSenderImpl, ht);
		System.out.println("RemoteAlertSender activated.");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// unregistering the service
		servReg.unregister();
	}

}
