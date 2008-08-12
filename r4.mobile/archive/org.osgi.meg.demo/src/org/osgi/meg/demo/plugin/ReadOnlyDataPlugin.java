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

import java.util.*;

import org.osgi.framework.*;
import info.dmtree.*;
import info.dmtree.spi.*;

/**
 * Simple read-only data plugin for trial purposes.
 */
public class ReadOnlyDataPlugin implements DataPlugin, 
        ReadableDataSession, BundleActivator {
    private static final String[] PLUGIN_ROOT_PATH = new String[] {
        ".", "OSGi", "Application", "Test"
    };
    private static final String VALUE = "foobar!";
    private ServiceRegistration reg;

    public ReadableDataSession openReadOnlySession(String[] sessionRoot,
            DmtSession session) {
        return this;
    }
    
    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, 
            DmtSession session) {
        return null;
    }

    public TransactionalDataSession openAtomicSession(String[] sessionRoot, 
            DmtSession session) {
        return null;
    }
    
    public void nodeChanged(String[] nodePath) {}

    public void close() throws DmtException {}

    public boolean isNodeUri(String[] nodePath) {
        try {
            checkPath(nodePath);
        } catch(Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean isLeafNode(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        return true;
    }

    public DmtData getNodeValue(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        return new DmtData(VALUE);
    }

    public String getNodeTitle(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        return "The node that contains foobar.";
    }

    public String getNodeType(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        return "text/plain";
    }

    public int getNodeVersion(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED, 
                "Version property not supported.");
    }

    public Date getNodeTimestamp(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED, 
                "Timestamp property not supported.");
    }

    public int getNodeSize(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        return VALUE.length();
    }

    public String[] getChildNodeNames(String[] nodePath) throws DmtException {
        throw new IllegalStateException("This method should never be called!");
    }

    public MetaNode getMetaNode(String[] nodePath) throws DmtException {
        checkPath(nodePath);
        return null;
    }

    public void start(BundleContext context) throws Exception {
        Dictionary properties = new Hashtable();
        StringBuffer rootUri = new StringBuffer(".");
        for(int i = 1; i < PLUGIN_ROOT_PATH.length; i++)
            rootUri.append('/').append(PLUGIN_ROOT_PATH[i]);
        properties.put("dataRootURIs", new String[] {rootUri.toString()});
        reg = context.registerService(DataPlugin.class.getName(), 
                this, properties);
    }

    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }

    private void checkPath(String[] nodePath) throws DmtException {
        if(!Arrays.equals(nodePath, PLUGIN_ROOT_PATH))
            throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND, 
                    "Invalid node name.");
    }

}
