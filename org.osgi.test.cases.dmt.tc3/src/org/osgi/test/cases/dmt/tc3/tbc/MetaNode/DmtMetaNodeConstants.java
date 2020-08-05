/*
 * Copyright (c) OSGi Alliance (2004, 2011). All Rights Reserved.
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
 * Mar 09, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * Tests all constants of MetaNode
 */

public class DmtMetaNodeConstants {
	@SuppressWarnings("unused")
	private DmtTestControl tbc;

	public DmtMetaNodeConstants(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}
	
	/**
	 * Tests all constants of MetaNode
	 * 
	 * @spec 117.12.8
	 */
	public void testConstants001(){
		DefaultTestBundleControl.log("#testConstants001");
		TestCase.assertEquals("Asserts MetaNode.CMD_ADD",0,MetaNode.CMD_ADD);
		TestCase.assertEquals("Asserts MetaNode.CMD_DELETE",1,MetaNode.CMD_DELETE);
		TestCase.assertEquals("Asserts MetaNode.CMD_EXECUTE",2,MetaNode.CMD_EXECUTE);
		TestCase.assertEquals("Asserts MetaNode.CMD_GET",4,MetaNode.CMD_GET);
		TestCase.assertEquals("Asserts MetaNode.CMD_REPLACE",3,MetaNode.CMD_REPLACE);
		TestCase.assertEquals("Asserts MetaNode.AUTOMATIC",2,MetaNode.AUTOMATIC);
		TestCase.assertEquals("Asserts MetaNode.DYNAMIC",1,MetaNode.DYNAMIC);
		TestCase.assertEquals("Asserts MetaNode.PERMANENT",0,MetaNode.PERMANENT);
	}

}
