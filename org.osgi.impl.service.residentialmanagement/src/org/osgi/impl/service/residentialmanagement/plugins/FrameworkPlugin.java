/*
 * Copyright (c) OSGi Alliance (2000, 2012). All Rights Reserved.
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
