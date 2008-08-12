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
package org.osgi.test.cases.metatype2.tbc;

import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.service.metatype2.*;
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
	private ServiceReference ref;
	private MetaDataService MDService;
	private Bundle tb1;
	private Bundle tb2;

	/**
	 * List of test methods used in this test case.
	 */
	static String[]	methods	= new String[] {"testConstants",
											"testBasicCommands",
											"testExtendedOCD",
											"testExtendedAD",
											"testAction",
											"testListeners",
											"testLocalization"};

	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testConstants() {
		log("hidden", ExtendedAttributeDefinition.HIDDEN);
		log("maxValue", ExtendedAttributeDefinition.MAXIMUM);
		log("minValue", ExtendedAttributeDefinition.MINIMUM);
		log("readOnly", ExtendedAttributeDefinition.READ_ONLY);
		log("stepValue", ExtendedAttributeDefinition.STEP);
		log("org.osgi.metatype.filter", MetaDataListener.METATYPE_FILTER);
		log("org.osgi.metatype.category", MetaDataService.METATYPE_CATEGORY);
	}
	
	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testBasicCommands() {
		
		String[] cats;
		String[] mts;
		String[] locs;
		String ver;

		// Gets available categories
		log("List of available categories");
		cats = MDService.getAvailableCatagories();
		if (cats != null)
			listStringArray(cats);
		
		// Gets available metatype with null parameters
		log("List of available metatype for category = null");
		mts = MDService.getAvailableMetaTypes(null);
		if (mts != null)
			listStringArray(mts);

		// Gets available locales with null parameters
		try {
			mts = MDService.getMetaTypeLocales(null, null);
			log("List of available locales for category = null and metatype ID = null");
		}
		catch (NullPointerException e) {
			assertException("List of available locales for category = null and metatype ID = null: ", e.getClass(), e);
		}

		// Gets available locales with null parameters
		try {
			ver = MDService.getMetaTypeVersion(null, null);
			log("List of available verion for category = null and metatype ID = null");
		}
		catch (NullPointerException e) {
			assertException("List of available verion for category = null and metatype ID = null: ", e.getClass(), e);
		}

		// Gets list of metatypes
		if (cats != null) {
			for (int i=0; i<cats.length; i++) {
				log("List of available metatype for category = " + cats[i]);
				mts = MDService.getAvailableMetaTypes(cats[i]);
				if (mts != null)
					listStringArray(mts);
			
				for (int j=0; j<mts.length; j++) {
					log("List of locales for metatype ID = " + mts[j]);				
					locs = MDService.getMetaTypeLocales(cats[i], mts[j]);
					if (locs != null)
						listStringArray(locs);				
					log("List of version for metatype ID = " + mts[j]);
					ver = MDService.getMetaTypeVersion(cats[i], mts[j]);
					if (ver != null) log(ver);
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testExtendedOCD() {
		ExtendedObjectClassDefinition ocd;
		
		try {
			ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition(null, null, null, null);
			log("Gets OCD with all parameters set to null");
		}
		catch (Exception e) {
			assertException("Gets OCD with all parameters set to null: ", e.getClass(), e);
		}
		
		try {
			ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition("ControlUnit", null, null, null);
			log("Gets OCD with category = ControlUnit and other parameters set to null");
		}
		catch (Exception e) {
			assertException("Gets OCD with category = ControlUnit and other parameters set to null: ", e.getClass(), e);
		}

		try {
			ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition("ControlUnit", "HipModule", null, null);
			log("Gets OCD with category = ControlUnit, metatype ID = HipModule and other parameters set to null");
		}
		catch (Exception e) {
			assertException("Gets OCD with category = ControlUnit, metatype ID = HipModule and other parameters set to null: ", e.getClass(), e);
		}
		
		try {
			ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition("ControlUnit", "HipModule", "messageDigestService", null);			
			log("Gets OCD with category = ControlUnit, metatype ID = HipModule, ocd ID = messageDigestService, locale = null");
			OCDtoString(ocd);
		}
		catch (Exception e) {
			assertException("Gets OCD with category = ControlUnit, metatype ID = HipModule, ocd ID = messageDigestService, locale = null: ", e.getClass(), e);
		}
	}
	
	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testExtendedAD() {
		ExtendedObjectClassDefinition ocd;
		ExtendedAttributeDefinition ad;
		AttributeDefinition[] ads;
		Object obj;
		
		// Retrieve all attributes
		ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition("ControlUnit", "HipModule", "messageDigestService", null);
		ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		
		log("Retrieve list of attributes of ocd ID = messageDigestService");
		if (ads != null) {
			for (int i=0; i<ads.length; i++) {
				ADtoString(ads[i]);
			}		
		}
		
		// retrieve only one attribute
		ad = (ExtendedAttributeDefinition)ocd.getAttributeDefinition("Att10");		
		log("Retrieve only one attribute ID = Att10");
		if (ad != null) 
			ExADtoString(ad);
		
	}
	
	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testAction() {
		ExtendedObjectClassDefinition ocd;
		ActionDefinition[] acts;
		ActionDefinition act;
		
		// Retrieve all actions
		ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition("ControlUnit", "HipModule", "messageDigestService", null);
		acts = ocd.getActionDefinitions();
		
		log("Retrieve list of actions of ocd ID = messageDigestService");
		for (int i=0; i<acts.length; i++) {
			ActDtoString(acts[i]);
		}		
		
		// retrieve only one attribute
		act = (ActionDefinition)ocd.getActionDefinition("updateInstance");		
		log("Retrieve only one action ID = updateInstance");
		if (act != null) 
			ActDtoString(act);
	}
	
	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testLocalization() {
		ExtendedObjectClassDefinition ocd;
		ActionDefinition[] acts;
		ActionDefinition act;
		
		// Retrieve all actions
		ocd = (ExtendedObjectClassDefinition)MDService.getObjectClassDefinition("ControlUnit", "HipModule", "messageDigestService", "fr_FR");
		acts = ocd.getActionDefinitions();
		
		log("Retrieve list of actions of ocd ID = messageDigestService");
		for (int i=0; i<acts.length; i++) {
			ActDtoString(acts[i]);
		}		
	}
	
	
	/**
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	public void testListeners() {
		Bundle tb3 = null;
		
		try {
			tb3 = installBundle("tb3.jar");
			uninstallBundle(tb2);
			tb2 = installBundle("tb2.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// Uninstall bundle to avoid interference with other test methods
		try {
			if (tb3 != null)
				uninstallBundle(tb3);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
	public String[] getMethods() {
		return methods;
	}
	
	/**
	 * Checks if some prerequisites are met like getting some services
	 * 
	 * @return true if the pre-requisites are met, otherwise false
	 */
	public boolean checkPrerequisites() {
		ref = getContext().getServiceReference(MetaDataService.class.getName());
		if (ref == null)
			return false;
		MDService = (MetaDataService) getContext().getService(ref);
		if (MDService == null)
			return false;
		return true;
	}
	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot
	 * of time in debugging, clean up all possible persistent remains
	 * before the test is run. Clean up is better don in the prepare
	 * because debugging sessions can easily cause the unprepare never
	 * to be called.</remove> 
	 */
	public void prepare() {
		log("#before each run");
		
		try {
			tb1 = installBundle("tb1.jar");
			tb2 = installBundle("tb2.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean up after a run. Notice that during debugging
	 * many times the unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
		try {
			uninstallAllBundles();
		}
		catch (Exception e) {
		}
	}	
	
	/**
	 * Logs elements of an array of String.
	 * @param elts
	 */
	private void listStringArray(String[] elts) {
		if (elts != null) 
			for (int i=0; i<elts.length; i++)
				log(elts[i]);
	}
	
	/**
	 * 
	 * @version $Revision$
	 */
	private void ADtoString(AttributeDefinition ad) {
		log("AttID="+ad.getID()+", Name="+ad.getName()+", Descr="+ad.getDescription()+
			", Type="+Integer.toString(ad.getType())+", Card="+Integer.toString(ad.getCardinality()));		
	}
	
	/**
	 * 
	 * @version $Revision$
	 */
	private void ActDtoString(ActionDefinition ad) {
		ExtendedAttributeDefinition[] ads;
		
		log("ActionID="+ad.getID()+", Name="+ad.getName()+", Descr="+ad.getDescription()+
			", Type="+Integer.toString(ad.getType())+", Card="+Integer.toString(ad.getCardinality()));
		
		ads = ad.getInputArgumentDefinitions();
		if (ads != null) {
			log("with following input args:");
			for (int i=0; i<ads.length; i++)
				ADtoString(ads[i]);
		}
	}
	
	/**
	 * 
	 * @version $Revision$
	 */
	private void ExADtoString(ExtendedAttributeDefinition ad) {
		ExtendedAttributeDefinition[] ads;
		Hashtable props = null;
		
		ADtoString((AttributeDefinition)ad);
		ads = ad.getAttributeDefinitions();
		if (ads != null) {
			for (int i=0; i<ads.length; i++) {
				ExADtoString(ads[i]);
			}
		}
		
		props = (Hashtable)ad.getProperties();		
		if (props != null)
			log(props.toString());
	}
	
	/**
	 * 
	 * @version $Revision$
	 */
	private void OCDtoString(ObjectClassDefinition ocd) {
		log("OCDID="+ocd.getID()+", Name="+ocd.getName()+", Descr="+ocd.getDescription());
	}
}