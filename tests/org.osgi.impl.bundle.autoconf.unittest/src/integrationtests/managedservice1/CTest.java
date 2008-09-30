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

package integrationtests.managedservice1;

import integrationtests.api.ITest;

import java.io.IOException;
import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;

public class CTest implements ITest,ManagedService {
	int increment;
	boolean configured = false;;
	
	protected void activate(ComponentContext context) throws IOException {
		//System.out.println(this+" activated "+context);
		ConfigurationAdmin ca = (ConfigurationAdmin)context.locateService("configurationAdmin");
		String pid = (String) context.getProperties().get(Constants.SERVICE_PID);
		Dictionary props = ca.getConfiguration(pid).getProperties();
		if (props!=null) {
			configured = true;
			Integer i = (Integer) props.get("increment");
			increment = i.intValue();
		}
	}
	
	protected void deactivate(ComponentContext context) {
		//System.out.println(this+" deactivated "+context);
	}

	public int succ(int i) {
		if (!configured) { throw new IllegalStateException("not configured"); }
		return i+increment;
	}

	public void updated(Dictionary props) throws ConfigurationException {
		//System.out.println(this+" updated "+props);
		if (props==null) {
			configured = false;
			return;
		}
		
		Integer i = (Integer) props.get("increment");
		if (i==null) {
			configured = false;
			return;
		}
		
		increment = i.intValue();
		configured = true;
	}

}
