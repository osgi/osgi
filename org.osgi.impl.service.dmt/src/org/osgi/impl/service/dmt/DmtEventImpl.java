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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.security.DmtPermission;

/**
 * Wrapper class to DmtEventCore that implements the DmtEvent interface and
 * filters the list of nodes returned to the caller based on either the ACLs of
 * the principal (if given), or the Java permissions of the caller
 */
public class DmtEventImpl implements DmtEvent {
    private DmtEventCore coreEvent;
    private Collection<String> principals;
    private String[] nodeUris;
    private String[] newNodeUris;

    DmtEventImpl(DmtEventCore coreEvent, Collection<String> principals) {
        this.coreEvent = coreEvent;
        this.principals = principals;
    }
    
    public int getType() {
        return coreEvent.getType();
    }
    
    public int getSessionId() {
        return coreEvent.getSessionId();
    }

    public String[] getNodes() {
    	if ( nodeUris == null ) {
            List<Node> nodes = coreEvent.getNodes();
            if ( nodes != null ) {
	            if(principals != null)
	                this.nodeUris = filterNodesByAcls(nodes, coreEvent.getAcls(), principals);
	            else {
	            	nodeUris = new String[nodes.size()];
	            	for (int i = 0; i < nodes.size(); i++)
	    				nodeUris[i] = nodes.get(i).getUri();
	            }
            }
    	}
        return nodeUris;
    }
    
    public String[] getNewNodes() {
    	if ( newNodeUris == null ) {
            List<Node> newNodes = coreEvent.getNewNodes();
            if ( newNodes != null ) {
	            if(principals != null)
	                this.newNodeUris = filterNodesByAcls(newNodes, coreEvent.getAcls(), principals);
	            else {
	            	newNodeUris = new String[newNodes.size()];
	            	for (int i = 0; i < newNodes.size(); i++)
	            		newNodeUris[i] = newNodes.get(i).getUri();
	            }
            }
    	}
        return newNodeUris;
    }
    
    public String toString() {
        return "DmtEventImpl(" + principals + ", " + coreEvent + ")";
    }
    
    
    /**
     * changed for spec 2.0
     * Now filters against list of principals and adds the uri if at least one is permitted. 
     * @param nodes
     * @param acls
     * @param principals
     * @return
     */
    private static String[] filterNodesByAcls(List<Node> nodes, List<Acl> acls,
            Collection<String> principals) {

    	// for internal events the acl list can be empty
    	if ( acls == null || acls.size() == 0 )
    		return nodes.toArray(new String[]{});
    	
        Set<String> filteredNodes = new HashSet<String>();
        
        for(int k = 0; k < nodes.size(); k++) {
            Acl acl = acls.get(k);
            for (String principal : principals) {
                if(acl.isPermitted(principal, Acl.GET))
                    filteredNodes.add((nodes.get(k)).getUri());
			}
        }
        
        return filteredNodes.toArray(new String[] {});
    }

	public String[] getPropertyNames() {
		return coreEvent.getPropertyNames();
	}

	public Object getProperty(String key) {
		return coreEvent.getProperty(key);
	}
}
