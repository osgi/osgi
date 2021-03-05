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
 * 1             Implement MEG TCK
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
 * This test case validates the Log Management Object
 * 
 */
public class Log {
	private DmtTestControl tbc;

	public Log(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testLog001();
		testLog002();
		testLog003();
		testLog004();
		testLog005();
		testLog006();
		testLog007();
		testLog008();
		testLog009();
		testLog010();
		testLog011();
		testLog012();
		testLog013();
		


	}


	
	/**
	 * Asserts the MetaNode of the $/Log node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			MetaNode metaNode = session.getMetaNode(DmtConstants.OSGi_LOG);
			TestCase.assertEquals("Asserts that $/Log node is permanent",MetaNode.PERMANENT, metaNode.getScope());
			TestCase.assertTrue("Asserts $/Log node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts $/Log node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Log node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);		
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt; node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH);
			TestCase.assertEquals("Asserts that $/Log/<search_id> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			TestCase.assertTrue("Asserts $/Log/<search_id> node can be added", metaNode.can(MetaNode.CMD_ADD));
			TestCase.assertTrue("Asserts $/Log/<search_id> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			TestCase.assertTrue("Asserts $/Log/<search_id> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts $/Log/<search_id> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Log/<search_id> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}

	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/Filter node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_FILTER);
			TestCase.assertEquals("Asserts that $/Log/<search_id>/Filter node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			TestCase.assertTrue("Asserts $/Log/<search_id>/Filter node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			TestCase.assertTrue("Asserts $/Log/<search_id>/Filter node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts $/Log/<search_id>/Filter node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Log/<search_id>/Filter node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/MaxRecords node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog004");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_MAXRECORDS);
			TestCase.assertEquals("Asserts that $/Log/<search_id>/MaxRecords node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			TestCase.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			TestCase.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts $/Log/<search_id>/MaxRecords node format is an integer node", DmtData.FORMAT_INTEGER,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Log/<search_id>/MaxRecords node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH});
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/Exclude node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_EXCLUDE);
			TestCase.assertEquals("Asserts that $/Log/<search_id>/Exclude node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			TestCase.assertTrue("Asserts $/Log/<search_id>/Exclude node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			TestCase.assertTrue("Asserts $/Log/<search_id>/Exclude node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts $/Log/<search_id>/Exclude node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Log/<search_id>/Exclude node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT);
			TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult node can be gotten", metaNode.can(MetaNode.CMD_GET));
			TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/<record_id> node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog007");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0]);
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id> node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id> node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/&lt;record_id&gt;/Time node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog008");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Time");
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Time node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Time node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Time node format is an interior node", DmtData.FORMAT_STRING,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Time node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/&lt;record_id&gt;/Severity node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Severity");
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Severity node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Severity node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Severity node format is an integer node", DmtData.FORMAT_INTEGER,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Severity node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/&lt;record_id&gt;/System node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/System");
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/System node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/System node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/System node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/System node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/&lt;record_id&gt;/SubSystem node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/SubSystem");
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/SubSystem node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/SubSystem node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/SubSystem node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/SubSystem node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/&lt;record_id&gt;/Message node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog012");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Message");
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Message node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Message node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Message node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Message node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
	/**
	 * Asserts the MetaNode of the $/Log/&lt;search_id&gt;/LogResult/&lt;record_id&gt;/Data node.
	 * 
	 * @spec 3.3 Log Management Object
	 */					

	public void testLog013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testLog013");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Data");
				TestCase.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Data node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Data node can be gotten", metaNode.can(MetaNode.CMD_GET));
				TestCase.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Data node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				TestCase.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Data node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    TestCase.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
}
