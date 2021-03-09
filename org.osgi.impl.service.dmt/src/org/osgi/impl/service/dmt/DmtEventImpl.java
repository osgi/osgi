/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.dmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    @Override
	public int getType() {
        return coreEvent.getType();
    }
    
    @Override
	public int getSessionId() {
        return coreEvent.getSessionId();
    }

    @Override
	public String[] getNodes() {
    	if ( nodeUris == null ) {
            List<Node> nodes = coreEvent.getNodes();
            if ( nodes != null ) {
				List<String> uris = new ArrayList<>();
        		SecurityManager sm = System.getSecurityManager();
            	for (int i = 0; i < nodes.size(); i++) {
            		if ( sm != null ) {
	            		try {
	        				sm.checkPermission(new DmtPermission(nodes.get(i).getUri(), DmtPermission.GET));
						} catch (SecurityException e) {
							// skip rest of this iteration
							continue;
						}
            		}
            		uris.add( nodes.get(i).getUri());
            	}
            	nodeUris = uris.toArray(new String[uris.size()]);
            }
    	}
        return nodeUris;
    }
    
    @Override
	public String[] getNewNodes() {
    	if ( newNodeUris == null ) {
            List<Node> newNodes = coreEvent.getNewNodes();
            if ( newNodes != null ) {
				List<String> uris = new ArrayList<>();
        		SecurityManager sm = System.getSecurityManager();
            	for (int i = 0; i < newNodes.size(); i++) {
            		if ( sm != null ) {
	            		try {
	        				sm.checkPermission(new DmtPermission(newNodes.get(i).getUri(), DmtPermission.GET));
						} catch (SecurityException e) {
							// skip rest of this iteration
							continue;
						}
            		}
            		uris.add( newNodes.get(i).getUri());
            	}
            	newNodeUris = uris.toArray(new String[uris.size()]);
            }
    	}
        return newNodeUris;
    }
    
    @Override
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
	@SuppressWarnings("unused")
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

	@Override
	public String[] getPropertyNames() {
		return coreEvent.getPropertyNames();
	}

	@Override
	public Object getProperty(String key) {
		return coreEvent.getProperty(key);
	}
}
