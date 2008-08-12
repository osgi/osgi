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
 * Feb 24, 2005  Luiz Felipe Guimaraes
 * 31            [MEGTCK][DMT] Validation of Configuration Tree Structure
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.TreeStructure;
import info.dmtree.DmtData;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test class validates the implementation of Configuration Management Object
 */
public class Configuration extends DefaultTestBundleControl {
    
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
	private void testConfiguration001() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			MetaNode metaNode = session.getMetaNode(DmtConstants.OSGi_CFG);
			tbc.assertEquals("Asserts that $/Configuration node is permanent",MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts that $/Configuration node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt; node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration002() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			MetaNode metaNode = session.getMetaNode(CFG_PID);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid> node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertTrue("Asserts that $/Configuration/<pid> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			tbc.assertEquals("Asserts that $/Configuration/<pid> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}

	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Location node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration003() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createLeafNode(CFG_PID_LOCATION, defaultDataString);
			MetaNode metaNode = session.getMetaNode(CFG_PID_LOCATION);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Location node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Location node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Location node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Location node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Location node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}	
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Pid node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration004() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration004");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createLeafNode(CFG_PID_PID,defaultDataString);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_PID);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Pid node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Pid node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Pid node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Pid node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Pid node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}	
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/FactoryPid node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration005() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createLeafNode(CFG_PID_FACTORYPID,defaultDataString);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_FACTORYPID);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/FactoryPid node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/FactoryPid node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/FactoryPid node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/FactoryPid node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/FactoryPid node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration006() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID });
		}
	}
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt; node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration007() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration007");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key> node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys/<key> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Type node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration008() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration008");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_TYPE,new DmtData("java.lang.String"));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_TYPE);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Type node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Type node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Type node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Type node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Type node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Cardinality node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration009() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_CARDINALITY,new DmtData("scalar"));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_CARDINALITY);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Cardinality node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Cardinality node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Value node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration010() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_VALUE,new DmtData("1"));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_VALUE);
			
			int formatsAllowed = DmtData.FORMAT_STRING | DmtData.FORMAT_BINARY | DmtData.FORMAT_INTEGER | DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_FLOAT;
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Value node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Value node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Value node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Value node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Value node format is a chr node", formatsAllowed ,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Value node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
	
	
	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Values node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration011() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createInteriorNode(CFG_PID_KEYS_KEY_VALUES);
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_VALUES);
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values node format is an interior node", DmtData.FORMAT_NODE ,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Values node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}

	/**
	 * Asserts the MetaNode of the $/Configuration/&lt;pid&gt;/Keys/&lt;key&gt;/Values/&lt;n&gt; node.
	 * 
	 * @spec 3.2 Configuration Management Object
	 */
	private void testConfiguration012() {
		DmtSession session = null;
		try {
			tbc.log("#testConfiguration012");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_CFG,DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(CFG_PID);
			session.createInteriorNode(CFG_PID_KEYS_KEY);
			session.createLeafNode(CFG_PID_KEYS_KEY_VALUES_0,new DmtData(10));
			
			MetaNode metaNode = session.getMetaNode(CFG_PID_KEYS_KEY_VALUES_0);
			
			int formatsAllowed = DmtData.FORMAT_STRING | DmtData.FORMAT_INTEGER | DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_FLOAT;
			
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			tbc.assertEquals("Asserts that $/Configuration/<pid>/Keys/<key>/Values/<n> node format is a chr, int, bool, float", formatsAllowed ,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Configuration/<pid>/Keys/<key>/Values/<n> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.cleanUp(session,new String[] { CFG_PID_KEYS_KEY, CFG_PID });
		}
	}
}
