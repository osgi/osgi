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

package org.osgi.meg.demo.plugin;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtReadOnlyDataPlugin;
import org.osgi.service.dmt.DmtSession;

/**
 * Simple read-only data plugin for trial purposes.
 */
public class ReadOnlyDataPlugin implements DmtReadOnlyDataPlugin, BundleActivator {
    private static final String PLUGIN_ROOT = "./OSGi/Application/Test";
    private static final String VALUE = "foobar!";
    private ServiceRegistration reg;

    public void open(String subtreeUri, DmtSession session) throws DmtException {}

    public void close() throws DmtException {}

    public boolean isNodeUri(String nodeUri) {
        try {
            checkPath(nodeUri);
        } catch(Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean isLeafNode(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return true;
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return new DmtData(VALUE);
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return "The node that contains foobar.";
    }

    public String getNodeType(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return "text/plain";
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return 0;
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return new Date(0);
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return VALUE.length();
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        throw new IllegalStateException("This method should never be called!");
    }

    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        checkPath(nodeUri);
        return null;
    }

    public void start(BundleContext context) throws Exception {
        Dictionary properties = new Hashtable();
        properties.put("dataRootURIs", new String[] {PLUGIN_ROOT});
        reg = context.registerService(DmtReadOnlyDataPlugin.class.getName(), this, properties);
    }

    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }

    private void checkPath(String nodeUri) throws DmtException {
        if(!nodeUri.equals(PLUGIN_ROOT))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "Invalid node name.");
    }

}
