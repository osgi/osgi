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
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.TreeStructure;

import info.dmtree.DmtData;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

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
			tbc.log("#testLog001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			MetaNode metaNode = session.getMetaNode(DmtConstants.OSGi_LOG);
			tbc.assertEquals("Asserts that $/Log node is permanent",MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH);
			tbc.assertEquals("Asserts that $/Log/<search_id> node is dynamic",MetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id> node can be added", metaNode.can(MetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Log/<search_id> node can be deleted", metaNode.can(MetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Log/<search_id> node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_FILTER);
			tbc.assertEquals("Asserts that $/Log/<search_id>/Filter node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/Filter node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog004");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_MAXRECORDS);
			tbc.assertEquals("Asserts that $/Log/<search_id>/MaxRecords node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/MaxRecords node format is an integer node", DmtData.FORMAT_INTEGER,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_EXCLUDE);
			tbc.assertEquals("Asserts that $/Log/<search_id>/Exclude node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node can be replaced", metaNode.can(MetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/Exclude node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT);
			tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/LogResult node can be gotten", metaNode.can(MetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/LogResult node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/LogResult node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog007");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0]);
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id> node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id> node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog008");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Time");
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Time node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Time node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Time node format is an interior node", DmtData.FORMAT_STRING,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Time node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Severity");
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Severity node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Severity node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Severity node format is an integer node", DmtData.FORMAT_INTEGER,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Severity node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/System");
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/System node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/System node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/System node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/System node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/SubSystem");
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/SubSystem node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/SubSystem node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/SubSystem node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/SubSystem node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog012");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Message");
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Message node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Message node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Message node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Message node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testLog013");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtConstants.LOG_SEARCH);
			String[] children = session.getChildNodeNames(DmtConstants.LOG_SEARCH_RESULT);
			if (children.length>0) {
				MetaNode metaNode = session.getMetaNode(DmtConstants.LOG_SEARCH_RESULT + "/" + children[0] + "/Data");
				tbc.assertEquals("Asserts that $/Log/<search_id>/LogResult/<record_id>/Data node is automatic",MetaNode.AUTOMATIC, metaNode.getScope());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Data node can be gotten", metaNode.can(MetaNode.CMD_GET));
				tbc.assertEquals("Asserts $/Log/<search_id>/LogResult/<record_id>/Data node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
				tbc.assertTrue("Asserts $/Log/<search_id>/LogResult/<record_id>/Data node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
			} else {
			    tbc.fail("There is no child under $/Log/<search_id>/LogResult");
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,new String [] { DmtConstants.LOG_SEARCH });
		}
	}
}
