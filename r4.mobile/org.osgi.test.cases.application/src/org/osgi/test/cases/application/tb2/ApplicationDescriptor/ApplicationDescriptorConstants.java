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
package org.osgi.test.cases.application.tb2.ApplicationDescriptor;

import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;

/**
 * 
 * This Test Class Validates the <code>ApplicationDescriptor</code> constants
 * according to MEG reference documentation.
 */

public class ApplicationDescriptorConstants implements TestInterface {
	private ApplicationTestControl tbc;
	/**
	 * @param tbc
	 */
	public ApplicationDescriptorConstants(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}	
	
	public void run() {
		testConstants001();
	}
	
	/**
	 * This method tests all constants values according
	 * to Constants fields values.
	 * 
	 * @spec 116.7.3 public abstract class ApplicationDescritor
	 */	
	private void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting APPLICATION_CONTAINER value", "application.container", org.osgi.service.application.ApplicationDescriptor.APPLICATION_CONTAINER);
		tbc.assertEquals("Asserting APPLICATION_COPYRIGHT value", "application.copyright", org.osgi.service.application.ApplicationDescriptor.APPLICATION_COPYRIGHT);
		tbc.assertEquals("Asserting APPLICATION_DESCRIPTION value", "application.description", org.osgi.service.application.ApplicationDescriptor.APPLICATION_DESCRIPTION);
		tbc.assertEquals("Asserting APPLICATION_DOCUMENTATION value", "application.documentation", org.osgi.service.application.ApplicationDescriptor.APPLICATION_DOCUMENTATION);	
		tbc.assertEquals("Asserting APPLICATION_ICON value", "application.icon", org.osgi.service.application.ApplicationDescriptor.APPLICATION_ICON);
		tbc.assertEquals("Asserting APPLICATION_LAUNCHABLE value", "application.launchable", org.osgi.service.application.ApplicationDescriptor.APPLICATION_LAUNCHABLE);
		tbc.assertEquals("Asserting APPLICATION_LICENSE value", "application.license", org.osgi.service.application.ApplicationDescriptor.APPLICATION_LICENSE);		
		tbc.assertEquals("Asserting APPLICATION_LOCKED value", "application.locked", org.osgi.service.application.ApplicationDescriptor.APPLICATION_LOCKED);		
		tbc.assertEquals("Asserting APPLICATION_NAME value", "application.name", org.osgi.service.application.ApplicationDescriptor.APPLICATION_NAME);
		tbc.assertEquals("Asserting APPLICATION_LOCATION value", "application.location", org.osgi.service.application.ApplicationDescriptor.APPLICATION_LOCATION);
		tbc.assertEquals("Asserting APPLICATION_VERSION value", "application.version", org.osgi.service.application.ApplicationDescriptor.APPLICATION_VERSION);
		tbc.assertEquals("Asserting APPLICATION_VISIBLE value", "application.visible", org.osgi.service.application.ApplicationDescriptor.APPLICATION_VISIBLE);
	}	

}
