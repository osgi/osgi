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
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 46            [MEGTCK][DMT] Implement AdminPermission Test Cases
 * ============  ==============================================================
 * Jun 17, 2005  Alexandre Alves
 * 28            [MEGTCK][DMT] Implement test cases for DmtSession.close()
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tbc.Activators;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 *
 */
public class EventHandlerActivator implements BundleActivator {

	private DmtTestControl tbc;
	
	public EventHandlerActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}	
	
	private ServiceRegistration<EventHandler>	servReg;
	
	private EventHandlerImpl testDmtHandlerImpl;

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testDmtHandlerImpl = new EventHandlerImpl(tbc);
		String[] topics = new String[] {"org/osgi/service/dmt/DmtEvent/*"};
		String subtree = "(nodes="+TestExecPluginActivator.ROOT + "/*)";
		
		Dictionary<String,Object> ht = new Hashtable<>();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, topics);
		ht.put(org.osgi.service.event.EventConstants.EVENT_FILTER, subtree);
		servReg = bc.registerService(EventHandler.class, testDmtHandlerImpl,
				ht);
	}
	
	@Override
	public void stop(BundleContext arg0) throws Exception {
		// unregistering the service
		servReg.unregister();
	}

}
