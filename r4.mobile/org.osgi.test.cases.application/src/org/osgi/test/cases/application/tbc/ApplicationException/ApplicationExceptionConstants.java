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
 * Apr 5, 2005  Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.ApplicationException;

import org.osgi.service.application.ApplicationException;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;

/**
 * 
 * This Test Class Validates the <code>ApplicationException</code> constants
 * according to MEG reference documentation.
 */
public class ApplicationExceptionConstants {
	private ApplicationTestControl tbc;
	/**
	 * @param tbc
	 */
	public ApplicationExceptionConstants(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}	
	
	public void run() {
		testConstants001();
	}
	
    /**
     * This method asserts the ApplicationException constants.
     * 
     * @spec ApplicationException.ApplicationException()
     */
	private void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting APPLICATION_NOT_LAUNCHABLE value", 2, ApplicationException.APPLICATION_NOT_LAUNCHABLE);
		tbc.assertEquals("Asserting APPLICATION_INTERNAL_ERROR value", 3, ApplicationException.APPLICATION_INTERNAL_ERROR);
		tbc.assertEquals("Asserting APPLICATION_LOCKED value", 1, ApplicationException.APPLICATION_LOCKED);
		tbc.assertEquals("Asserting APPLICATION_SCHEDULING_FAILED value", 4, ApplicationException.APPLICATION_SCHEDULING_FAILED);
		tbc.assertEquals("Asserting APPLICATION_DUPLICATE_SCHEDULE_ID value", 5, ApplicationException.APPLICATION_DUPLICATE_SCHEDULE_ID);
		
	}	

}
