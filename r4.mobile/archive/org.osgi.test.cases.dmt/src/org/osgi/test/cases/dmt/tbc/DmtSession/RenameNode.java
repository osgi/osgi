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
 * Jan 25, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Luiz Felipe Guimarães
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#renameNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>renameNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class RenameNode {
	//TODO TCs for INVALID_URI, PERMISSION_DENIED, COMMAND_NOT_ALLOWED, 
	//TODO METADATA_MISMATCH, DATA_STORE_FAILURE, TRANSACTION_ERROR and IllegalStateException
	private DmtTestControl tbc;
	
	private String leaf = DmtTestControl.SOURCE + "/filter";
	private String nameLeaf = "exclude";
	private String newLeaf = DmtTestControl.SOURCE + "/" + nameLeaf;
	
	public RenameNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	public void run() {
		testRenameNode001();
		testRenameNode002();
		testRenameNode003();
		testRenameNode004();
		testRenameNode005();
		testRenameNode006();
		testRenameNode007();
		testRenameNode008();
	}

	/**
	 * @testID testRenameNode001
	 * @testDescription This method asserts that no changes occured on the nodes
	 *                  properties, after it is renamed.
	 */
	private void testRenameNode001() {


		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(leaf, new DmtData("string"));

			DmtAcl acl = new DmtAcl(
					DmtTestControl.ACL_CESAR);
			tbc.getSession().setNodeAcl(leaf, acl);

			DmtData data = new DmtData("value");
			tbc.getSession().setNodeValue(leaf, data);

			tbc.getSession().renameNode(leaf, nameLeaf);
			tbc.assertEquals("Asserting same node value", data, tbc
					.getSession().getNodeValue(newLeaf));
			tbc.assertEquals("Asserting same node ACL", acl, tbc.getSession()
					.getNodeAcl(newLeaf));
		} catch (DmtException e) {
	        tbc.fail("Unexpected exception");
		} finally {
			tbc.cleanUp(new String[] { newLeaf, leaf, DmtTestControl.SOURCE });
		}
	}

	/**
	 * @testID testRenameNode002
	 * @testDescription This method asserts that children names of the parent
	 *                  node has correctly changed.
	 */
	private void testRenameNode002()  {

		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(leaf, new DmtData("string"));

			tbc.getSession().renameNode(leaf, nameLeaf);
			// test that child node names has changed
			for (int i = 0; i < tbc.getSession().getChildNodeNames(DmtTestControl.SOURCE).length; i++) {
				if (tbc.getSession().getChildNodeNames(DmtTestControl.SOURCE)[i]
						.equals("exclude")) {
					tbc.pass("The new leaf node name has been found");
				} else {
					tbc.fail("The new leaf node name has not been found");
				}
			}
		} catch (DmtException e) {
	        tbc.fail("Unexpected exception");
		} finally {
			// just in case rename does not work attempt to delete the old leaf
			// name
			tbc.cleanUp(new String[] { newLeaf, leaf, DmtTestControl.SOURCE });
		}
	}
	/**
	 * @testID testRenameNode003
	 * @testDescription This method tests if a DmtException.NODE_NOT_FOUND  
	 *                  is thrown correctly
	 */
	private void testRenameNode003() {

		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().renameNode(leaf, nameLeaf);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is NODE_NOT_FOUND",DmtException.NODE_NOT_FOUND,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { 	DmtTestControl.SOURCE });
		}
	}	
	/**
	 * @testID testRenameNode004
	 * @testDescription This method tests if a DmtException.COMMAND_FAILED  
	 *                  is thrown correctly
	 */
	private void testRenameNode004() {

		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(leaf, new DmtData("string"));			
			tbc.getSession().renameNode(leaf, DmtTestControl.INVALID_LEAFNAME );
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is COMMAND_FAILED",DmtException.COMMAND_FAILED,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, DmtTestControl.SOURCE });
		}
	}	
	
	/**
	 * @testID testRenameNode005
	 * @testDescription This method tests if a DmtException.OTHER_ERROR 
	 *                  is thrown correctly
	 */
	private void testRenameNode005() {

		try {
            tbc.getSession(DmtTestControl.SOURCE);
            tbc.getSession().renameNode(DmtTestControl.DESTINY, nameLeaf );
			tbc.failException("#",DmtException.class);	
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR",DmtException.OTHER_ERROR,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		}
	}	
	/**
	 * @testID testRenameNode006
	 * @testDescription This method tests if a IllegalStateException   
	 *                  is thrown correctly when a session is already closed
	 */
	private void testRenameNode006() {

		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(leaf, new DmtData("string"));			
			tbc.getSession().close();
			tbc.getSession().renameNode(leaf, nameLeaf);			
			tbc.failException("#",IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The exception was IllegalStateException");
        } catch (Exception e) {
        	tbc.fail("Expected " + IllegalStateException.class.getName() + " but was " + e.getClass().getName());
        }
	}	
	/**
	 * @testID testRenameNode007
	 * @testDescription This method tests if a IllegalStateException   
	 *                  is thrown correctly when a session is already rolled back
	 */
	private void testRenameNode007() {

		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(leaf, new DmtData("string"));			
			tbc.getSession().rollback();
			tbc.getSession().renameNode(leaf, nameLeaf);			
			tbc.failException("#",IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The exception was IllegalStateException");
        } catch (Exception e) {
        	tbc.fail("Expected " + IllegalStateException.class.getName() + " but was " + e.getClass().getName());
        }
	}	
	/**
	 * @testID testRenameNode008
	 * @testDescription This method tests if a DmtException.URI_TOO_LONG  
	 *                  is thrown correctly
	 */
	private void testRenameNode008()  {

		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(leaf, new DmtData("string"));			
			tbc.getSession().renameNode(leaf, DmtTestControl.LONG_NAME );
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG",DmtException.URI_TOO_LONG,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, DmtTestControl.SOURCE });
		}

	}

}
