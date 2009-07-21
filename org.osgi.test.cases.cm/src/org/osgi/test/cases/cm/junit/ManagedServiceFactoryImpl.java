/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.cm.junit;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.support.compatibility.Semaphore;

public class ManagedServiceFactoryImpl implements ManagedServiceFactory {
	private final String	name;
	private final String	propertyName;
	private final Map		services	= new HashMap();
	private final Semaphore	semaphore;

	public ManagedServiceFactoryImpl(String name, String propertyName,
			Semaphore semaphore) {
		this.name = name;
		this.propertyName = propertyName;
		this.semaphore = semaphore;
	}

	public String getName() {
		return name;
	}

	public synchronized void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		try {
			CMControl.log("+++ updating " + pid);
			String data = "somedata";
			/*
			 * String data = (String) properties.get(propertyName);
			 * 
			 * if(data == null) { throw new ConfigurationException(propertyName,
			 * "not found in the properties"); }
			 */
			/* Try to get the service */
			SomeService theService = (SomeService) services.get(pid);
			/* If the service did not exist... */
			if (theService == null) {
				theService = new SomeService();
				services.put(pid, theService);
			}
			theService.setConfigData(data);
			semaphore.signal();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		CMControl.log("--- done updating " + pid);
	}

	public synchronized void deleted(java.lang.String pid) {
		services.remove(pid);
	}

	public synchronized int getNumberOfServices() {
		return services.size();
	}

	class SomeService {
		private String	configData;

		public void setConfigData(String configData) {
			this.configData = configData;
		}
	}
}
