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
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
/**
 * 
 * @author Shigekuni Kondo, NTT Corporation
 */
public class FrameworkPluginActivator implements BundleActivator {
	static String[] PLUGIN_ROOT_PATH = new String[] { ".","RMT","Framework" };
	static int PLUGIN_ROOT_PATH_LENGTH = PLUGIN_ROOT_PATH.length;
	static String PLUGIN_ROOT_URI = "./Framework";
	private ServiceRegistration<DataPlugin>	servReg;
    private FrameworkPlugin     frameworkPlugin;

	@Override
	public void start(BundleContext bc) throws BundleException {
		if(RMTConstants.RMT_ROOT!=null){
			PLUGIN_ROOT_URI = RMTConstants.RMT_ROOT+"/Framework";
			StringTokenizer st = new StringTokenizer(PLUGIN_ROOT_URI,"/");
			int count = st.countTokens();
			PLUGIN_ROOT_PATH_LENGTH = count;
		}
 		frameworkPlugin = new FrameworkPlugin(bc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS, PLUGIN_ROOT_URI);
		servReg = bc.registerService(DataPlugin.class, frameworkPlugin, props);
	}

	@Override
	public void stop(BundleContext bc) throws BundleException {
		servReg.unregister();
		frameworkPlugin.removeBundleListener();
	}
}
