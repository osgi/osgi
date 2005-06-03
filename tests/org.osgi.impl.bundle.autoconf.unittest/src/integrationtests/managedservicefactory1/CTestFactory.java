/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package integrationtests.managedservicefactory1;

import integrationtests.api.ITest;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class CTestFactory implements BundleActivator,ManagedServiceFactory {
	
	public static final String	INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_PID	= "integrationtests.managedservicefactory1.pid";
	public static final String	INCREMENT	= "increment";

	public class CTest implements ITest {
		public int increment;
		private ServiceRegistration	serviceRegistration;
		public CTest(Dictionary properties) {
			Integer increment = (Integer) properties.get(INCREMENT);
			this.increment = increment.intValue();
			Dictionary d = new Hashtable();
			d.put(INCREMENT,increment);
			serviceRegistration = context.registerService(ITest.class.getName(),this,d);
			System.out.println("CTestFactory.CTest(increment="+increment+") registered");
		}

		public int succ(int i) {
			return i+increment;
		}
		
		public void update(Dictionary properties) {
			Integer increment = (Integer) properties.get(INCREMENT);
			int oldIncrement = this.increment;
			this.increment = increment.intValue();
			Dictionary d = new Hashtable();
			d.put(INCREMENT,increment);
			serviceRegistration.setProperties(d);
			System.out.println("CTestFactory.CTest(increment="+oldIncrement+") updated to increment="+increment+")");
		}
		
		public void unregister() {
			serviceRegistration.unregister();
			System.out.println("CTestFactory.CTest(increment="+increment+") unregistered");
		}
	}

	HashMap services = new HashMap();
	BundleContext context;
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID,INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_PID);
		context.registerService(ManagedServiceFactory.class.getName(),this,d);
	}

	public void stop(BundleContext context) throws Exception {
		// we should play nice and deregister, but who cares....
	}


	public void updated(String pid, Dictionary properties) throws ConfigurationException {
		CTest service = (CTest) services.get(pid);
		if (service==null) {
			service = new CTest(properties);
			services.put(pid,service);
		} else {
			service.update(properties);
		}
	}

	public void deleted(String pid) {
		CTest service = (CTest) services.get(pid);
		services.remove(pid);
		service.unregister();
	}

	public String getName() {
		return "foo";
	}

}
