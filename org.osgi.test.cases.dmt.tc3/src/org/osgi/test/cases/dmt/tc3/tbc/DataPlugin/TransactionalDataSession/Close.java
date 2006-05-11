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
 * Feb 16, 2005  Andre Assad
 * 11		     Implement DMT Use Cases
 * ============  =================================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;

/**
 * This test case validates the implementation of <code>close</code> method, 
 * according to MEG specification
 */
public class Close {
	private DmtTestControl tbc;
	
	public Close(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	public void run() {
		testClose001();
		testClose002();
	}
	
	/**
	 * Asserts that COMMAND_FAILED is thrown if an underlying plugin failed to close
	 * 
	 * @spec ReadableDataSession.close()
	 */
	private void testClose001() {
		DmtSession session = null;
		try {
			tbc.log("#testClose001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestDataPlugin.setCloseThrowsException(true);
			session.close();
			tbc.failException("",DmtException.class);

		} catch (DmtException e) {
			tbc.assertEquals("Asserts that COMMAND_FAILED is thrown if an underlying plugin failed to close:", 
				DmtException.COMMAND_FAILED, e.getCode());
		
			if (e.getCause() instanceof DmtException) {
				DmtException exception = (DmtException)e.getCause();
				tbc
						.assertNull(
								"Asserts that DmtAdmin fowarded the DmtException with the correct subtree",exception.getURI());
				tbc
						.assertEquals(
								"Asserts that DmtAdmin fowarded the DmtException with the correct code: ",
								DmtException.CONCURRENT_ACCESS, exception.getCode());
				tbc
						.assertTrue(
								"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
								exception.getMessage().indexOf(
								    TestDataPlugin.CLOSE) > -1);
			}	else {
				tbc.failExpectedOtherException(DmtException.class,e.getCause());
			}		
		} catch (Exception e) {
			tbc.failUnexpectedException(e);	
		} finally {
		    TestDataPlugin.setCloseThrowsException(false);
		}
	}	
	/**
	 * Asserts that TRANSACTION_ERROR is thrown if an underlying plugin failed to commit
	 * 
	 * @spec ReadableDataSession.close()
	 */
	private void testClose002() {
		DmtSession session = null;
		try {
			tbc.log("#testClose002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestDataPlugin.setCommitThrowsException(true);
			session.close();
			tbc.failException("",DmtException.class);

		} catch (DmtException e) {
			tbc.assertEquals("Asserts that TRANSACTION_ERROR is thrown if an underlying plugin failed to commit:", 
					DmtException.TRANSACTION_ERROR, e.getCode());
			
			if (e.getCause() instanceof DmtException) {
				DmtException exception = (DmtException)e.getCause();
				tbc
						.assertNull(
								"Asserts that DmtAdmin fowarded the DmtException with the correct subtree",exception.getURI());
				tbc
						.assertEquals(
								"Asserts that DmtAdmin fowarded the DmtException with the correct code: ",
								DmtException.COMMAND_FAILED, exception.getCode());
				tbc
						.assertTrue(
								"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
								exception.getMessage().indexOf(
								    TestDataPlugin.COMMIT) > -1);
				
			} else {
				tbc.failExpectedOtherException(DmtException.class, e.getCause());
			}			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
		    TestDataPlugin.setCommitThrowsException(false);
		}
	}


}
