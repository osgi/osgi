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
 * <remove>The TemplateControl controls is downloaded in the target and will control the
 * test run. The description of this test cases should contain the overall
 * execution of the run. This description is usuall quite minimal because the
 * main description is in the TemplateTestCase.</remove>
 * 
 * TODO Add Javadoc comment for this.
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {
	MetaTypeService mts;
	

	public boolean checkPrerequisites() {
		ServiceReference ref = getContext().getServiceReference(MetaTypeService.class.getName());
		if ( ref == null )
			return false;
		
		mts = (MetaTypeService) getContext().getService(ref);
		return true;
	}
	/**
	 * <remove>Test methods starts with "test" and are automatically
	 * picked up by the base class. The order is in the order of declaration.
	 * (It is possible to control this). Test methods should use the assert methods
	 * to test.</remove>
	 * <remove>The documentation of the test method is the test method
	 * specification. Normal java tile and html rules apply.</remove>
	 * 
	 * TODO Fill in tags
	 * @specification			<remove>Specification</remove>
	 * @interface				<remove>Related interface, e.g. org.osgi.util.measurement</remove>
	 * @specificationVersion	<remove>Version nr of the specification</remove>
	 * @methods					<remove>Related method(s)</remove>
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