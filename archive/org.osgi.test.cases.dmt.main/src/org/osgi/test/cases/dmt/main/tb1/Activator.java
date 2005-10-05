/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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
package org.osgi.test.cases.dmt.main.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.dmt.main.tb1.DmtAdmin.DmtAdressingUri;
import org.osgi.test.cases.dmt.main.tb1.DmtAdmin.GetSession;
import org.osgi.test.cases.dmt.main.tb1.DmtAdmin.SendAlert;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Close;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Commit;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Copy;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.CreateInteriorNode;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.CreateLeafNode;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.DeleteNode;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.DmtSessionConstants;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Events;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Execute;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetChildNodeNames;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetEffectiveNodeAcl;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetLockType;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetMetaNode;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetNodeSize;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetNodeTimestamp;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetNodeVersion;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetPrincipal;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetRootUri;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetSessionId;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetSetNodeAcl;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetSetNodeTitle;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetSetNodeType;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetSetNodeValue;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.GetState;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.IsLeafNode;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.IsNodeUri;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Mangle;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.RenameNode;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.Rollback;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.SetDefaultNodeValue;
import org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TB1Service;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class Activator implements BundleActivator, TB1Service  {
	private ServiceRegistration servReg;
	public void start(BundleContext bc) throws Exception {
		servReg = bc.registerService(TB1Service.class.getName(),this,null);
	}

	public void stop(BundleContext bc) throws Exception {
		servReg.unregister();
	}

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
				 new SendAlert((DmtTestControl)tbc),
				 new org.osgi.test.cases.dmt.main.tb1.DmtAdmin.Mangle((DmtTestControl)tbc),
				 new SetDefaultNodeValue((DmtTestControl)tbc),
				 new DmtAdressingUri((DmtTestControl)tbc),
				 new TestExceptions((DmtTestControl)tbc),
				 new Mangle((DmtTestControl)tbc), 
				 new Events((DmtTestControl)tbc)
		};
	}					
				
	

}
