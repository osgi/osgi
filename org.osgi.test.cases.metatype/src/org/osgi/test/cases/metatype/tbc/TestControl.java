/*
 * $Header$
 * 
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
package org.osgi.test.cases.metatype.tbc;

import java.util.Arrays;

import org.osgi.framework.*;
import org.osgi.service.metatype.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * Get a Meta Type Service and do diverse tests.
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {
	MetaTypeService mts;
	

	/**
	 * Check if the meta type service is present.
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#checkPrerequisites()
	 */
	public boolean checkPrerequisites() {
		ServiceReference ref = getContext().getServiceReference(MetaTypeService.class.getName());
		if ( ref == null )
			return false;
		
		mts = (MetaTypeService) getContext().getService(ref);
		return true;
	}
	/**
	 * @specification			Meta Type Service
	 * @interface				MetaTypeInformation
	 * @specificationVersion	1.0
	 */
	public void testMTS() throws Exception {
		Bundle b = installBundle("tb1.jar");
		MetaTypeInformation mti = mts.getMetaTypeInformation(b);
		
		System.out.println("Pids " + Arrays.asList(mti.getPids()));
		System.out.println("Factory " + Arrays.asList(mti.getFactoryPids()));

		ObjectClassDefinition	ocd = mti.getObjectClassDefinition("0.123.1.2", "en");
		assertEquals("Default", ocd.getName(), "Person(default)");
		
		ocd = mti.getObjectClassDefinition("0.123.1.2", "du_NL_fries");
		assertEquals("du_NL_fries -> du_NL", "Persoon(du_NL)", ocd.getName() );
		
		ocd = mti.getObjectClassDefinition("0.123.1.2", "du_NL");
		assertEquals("du_NL -> du_NL","Persoon(du_NL)", ocd.getName());
		
		ocd = mti.getObjectClassDefinition("0.123.1.2", "du");
		assertEquals("du -> du", "Persoon(du)", ocd.getName());
		
		ocd = mti.getObjectClassDefinition("0.123.1.2", null);
		assertEquals("default","Person(default)", ocd.getName());
		
		ocd = mti.getObjectClassDefinition("0.123.1.2", "");
		assertEquals("raw", "Person", ocd.getName());
		b.uninstall();
	}

}