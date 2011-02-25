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

import info.dmtree.*;

import java.util.*;

import org.osgi.service.permissionadmin.PermissionInfo;

class SessionWrapper extends DmtSessionImpl {
    Timer timer;
    TimerTask invalidateTask;

    SessionWrapper(String principal, String subtreeUri, int lockMode,
            PermissionInfo[] permissions, Context context,
            DmtAdminCore dmtAdmin) throws DmtException {
        super(principal, subtreeUri, lockMode, permissions, context, dmtAdmin);
        
        timer = new Timer(true);
        invalidateTask = null;
    }
    
    void open() throws DmtException {
        try {
            super.open();
        } catch(PluginUnregisteredException e) {
            throw new DmtIllegalStateException(e.getMessage());
        }
        startTimer(); // this is the first time the timer is started
    }
    
    // convenience method for fatal errors in normal DMT access methods
    private void invalidateSession() {
        invalidateSession(true, false);
    }
    
    protected synchronized void invalidateSession(boolean rollback, 
            boolean timeout) {
        // timer is stopped for good if the session is invalidated
        removeTimer(); 
        super.invalidateSession(rollback, timeout);
    }
    
    public void close() throws DmtException {
        removeTimer(); // timer is stopped for good if session is closed
        try {
            super.close();
        } catch(PluginUnregisteredException e) {
            throw new DmtIllegalStateException(e.getMessage());
        }
    }
    
    public void execute(String nodeUri, String data) throws DmtException {
        stopTimer();
        try {
            super.execute(nodeUri, data);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void execute(String nodeUri, String correlator, String data)
            throws DmtException {
        stopTimer();
        try {
            super.execute(nodeUri, correlator, data);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public Acl getNodeAcl(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeAcl(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public Acl getEffectiveNodeAcl(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getEffectiveNodeAcl(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void setNodeAcl(String nodeUri, Acl acl) throws DmtException {
        stopTimer();
        try {
            super.setNodeAcl(nodeUri, acl);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void rollback() throws DmtException {
        stopTimer();
        try {
            super.rollback();
        } catch(DmtException e) {
            invalidateSession(false, false);
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } catch(RuntimeException e) {
            invalidateSession(false, false);
            throw e;
        } finally {
            startTimer();
        }
    }

    public void commit() throws DmtException {
        stopTimer();
        try {
            super.commit();
        } catch(DmtException e) {
            invalidateSession(false, false);
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } catch(RuntimeException e) {
            invalidateSession(false, false);
            throw e;
        } finally {
            startTimer();
        }
    }

    public void setNodeTitle(String nodeUri, String title) throws DmtException {
        stopTimer();
        try {
            super.setNodeTitle(nodeUri, title);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        stopTimer();
        try {
            super.setNodeValue(nodeUri, data);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        stopTimer();
        try {
            super.setDefaultNodeValue(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void setNodeType(String nodeUri, String type) throws DmtException {
        stopTimer();
        try {
            super.setNodeType(nodeUri, type);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void deleteNode(String nodeUri) throws DmtException {
        stopTimer();
        try {
            super.deleteNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void createInteriorNode(String nodeUri) throws DmtException {
        stopTimer();
        try {
            super.createInteriorNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void createInteriorNode(String nodeUri, String type)
            throws DmtException {
        stopTimer();
        try {
            super.createInteriorNode(nodeUri, type);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void createLeafNode(String nodeUri) throws DmtException {
        stopTimer();
        try {
            super.createLeafNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void createLeafNode(String nodeUri, DmtData value)
            throws DmtException {
        stopTimer();
        try {
            super.createLeafNode(nodeUri, value);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        stopTimer();
        try {
            super.createLeafNode(nodeUri, value, mimeType);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void copy(String nodeUri, String newNodeUri, boolean recursive)
            throws DmtException {
        stopTimer();
        try {
            super.copy(nodeUri, newNodeUri, recursive);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public void renameNode(String nodeUri, String newName) throws DmtException {
        stopTimer();
        try {
            super.renameNode(nodeUri, newName);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public boolean isNodeUri(String nodeUri) {
        stopTimer();
        try {
            return super.isNodeUri(nodeUri);
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }
    
    public boolean isLeafNode(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.isLeafNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeValue(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeTitle(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public String getNodeType(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeType(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeVersion(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeTimestamp(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getNodeSize(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getChildNodeNames(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }

    public MetaNode getMetaNode(String nodeUri) throws DmtException {
        stopTimer();
        try {
            return super.getMetaNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession();
            throw e;
        } catch(PluginUnregisteredException e) {
            invalidateSession();
            throw new DmtIllegalStateException(e.getMessage());
        } finally {
            startTimer();
        }
    }
    
    private synchronized void stopTimer() {
        if(invalidateTask == null)
            return;
        
        invalidateTask.cancel();
        invalidateTask = null;
    }
    
    // (re)starts the inactivity timer, but only if the session is still valid:
    // it is not closed or invalidated (i.e. removeTimer was not called) 
    private synchronized void startTimer() {
        if(timer == null)
            return;
        
        // stops previous timer, if any (there shouldn't be one)
        stopTimer(); 
        invalidateTask = new InvalidateTask();
        timer.schedule(invalidateTask, DmtAdminCore.IDLE_TIMEOUT);
    }
    
    private synchronized void removeTimer() {
        if(timer == null)
            return;
        
        stopTimer(); // this might not be needed, but it does not cause any harm
        timer.cancel();
        timer = null; // to make sure the timer is not reset again
    }
    
    private class InvalidateTask extends TimerTask {
        public void run() {
            invalidateSession(true, true);
        }
    }
}
