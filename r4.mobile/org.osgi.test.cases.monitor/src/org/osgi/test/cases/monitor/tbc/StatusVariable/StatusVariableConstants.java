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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 09/03/05 	Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.StatusVariable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;

/**
 * @author Leonardo Barros
 * 
 * This test class validates StatusVariable constants according to MEG reference
 * documentation.
 */
public class StatusVariableConstants {
	private MonitorTestControl tbc;

	public StatusVariableConstants(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}

	/**
	 * This method asserts StatusVariable constant fields.
	 * 
	 * @spec StatusVariable constant fields
	 */
	private void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserts CM_CC value", 0,
				org.osgi.service.monitor.StatusVariable.CM_CC);
		tbc.assertEquals("Asserts CM_DER value", 1,
				org.osgi.service.monitor.StatusVariable.CM_DER);
		tbc.assertEquals("Asserts CM_GAUGE value", 2,
				org.osgi.service.monitor.StatusVariable.CM_GAUGE);
		tbc.assertEquals("Asserts CM_SI value", 3,
				org.osgi.service.monitor.StatusVariable.CM_SI);
		tbc.assertEquals("Asserts TYPE_INTEGER value", 0,
				org.osgi.service.monitor.StatusVariable.TYPE_INTEGER);
		tbc.assertEquals("Asserts TYPE_FLOAT value", 1,
				org.osgi.service.monitor.StatusVariable.TYPE_FLOAT);
		tbc.assertEquals("Asserts TYPE_STRING value", 2,
				org.osgi.service.monitor.StatusVariable.TYPE_STRING);
		tbc.assertEquals("Asserts TYPE_BOOLEAN value", 3,
				org.osgi.service.monitor.StatusVariable.TYPE_BOOLEAN);
	}

}
