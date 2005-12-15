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
 * 23/08/2005   Alexandre Santos
 * 153          [MEGTCK][APP] Implement OAT test cases
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.util;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;


/**
 * @Author Alexandre Santos
 * A dummy bundle.
 */

public class TestingActivatorImplLow implements TestingActivator {
	private ServiceRegistration servReg;
	private Hashtable ht;

	public void start(BundleContext context) throws Exception {
		ht = new Hashtable();
		ht.put(Constants.SERVICE_RANKING, new Integer(5) );		
		servReg = context.registerService(TestingActivator.class.getName(), this, ht);
		System.out.println("Activator activated.");
	}
	
	public void startWithoutRanking(BundleContext context) throws Exception {
		servReg = context.registerService(TestingActivator.class.getName(), this, null);
		System.out.println("Activator activated.");
	}	

	public void stop(BundleContext context) throws Exception {
		servReg.unregister();
	}
	
	public void setProperties(Hashtable props) {
		servReg.setProperties(props);
	}
	
	public void resetProperties() {
		servReg.setProperties(ht);
	}	
	
	public boolean isHighest() {
		return false;
	}
	
}
