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

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.*;

import org.osgi.framework.BundleContext;
/**
 * 
 * @author Shigekuni Kondo NTT Corporation
 */
class FrameworkPlugin implements DataPlugin {
    private FrameworkReadOnlySession readonly;
    private FrameworkReadWriteSession readwrite;
    private BundleContext context = null;

    FrameworkPlugin(BundleContext context) {
    	this.context = context;
    	readonly = new FrameworkReadOnlySession(this, context);
    	readwrite = new FrameworkReadWriteSession(this, context, readonly);
    	this.context.addBundleListener(readonly);
    }
    
    public void removeBundleListener(){
    	if(this.context != null)
    		this.context.removeBundleListener(readonly);
    }
    
    @Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) throws DmtException {
    	readonly.managedWires();
        return readonly;
    }

    @Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return null; // non-atomic write sessions not supported
    }

    @Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
    	if(sessionRoot.length > 
                FrameworkPluginActivator.PLUGIN_ROOT_PATH_LENGTH + 1)
            throw new DmtException(sessionRoot, DmtException.COMMAND_FAILED,
                    "Fine-grained locking not supported, session subtree " +
                    "must contain at least one whole configuration table.");

    	readwrite.managedWires();
        return readwrite;
    }
}
