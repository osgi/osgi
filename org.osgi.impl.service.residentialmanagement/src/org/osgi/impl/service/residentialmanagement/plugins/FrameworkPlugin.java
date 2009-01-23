
package org.osgi.impl.service.residentialmanagement.plugins;

import info.dmtree.*;
import info.dmtree.spi.*;

import org.osgi.framework.BundleContext;

class FrameworkPlugin implements DataPlugin {
    private BundleContext context;

    FrameworkPlugin(BundleContext context) {
    	this.context = context;
    }
    
    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return new FrameworkReadOnlySession(this, context);
    }

    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        return null; // non-atomic write sessions not supported
    }

    public TransactionalDataSession openAtomicSession(String[] sessionRoot,
            DmtSession session) throws DmtException {
        
        if(sessionRoot.length > 
                FrameworkPluginActivator.PLUGIN_ROOT_PATH.length + 1)
            throw new DmtException(sessionRoot, DmtException.COMMAND_FAILED,
                    "Fine-grained locking not supported, session subtree " +
                    "must contain at least one whole configuration table.");

        return new FrameworkReadWriteSession(this, context);
    }
}
