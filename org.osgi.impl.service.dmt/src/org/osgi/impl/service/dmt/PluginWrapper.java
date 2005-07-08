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
 * open methods from DmtSessionImpl.
 * <p>
 * This class also takes over the responsibility of properly handling plugins
 * that do not support transactions in atomic transactions.  Commit and rollback
 * calls are not propagated to such plugins, and an exception is thrown if there 
 * is an attempt to call any of their write methods. 
 */

// TODO find a way to make "privileged" calls with no security check
// (for getMetaNodeNoCheck, isLeafNoCheck, and cardinality checks with getChildNodeNames)
public class PluginWrapper implements DmtDataPlugin, DmtReadOnlyDataPlugin {
    private DmtDataPlugin dataPlugin;
    private DmtReadOnlyDataPlugin readOnlyDataPlugin;

    // always equal to one of the above members, used for type-safety 
    private DmtReadOnly dmtReadOnly;
    
    // stores the roots of the subtrees handled by the plugins
    private String[] dataRoots;
    
    private int lockMode;
    private AccessControlContext securityContext;
    
    public PluginWrapper(Plugin plugin, int lockMode,
                         AccessControlContext securityContext) {
        
        if(plugin.isReadOnlyDataPlugin()) {
            dataPlugin = null;
            readOnlyDataPlugin = plugin.getReadOnlyDataPlugin();
            dmtReadOnly = readOnlyDataPlugin;
        } else if(plugin.isWritableDataPlugin()) {
            dataPlugin = plugin.getWritableDataPlugin();
            readOnlyDataPlugin = null;
            dmtReadOnly = dataPlugin;
        } else // never happens
            throw new IllegalArgumentException("'plugin' parameter does not " +
                    "contain a 'DmtDataPlugin' or 'DmtReadOnlyDataPlugin'.");
        
        this.lockMode = lockMode;
        this.securityContext = securityContext;
        
        dataRoots = plugin.getDataRoots();
    }
    
    String[] getDataRoots() {
        return dataRoots;
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
     * NOTE: 
     * - if this class wraps a read-only plugin, this call is ignored
     * - if the (writable) plugin does not support transactions, commit is not called
     */
    public void commit() throws DmtException {
        if(dataPlugin == null) // ignore commit for read-only plugins
            return;
        
        if (securityContext == null) {                      // local caller
            commitIfTransactional();
            return;
        }

        
        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    commitIfTransactional();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    private void commitIfTransactional() throws DmtException {
        if(dataPlugin.supportsAtomic())
            dataPlugin.commit();
    }
    
    /*
     * NOTE: 
     * - if this class wraps a read-only plugin, this call is ignored
     * - if the (writable) plugin does not support transactions, rollback is not called
     */
    public void rollback() throws DmtException {
        if(dataPlugin == null) // ignore rollback for read-only plugins
            return;
        
        if (securityContext == null) {                      // local caller
            rollbackIfTransactional();
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    rollbackIfTransactional();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    private void rollbackIfTransactional() throws DmtException {
        if(dataPlugin.supportsAtomic())
            dataPlugin.rollback();
    }

    public void setNodeTitle(final String uri, final String title)
            throws DmtException {
        if (securityContext == null) {                      // local caller
            checkTransactionSupport(uri);
            dataPlugin.setNodeTitle(uri, title);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.setNodeValue(uri, data);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.setDefaultNodeValue(uri);
            return;
        }
        
        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.setNodeType(uri, type);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.deleteNode(uri);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.createInteriorNode(uri);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.createInteriorNode(uri, type);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.createLeafNode(uri);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.createLeafNode(uri, value);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.createLeafNode(uri, value, mimeType);
            return;
        }
        
        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.copy(uri, newUri, recursive);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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
            checkTransactionSupport(uri);
            dataPlugin.renameNode(uri, newName);
            return;
        }
        

        try {                                               // remote caller
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    checkTransactionSupport(uri);
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

    public boolean isLeafNode(final String uri) throws DmtException {
        if (securityContext == null)                        // local caller
            return dmtReadOnly.isLeafNode(uri);
        
        try {                                               // remote caller
            Boolean isLeaf = (Boolean) AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws DmtException {
                            return new Boolean(dmtReadOnly.isLeafNode(uri));
                        }
                    }, securityContext);
            return isLeaf.booleanValue();
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
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
    
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof PluginWrapper))
            return false;
        
        return dmtReadOnly.equals(((PluginWrapper) obj).dmtReadOnly); 
    }
    
    public int hashCode() {
        return dmtReadOnly.hashCode();
    }
    
    private void checkTransactionSupport(String uri) throws DmtException {
        if(lockMode == DmtSession.LOCK_TYPE_ATOMIC
                && !dataPlugin.supportsAtomic())
            throw new DmtException(uri, DmtException.TRANSACTION_ERROR,
                    "Write operation attempted in atomic session " + 
                    "on a non-transactional plugin.");
    }
}
