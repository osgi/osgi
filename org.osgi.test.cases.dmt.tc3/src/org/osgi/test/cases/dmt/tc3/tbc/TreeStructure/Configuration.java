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
 * Feb 24, 2005  Luiz Felipe Guimaraes
 * 31            [MEGTCK][DMT] Validation of Configuration Tree Structure
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.TreeStructure;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;

import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test class validates the implementation of Configuration Management Object
 */
public class Configuration {
    
	private DmtTestControl tbc;
	
	public final static String PID = "br.org.cesar.pid";
	
	public final static String CFG_PID = DmtConstants.OSGi_CFG + "/" + PID;
	
	public final static String CFG_PID_PID = CFG_PID +"/Pid";
	
	public final static String CFG_PID_FACTORYPID = CFG_PID +"/FactoryPid";
	
	public final static String CFG_PID_LOCATION = CFG_PID + "/Location";
	
	public final static String CFG_PID_KEYS = CFG_PID +"/Keys";
	
	public final static String CFG_PID_KEYS_KEY = CFG_PID_KEYS + "/key1";
	
	public final static String CFG_PID_KEYS_KEY_TYPE = CFG_PID_KEYS_KEY + "/Type";
	
	public final static String CFG_PID_KEYS_KEY_CARDINALITY = CFG_PID_KEYS_KEY + "/Cardinality";
	
	public final static String CFG_PID_KEYS_KEY_VALUE = CFG_PID_KEYS_KEY + "/Value";
	
	public final static String CFG_PID_KEYS_KEY_VALUES = CFG_PID_KEYS_KEY + "/Values";
	
	public final static String CFG_PID_KEYS_KEY_VALUES_0 = CFG_PID_KEYS_KEY_VALUES + "/0";
	
	
	private final DmtData defaultDataString = new DmtData("test");
	
	public Configuration(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConfiguration001();
		testConfiguration002();
		testConfiguration003();
		testConfiguration004();
		testConfiguration005();
		testConfiguration006();
		testConfiguration007();
		testConfiguration008();
		testConfiguration009();
		testConfiguration010();
		testConfiguration011();
		testConfiguration012();
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			MetaNode metaNode = session.getMetaNode(DmtConstants.OSGi_CFG);
			TestCase.assertEquals("Asserts that $/Configuration node is permanent",MetaNode.PERMANENT, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts that $/Configuration node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			
			
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt; node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			MetaNode metaNode = session.getMetaNode(CFG_PID);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid> node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertTrue("Asserts that $/Configuration/<pid> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			TestCase.assertEquals("Asserts that $/Configuration/<pid> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}

	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Location node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createLeafNode(CFG_PID_LOCATION, defaultDataString);
			MetaNode metaNode = session.getMetaNode(CFG_PID_LOCATION);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Location node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Location node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Location node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Location node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Location node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}	
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Pid node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration004");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createLeafNode(CFG_PID_PID,defaultDataString);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_PID);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Pid node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Pid node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Pid node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Pid node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Pid node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}	
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/FactoryPid node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createLeafNode(CFG_PID_FACTORYPID,defaultDataString);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_FACTORYPID);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/FactoryPid node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/FactoryPid node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/FactoryPid node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/FactoryPid node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/FactoryPid node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt; node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration007");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key> node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys/<key> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Type node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration008");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_TYPE,new DmtData("java.lang.String"));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_TYPE);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Type node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Type node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Type node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Type node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Type node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Cardinality node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_CARDINALITY,new DmtData("scalar"));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_CARDINALITY);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Cardinality node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Value node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_VALUE,new DmtData("1"));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_VALUE);
			
			int formatsAllowed = DmtData.FORMAT_STRING | DmtData.FORMAT_BINARY | DmtData.FORMAT_INTEGER | DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_FLOAT;
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Value node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Value node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Value node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Value node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Value node format is a chr node", formatsAllowed ,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Value node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Values node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createInteriorNode(CFG_PID_KEYS_KEY_VALUES);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_VALUES);
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values node format is an interior node", DmtData.FORMAT_NODE ,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Values node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}

	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Values/&lt;n&gt; node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	public void testConfiguration012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testConfiguration012");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_VALUES_0,new DmtData(10));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_VALUES_0);
			
			int formatsAllowed = DmtData.FORMAT_STRING | DmtData.FORMAT_INTEGER | DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_FLOAT;
			
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			TestCase.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			TestCase.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node format is a chr, int, bool, float", formatsAllowed ,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Values/<n> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
}
