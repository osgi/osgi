/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jun 07, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Plugins;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;


public class FatalExceptionDataPluginActivator implements BundleActivator {

	private ServiceRegistration<DataPlugin>	servReg;
	
	public static final String TEST_EXCEPTION_PLUGIN_ROOT = DmtConstants.OSGi_ROOT + "/data_plugin_exception";
	
	public static final String INEXISTENT_NODE = TEST_EXCEPTION_PLUGIN_ROOT +"/inexistent";
	
	public static final String INEXISTENT_LEAF_NODE = TEST_EXCEPTION_PLUGIN_ROOT +"/inex_leaf";
	
    private DmtTestControl tbc;
    
	private FatalExceptionDataPlugin fatalExceptionDataPlugin;

    public FatalExceptionDataPluginActivator(DmtTestControl tbc) {
        this.tbc = tbc;
    }

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		fatalExceptionDataPlugin = new FatalExceptionDataPlugin(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { TEST_EXCEPTION_PLUGIN_ROOT });
		servReg = bc.registerService(DataPlugin.class, fatalExceptionDataPlugin,
				props);
		System.out.println("FatalExceptionDataPlugin activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
