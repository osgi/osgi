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
 */
package org.osgi.test.cases.dmt.tc2.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin.AddEventListener;
import org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin.DmtAdressingUri;
import org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin.GetSession;
import org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin.RemoveEventListener;
import org.osgi.test.cases.dmt.tc2.tb1.DmtEvent.DmtEvent;
import org.osgi.test.cases.dmt.tc2.tb1.DmtEvent.DmtEventConstants;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.Close;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.Commit;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.Copy;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.CreateInteriorNode;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.CreateLeafNode;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.DeleteNode;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.DmtSessionConstants;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.Events;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.Execute;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetChildNodeNames;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetEffectiveNodeAcl;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetLockType;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetMetaNode;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetNodeSize;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetNodeTimestamp;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetNodeVersion;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetPrincipal;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetRootUri;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetSessionId;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetSetNodeAcl;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetSetNodeTitle;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetSetNodeType;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetSetNodeValue;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.GetState;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.IsLeafNode;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.IsNodeUri;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.RenameNode;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.Rollback;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.SetDefaultNodeValue;
import org.osgi.test.cases.dmt.tc2.tb1.DmtSession.TestExceptions;
import org.osgi.test.cases.dmt.tc2.tb1.NotificationService.SendNotification;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TB1Service;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class Activator implements BundleActivator, TB1Service  {
	private ServiceRegistration<TB1Service> servReg;
	@Override
	public void start(BundleContext bc) throws Exception {
		servReg = bc.registerService(TB1Service.class, this, null);
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		servReg.unregister();
	}

	@Override
	public TestInterface[] getTestClasses(DefaultTestBundleControl tbc) {
		return new TestInterface[] { 
				 new Close((DmtTestControl)tbc), 
				 new Commit((DmtTestControl)tbc),
				 new Copy((DmtTestControl)tbc),
				 new CreateInteriorNode((DmtTestControl)tbc),
				 new CreateLeafNode((DmtTestControl)tbc),
				 new DeleteNode((DmtTestControl)tbc),				 
				 new GetChildNodeNames((DmtTestControl)tbc),
				 new GetMetaNode((DmtTestControl)tbc), 
				 new GetNodeSize((DmtTestControl)tbc),
				 new GetNodeTimestamp((DmtTestControl)tbc), 
				 new GetNodeVersion((DmtTestControl)tbc),
				 new GetSetNodeTitle((DmtTestControl)tbc), 
				 new GetSetNodeType((DmtTestControl)tbc),
				 new GetSetNodeValue((DmtTestControl)tbc), 
				 new RenameNode((DmtTestControl)tbc),
				 new Rollback((DmtTestControl)tbc),
				 new IsLeafNode((DmtTestControl)tbc),
				 new Execute((DmtTestControl)tbc), 
				 new GetEffectiveNodeAcl((DmtTestControl)tbc), 
				 new GetSetNodeAcl((DmtTestControl)tbc),
				 new GetSession((DmtTestControl)tbc),
				 new DmtSessionConstants((DmtTestControl)tbc),
				 new GetLockType((DmtTestControl)tbc),
				 new GetPrincipal((DmtTestControl)tbc),
				 new GetRootUri((DmtTestControl)tbc),
				 new GetSessionId((DmtTestControl)tbc),
				 new GetState((DmtTestControl)tbc),
				 new IsNodeUri((DmtTestControl)tbc),
				 new SendNotification((DmtTestControl)tbc),
				 new SetDefaultNodeValue((DmtTestControl)tbc),
				 new DmtAdressingUri((DmtTestControl)tbc),
				 new TestExceptions((DmtTestControl)tbc),
				 new Events((DmtTestControl)tbc),
				 new AddEventListener((DmtTestControl)tbc),
				 new RemoveEventListener((DmtTestControl)tbc),
                 new DmtEvent((DmtTestControl)tbc),
                 new DmtEventConstants((DmtTestControl)tbc)
		};
	}					
				
	

}
