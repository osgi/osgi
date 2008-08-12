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
 * 08/04/2005   Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ApplicationHandle;

import org.osgi.service.application.ApplicationHandle;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;

/**
 * 
 * This Test Class Validates the <code>ApplicationHandle</code> constants
 * according to MEG reference documentation.
 */
public class ApplicationHandleConstants implements TestInterface {
	private ApplicationTestControl tbc;
	/**
	 * @param tbc
	 */
	public ApplicationHandleConstants(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}	
	
	public void run() {
		testConstants001();
	}
	
	/**
	 * This method tests all constants values according
	 * to Constants fields values.
	 * 
	 * @spec 116.7.5 public abstract class ApplicationHandle
	 */	
	private void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting RUNNING value", "RUNNING", ApplicationHandle.RUNNING);
		tbc.assertEquals("Asserting STOPPING value", "STOPPING", ApplicationHandle.STOPPING);
		tbc.assertEquals("Asserting APPLICATION_DESCRIPTOR value", "application.descriptor", ApplicationHandle.APPLICATION_DESCRIPTOR);
		tbc.assertEquals("Asserting APPLICATION_STATE value", "application.state", ApplicationHandle.APPLICATION_STATE);
	}	

}
