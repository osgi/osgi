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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import info.dmtree.Acl;
import info.dmtree.DmtEvent;
import info.dmtree.security.DmtPermission;

/*
 * Wrapper class to DmtEventCore that implements the DmtEvent interface and
 * filters the list of nodes returned to the caller based on either the ACLs of
 * the principal (if given), or the Java permissions of the caller
 */
public class DmtEventImpl implements DmtEvent {
    private DmtEventCore coreEvent;
    private String principal;

    DmtEventImpl(DmtEventCore coreEvent, String principal) {
        this.coreEvent = coreEvent;
        this.principal = principal;
    }
    
    public int getType() {
        return coreEvent.getType();
    }
    
    public int getSessionId() {
        return coreEvent.getSessionId();
    }

    public String[] getNodes() {
        List nodes = coreEvent.getNodes();
        if(nodes == null)
            return null;
        
        if(principal != null)
            return filterNodesByAcls(nodes, coreEvent.getAcls(), principal);
        
        return filterNodesByPermissions(nodes);
    }
    
    public String[] getNewNodes() {
        List newNodes = coreEvent.getNewNodes();
        if(newNodes == null)
            return null;
        
        if(principal != null)
            return filterNodesByAcls(newNodes, coreEvent.getAcls(), principal);
        
        return filterNodesByPermissions(newNodes);
    }
    
    public String toString() {
        return "DmtEventImpl(" + principal + ", " + coreEvent + ")";
    }
    
    private static String[] filterNodesByPermissions(List nodes) {
        SecurityManager sm = System.getSecurityManager();

        List filteredNodes = new Vector();
        Iterator i = nodes.iterator();
        while (i.hasNext()) {
            String uri = ((Node) i.next()).getUri();
            try {
                sm.checkPermission(new DmtPermission(uri, DmtPermission.GET));
                filteredNodes.add(uri);
            } catch(SecurityException e) {
                // no GET permissions -> not adding URI to the filtered list
            }
        }
        
        return (String[]) filteredNodes.toArray(new String[] {});
    }
    
    private static String[] filterNodesByAcls(List nodes, List acls,
            String principal) {
        
        List filteredNodes = new Vector();
        
        for(int k = 0; k < nodes.size(); k++) {
            Acl acl = (Acl) acls.get(k);
            if(acl.isPermitted(principal, Acl.GET))
                filteredNodes.add(((Node) nodes.get(k)).getUri());
        }
        
        return (String[]) filteredNodes.toArray(new String[] {});
    }
}
