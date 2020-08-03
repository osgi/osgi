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
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Date;

import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;


/**
 * Wrapper class around data plugin sessions, for reducing the privileges of 
 * remote callers while the plugin is executed.
 * <p>
 * Two instances of this class are equal if the wrapped plugin sessions are
 * equal and the roots of the sessions are the same.   
 */

public class PluginSessionWrapper implements TransactionalDataSession {
	ReadableDataSession							readableDataSession			= null;
	ReadWriteDataSession						readWriteDataSession		= null;
	TransactionalDataSession					transactionalDataSession	= null;
    
    private final AccessControlContext securityContext;
    
    // the root node of the session, either one of the plugin roots or a subnode
    private final Node sessionRoot;
    
    // the registration object for the plugin providing the session, used for
    // checking that the plugin has not been unregistered
    
//    private final PluginRegistration pluginRegistration;
    // SD: pluginRegistration is just for checking that registration is still valid
    // this can be done also via a ServiceRegistration directly 
private final ServiceReference<DataPlugin>		pluginReference;
    
    
    // redundant information, could be calculated from session variables
    private final int sessionType;
    
    // caches the output of the toString() method
    private String infoString;
    
    // Note, that the session type reflects the kind of 
	public PluginSessionWrapper(ServiceReference<DataPlugin> pluginReference,
            ReadableDataSession session, int sessionType, Node sessionRoot, 
            AccessControlContext securityContext) {
        readableDataSession = session;
        if(sessionType != DmtSession.LOCK_TYPE_SHARED) {
            readWriteDataSession = (ReadWriteDataSession) session;
            if(sessionType != DmtSession.LOCK_TYPE_EXCLUSIVE)
                transactionalDataSession = (TransactionalDataSession) session;
        }
        
        this.sessionType = sessionType;
        this.sessionRoot = sessionRoot;
        this.pluginReference = pluginReference;
        this.securityContext = securityContext;
        
        infoString = null;
    }
    
    int getSessionType() {
        return sessionType;
    }
    
    Node getSessionRoot() {
        return sessionRoot;
    } 
    
    @Override
	public void nodeChanged(String[] nodePath) throws DmtException {
        // no need to override the permissions of the plugin here,
        // only internal data structures have to be modified
        
        checkRegistration(nodePath);
        readableDataSession.nodeChanged(nodePath);
    }
    
    /*
     * NOTE: if this class wraps a plugin without transactional write support,
     * this call is ignored
     */
    @Override
	public void commit() throws DmtException {
        checkRegistration(sessionRoot.getPath());
        
        // ignore commit for non-transactional plugins
        if(transactionalDataSession == null)
            return;
        
        if (securityContext == null) {                      // local caller
            transactionalDataSession.commit();
            return;
        }
        
        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    transactionalDataSession.commit();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    /*
     * NOTE: if this class wraps a plugin without transactional write support,
     * this call is ignored
     */
    @Override
	public void rollback() throws DmtException {
        checkRegistration(sessionRoot.getPath());
        
        // ignore rollback for non-transactional plugins
        if(transactionalDataSession == null)
            return;
        
        if (securityContext == null) {                      // local caller
            transactionalDataSession.rollback();
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    transactionalDataSession.rollback();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    @Override
	public void setNodeTitle(final String[] path, final String title)
            throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.setNodeTitle(path, title);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.setNodeTitle(path, title);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    @Override
	public void setNodeValue(final String[] path, final DmtData data)
            throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.setNodeValue(path, data);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.setNodeValue(path, data);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public void setNodeType(final String[] path, final String type)
            throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.setNodeType(path, type);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.setNodeType(path, type);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public void deleteNode(final String[] path) throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.deleteNode(path);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.deleteNode(path);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public void createInteriorNode(final String[] path, final String type)
            throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.createInteriorNode(path, type);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.createInteriorNode(path, type);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public void createLeafNode(final String[] path, final DmtData value, 
            final String mimeType) throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.createLeafNode(path, value, mimeType);
            return;
        }
        
        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.createLeafNode(path, value, mimeType);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    @Override
	public void copy(final String[] path, final String[] newPath,
            final boolean recursive) throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.copy(path, newPath, recursive);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.copy(path, newPath, recursive);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public void renameNode(final String[] path, final String newName)
            throws DmtException {
        checkRegistration(path);
        
        if (securityContext == null) {                      // local caller
            readWriteDataSession.renameNode(path, newName);
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readWriteDataSession.renameNode(path, newName);
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public void close() throws DmtException {
        checkRegistration(sessionRoot.getPath());
        
        if (securityContext == null) {                      // local caller
            readableDataSession.close();
            return;
        }
        

        try {                                               // remote caller
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
						public Void run() throws DmtException {
                    readableDataSession.close();
                    return null;
                }
            }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public boolean isNodeUri(final String[] path) {
        checkRegistration(path);
        
        if (securityContext == null)                        // local caller
            return readableDataSession.isNodeUri(path);
        
                                                            // remote caller
															Boolean ret = AccessController
																	.doPrivileged(
																			new PrivilegedAction<Boolean>() {
                    @Override
																				public Boolean run() {
                        return Boolean.valueOf(readableDataSession.isNodeUri(path));
                    }
                }, securityContext);
        return ret.booleanValue();
    }

    @Override
	public boolean isLeafNode(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.isLeafNode(path);
        
        try {                                               // remote caller
            Boolean isLeaf = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Boolean>() {
                        @Override
						public Boolean run() throws DmtException {
                            return Boolean.valueOf(
                                    readableDataSession.isLeafNode(path));
                        }
                    }, securityContext);
            return isLeaf.booleanValue();
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    @Override
	public DmtData getNodeValue(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getNodeValue(path);
        

        try {                                               // remote caller
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<DmtData>() {
                        @Override
						public DmtData run() throws DmtException {
                            return readableDataSession.getNodeValue(path);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public String getNodeTitle(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getNodeTitle(path);
        

        try {                                               // remote caller
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<String>() {
                        @Override
						public String run() throws DmtException {
                            return readableDataSession.getNodeTitle(path);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public String getNodeType(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getNodeType(path);
        

        try {                                               // remote caller
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<String>() {
                        @Override
						public String run() throws DmtException {
                            return readableDataSession.getNodeType(path);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public int getNodeVersion(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getNodeVersion(path);
        

        try {                                               // remote caller
            Integer ret = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Integer>() {
                        @Override
						public Integer run() throws DmtException {
                            return Integer.valueOf(readableDataSession.getNodeVersion(path));
                        }
                    }, securityContext);
            return ret.intValue();
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public Date getNodeTimestamp(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getNodeTimestamp(path);
        

        try {                                               // remote caller
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Date>() {
                        @Override
						public Date run() throws DmtException {
                            return readableDataSession.getNodeTimestamp(path);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public int getNodeSize(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getNodeSize(path);
        

        try {                                               // remote caller
            Integer ret = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Integer>() {
                        @Override
						public Integer run() throws DmtException {
                            return Integer.valueOf(
                                    readableDataSession.getNodeSize(path));
                        }
                    }, securityContext);
            return ret.intValue();
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public String[] getChildNodeNames(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getChildNodeNames(path);
        

        try {                                               // remote caller
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<String[]>() {
                        @Override
						public String[] run() throws DmtException {
                            return readableDataSession.getChildNodeNames(path);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }

    @Override
	public MetaNode getMetaNode(final String[] path) throws DmtException {
        checkRegistration(path);

        if (securityContext == null)                        // local caller
            return readableDataSession.getMetaNode(path);
        

        try {                                               // remote caller
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<MetaNode>() {
                        @Override
						public MetaNode run() throws DmtException {
                            return readableDataSession.getMetaNode(path);
                        }
                    }, securityContext);
        } catch (PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
    }
    
    @Override
	public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof PluginSessionWrapper))
            return false;
        
        PluginSessionWrapper other = (PluginSessionWrapper) obj;
        
        return sessionRoot.equals(other.sessionRoot) &&
                readableDataSession.equals(other.readableDataSession);       
    }
    
    @Override
	public int hashCode() {
        return sessionRoot.hashCode() ^ readableDataSession.hashCode();
    }
    
    @Override
	public String toString() {
        if(infoString == null) {
            infoString = "PluginSessionWrapper(" + sessionRoot + ", ";
            if(sessionType == DmtSession.LOCK_TYPE_EXCLUSIVE)
                infoString += "exclusive";
            else if(sessionType == DmtSession.LOCK_TYPE_ATOMIC)
                infoString += "atomic";
            else
                infoString += "shared";
            infoString += ", " + readableDataSession + ")";
        }
            
        return infoString;
    }
    
    private void checkRegistration(String[] path) {
    	// SD: got rid of PluginRegistration class
    	if ( pluginReference == null || pluginReference.getBundle() == null )
            throw new PluginUnregisteredException(path);
    }
}
