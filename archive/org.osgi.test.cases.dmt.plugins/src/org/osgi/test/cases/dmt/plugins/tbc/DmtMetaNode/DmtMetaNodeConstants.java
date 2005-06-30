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
 * Mar 09, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode;

import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @generalDescription This class tests DmtExpcetion constants according with
 *                     MEG specification (rfc0085)
 */

public class DmtMetaNodeConstants {
	private DmtTestControl tbc;

	public DmtMetaNodeConstants(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}
	
	/**
	 * @testID testConstants001
	 * @testDescription Tests if constants have a correct value
	 */
	private void testConstants001(){
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserts DmtMetaNode.CMD_ADD",0,DmtMetaNode.CMD_ADD);
		tbc.assertEquals("Asserts DmtMetaNode.CMD_DELETE",1,DmtMetaNode.CMD_DELETE);
		tbc.assertEquals("Asserts DmtMetaNode.CMD_EXECUTE",2,DmtMetaNode.CMD_EXECUTE);
		tbc.assertEquals("Asserts DmtMetaNode.CMD_GET",4,DmtMetaNode.CMD_GET);
		tbc.assertEquals("Asserts DmtMetaNode.CMD_REPLACE",3,DmtMetaNode.CMD_REPLACE);
		tbc.assertEquals("Asserts DmtMetaNode.DYNAMIC",1,DmtMetaNode.DYNAMIC);
		tbc.assertEquals("Asserts DmtMetaNode.PERMANENT",0,DmtMetaNode.PERMANENT);
	}

}
