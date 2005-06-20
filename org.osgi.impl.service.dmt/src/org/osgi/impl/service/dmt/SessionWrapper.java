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

import java.util.Date;
import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

class SessionWrapper extends DmtSessionImpl {

    SessionWrapper(String principal, String subtreeUri, int lockMode,
            PermissionInfo[] permissions, ServiceTracker eventTracker,
            DmtPluginDispatcher dispatcher, DmtAdminImpl dmtAdmin)
            throws DmtException {
        super(principal, subtreeUri, lockMode, permissions, eventTracker,
                dispatcher, dmtAdmin);
    }

    // close() not wrapped, nothing to do if it throws an exception
    // isNodeUri() not wrapped because it cannot throw DmtExceptions 
    
    public void execute(String nodeUri, String data) throws DmtException {
        try {
            super.execute(nodeUri, data);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void execute(String nodeUri, String correlator, String data)
            throws DmtException {
        try {
            super.execute(nodeUri, correlator, data);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public DmtAcl getNodeAcl(String nodeUri) throws DmtException {
        try {
            return super.getNodeAcl(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public DmtAcl getEffectiveNodeAcl(String nodeUri) throws DmtException {
        try {
            return super.getEffectiveNodeAcl(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void setNodeAcl(String nodeUri, DmtAcl acl) throws DmtException {
        try {
            super.setNodeAcl(nodeUri, acl);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void rollback() throws DmtException {
        try {
            super.rollback();
        } catch(DmtException e) {
            invalidateSession(false);
            throw e;
        } catch(RuntimeException e) {
            invalidateSession(false);
            throw e;
        }
    }

    public void commit() throws DmtException {
        try {
            super.commit();
        } catch(DmtException e) {
            invalidateSession(false);
            throw e;
        } catch(RuntimeException e) {
            invalidateSession(false);
            throw e;
        }
    }

    public void setNodeTitle(String nodeUri, String title) throws DmtException {
        try {
            super.setNodeTitle(nodeUri, title);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        try {
            super.setNodeValue(nodeUri, data);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        try {
            super.setDefaultNodeValue(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void setNodeType(String nodeUri, String type) throws DmtException {
        try {
            super.setNodeType(nodeUri, type);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void deleteNode(String nodeUri) throws DmtException {
        try {
            super.deleteNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void createInteriorNode(String nodeUri) throws DmtException {
        try {
            super.createInteriorNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void createInteriorNode(String nodeUri, String type)
            throws DmtException {
        try {
            super.createInteriorNode(nodeUri, type);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void createLeafNode(String nodeUri) throws DmtException {
        try {
            super.createLeafNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void createLeafNode(String nodeUri, DmtData value)
            throws DmtException {
        try {
            super.createLeafNode(nodeUri, value);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType)
            throws DmtException {
        try {
            super.createLeafNode(nodeUri, value, mimeType);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void copy(String nodeUri, String newNodeUri, boolean recursive)
            throws DmtException {
        try {
            super.copy(nodeUri, newNodeUri, recursive);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public void renameNode(String nodeUri, String newName) throws DmtException {
        try {
            super.renameNode(nodeUri, newName);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
        try {
            return super.isLeafNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
        try {
            return super.getNodeValue(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        try {
            return super.getNodeTitle(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public String getNodeType(String nodeUri) throws DmtException {
        try {
            return super.getNodeType(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
        try {
            return super.getNodeVersion(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        try {
            return super.getNodeTimestamp(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        try {
            return super.getNodeSize(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        try {
            return super.getChildNodeNames(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }

    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        try {
            return super.getMetaNode(nodeUri);
        } catch(DmtException e) {
            if(e.isFatal())
                invalidateSession(true);
            throw e;
        }
    }
}
