/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */

package org.osgi.impl.service.dmt;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Date;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtReadOnly;
import org.osgi.service.dmt.DmtReadOnlyDataPlugin;
import org.osgi.service.dmt.DmtSession;

/**
 * Wrapper class around data plugins, for reducing the privileges of remote
 * callers while the plugin is executed. Additionally, it hides the two types of
 * open methods from DmtSessionImpl, and in case of rollback it calls close
 * instead for read-only plugins.
 */

// TODO should the write methods check if this wraps a writeable plugin?
// TODO should the rollback method check if this wraps a transactional plugin?
// TODO should this wrapper decide whether to roll back or close?
public class PluginWrapper implements DmtDataPlugin, DmtReadOnlyDataPlugin {
    private DmtDataPlugin dataPlugin;
    private DmtReadOnlyDataPlugin readOnlyDataPlugin;

    private DmtReadOnly dmtReadOnly;
    
    private AccessControlContext securityContext;
    
    public PluginWrapper(Object plugin, AccessControlContext securityContext) {
        if(plugin instanceof DmtDataPlugin) {
            dataPlugin = (DmtDataPlugin) plugin;
            readOnlyDataPlugin = null;
        } else if(plugin instanceof DmtReadOnlyDataPlugin) {
            dataPlugin = null;
            readOnlyDataPlugin = (DmtReadOnlyDataPlugin) plugin;
        } else // never happens
            throw new IllegalArgumentException("'plugin' parameter is not " +
                    "of the type 'DmtDataPlugin' or 'DmtReadOnlyDataPlugin'.");
        
        dmtReadOnly = (DmtReadOnly) plugin;
        
        this.securityContext = securityContext;
    }
    
    /*
     * NOTE: This is a combination of the DmtDataPlugin.open and
     * DmtReadOnlyDataPlugin.open methods: if this class wraps a read-only
     * plugin, this call is redirected to the read-only version of open.
     */
    public void open(final String uri, final int lockMode, final DmtSession session)
            throws DmtException {
        if(dataPlugin == null) { // redirect to open/2 for read-only plugins
            open(uri, session);
            return;
        }
        
        if (securityContext == null) {                      // local caller
            dataPlugin.open(uri, lockMode, session);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.open(uri, lockMode, session);
                    return null;
                }
            }, securityContext);
        } catch(PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public boolean supportsAtomic() {
        if (securityContext == null)                        // local caller
            return dataPlugin.supportsAtomic();
        
        Object ret = AccessController.doPrivileged(         // remote caller
                new PrivilegedAction() {
                    public Object run() {
                        return new Boolean(dataPlugin.supportsAtomic());
                    }
                }, securityContext);
        return ((Boolean) ret).booleanValue(); 
    }

    public void open(final String uri, final DmtSession session) throws DmtException {
        if (securityContext == null) {                      // local caller
            readOnlyDataPlugin.open(uri, session);
            return;
        }

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    readOnlyDataPlugin.open(uri, session);
                    return null;
                }
            }, securityContext);
        } catch(PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    /*
     * NOTE: if this class wraps a read-only plugin, this call is ignored
     */
    public void commit() throws DmtException {
        // TODO what happens with DmtDataPlugins that do not support transactions?
        if(dataPlugin == null) // ignore commit for read-only plugins
            return;
        
        if (securityContext == null) {                      // local caller
            dataPlugin.commit();
            return;
        }

        
        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.commit();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    /*
     * NOTE: if this class wraps a read-only plugin, this call is ignored
     */
    public void rollback() throws DmtException {
        // TODO what happens with DmtDataPlugins that do not support transactions?
        if(dataPlugin == null) // ignore rollback for read-only plugins
            return;
        
        if (securityContext == null) {                      // local caller
            dataPlugin.rollback();
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.rollback();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void setNodeTitle(final String uri, final String title)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.setNodeTitle(uri, title);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.setNodeTitle(uri, title);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void setNodeValue(final String uri, final DmtData data)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.setNodeValue(uri, data);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.setNodeValue(uri, data);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void setDefaultNodeValue(final String uri)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.setDefaultNodeValue(uri);
            return;
        }
        
        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.setDefaultNodeValue(uri);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void setNodeType(final String uri, final String type)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.setNodeType(uri, type);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.setNodeType(uri, type);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void deleteNode(final String uri) throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.deleteNode(uri);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.deleteNode(uri);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void createInteriorNode(final String uri) throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.createInteriorNode(uri);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.createInteriorNode(uri);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void createInteriorNode(final String uri, final String type)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.createInteriorNode(uri, type);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.createInteriorNode(uri, type);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void createLeafNode(final String uri) throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.createLeafNode(uri);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.createLeafNode(uri);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    public void createLeafNode(final String uri, final DmtData value)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.createLeafNode(uri, value);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.createLeafNode(uri, value);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    public void createLeafNode(final String uri, final DmtData value, 
            final String mimeType) throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.createLeafNode(uri, value, mimeType);
            return;
        }
        
        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.createLeafNode(uri, value, mimeType);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    public void copy(final String uri, final String newUri,
            final boolean recursive) throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.copy(uri, newUri, recursive);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.copy(uri, newUri, recursive);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void renameNode(final String uri, final String newName)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            dataPlugin.renameNode(uri, newName);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dataPlugin.renameNode(uri, newName);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public void close() throws DmtException {
        if (securityContext == null) {                      // local caller
            dmtReadOnly.close();
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    dmtReadOnly.close();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public boolean isNodeUri(final String uri) {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.isNodeUri(uri);
        
                                                            // remote caller
        Boolean ret = (Boolean) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return new Boolean(dmtReadOnly.isNodeUri(uri));
                    }
                }, securityContext);
        return ret.booleanValue();
    }

    public DmtData getNodeValue(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getNodeValue(uri);
        

        try {                                               // remote caller
            return (DmtData) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return dmtReadOnly.getNodeValue(uri);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public String getNodeTitle(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getNodeTitle(uri);
        

        try {                                               // remote caller
            return (String) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return dmtReadOnly.getNodeTitle(uri);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public String getNodeType(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getNodeType(uri);
        

        try {                                               // remote caller
            return (String) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return dmtReadOnly.getNodeType(uri);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public int getNodeVersion(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getNodeVersion(uri);
        

        try {                                               // remote caller
            Integer ret = (Integer) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return new Integer(dmtReadOnly.getNodeVersion(uri));
                        }
                    }, securityContext);
            return ret.intValue();
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public Date getNodeTimestamp(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getNodeTimestamp(uri);
        

        try {                                               // remote caller
            return (Date) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return dmtReadOnly.getNodeTimestamp(uri);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public int getNodeSize(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getNodeSize(uri);
        

        try {                                               // remote caller
            Integer ret = (Integer) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return new Integer(dmtReadOnly.getNodeSize(uri));
                        }
                    }, securityContext);
            return ret.intValue();
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public String[] getChildNodeNames(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getChildNodeNames(uri);
        

        try {                                               // remote caller
            return (String[]) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return dmtReadOnly.getChildNodeNames(uri);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    public DmtMetaNode getMetaNode(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.getMetaNode(uri);
        

        try {                                               // remote caller
            return (DmtMetaNode) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return dmtReadOnly.getMetaNode(uri);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
}
