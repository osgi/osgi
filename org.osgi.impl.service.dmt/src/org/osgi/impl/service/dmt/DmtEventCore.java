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

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtEvent;

/**
 * Stores all parameters of a DMT event, together with the ACLs of the affected
 * nodes at the time of the operation that triggered the event.
 */
public class DmtEventCore {
    private int type;
    private int sessionId;
    private List<Node> nodes;
    private List<Node> newNodes;
    private List<Acl> acls;
	private Dictionary<String,Object>	props;

    static int getType(String topic) {
    	if (DmtConstants.EVENT_TOPIC_ADDED.equals(topic))
    		return DmtEvent.ADDED;
    	if (DmtConstants.EVENT_TOPIC_DELETED.equals(topic))
    		return DmtEvent.DELETED;
    	if (DmtConstants.EVENT_TOPIC_REPLACED.equals(topic))
    		return DmtEvent.REPLACED;
    	if (DmtConstants.EVENT_TOPIC_RENAMED.equals(topic))
    		return DmtEvent.RENAMED;
    	if (DmtConstants.EVENT_TOPIC_COPIED.equals(topic))
    		return DmtEvent.COPIED;
    	if (DmtConstants.EVENT_TOPIC_SESSION_OPENED.equals(topic))
    		return DmtEvent.SESSION_OPENED;
    	if (DmtConstants.EVENT_TOPIC_SESSION_CLOSED.equals(topic))
    		return DmtEvent.SESSION_CLOSED;
    	return -1;
    }

    DmtEventCore(String topic, int sessionId ){
    	this( getType(topic), sessionId);
    }
    
	DmtEventCore(String topic, int sessionId,
			Dictionary<String,Object> initialProps) {
    	this( getType(topic), sessionId);
    	this.props = initialProps;
    }
    
    DmtEventCore(int type, int sessionId) {
        checkType(type);
        
        this.type = type;
        this.sessionId = sessionId;
        
        if(type != DmtEvent.SESSION_OPENED && type != DmtEvent.SESSION_CLOSED) {
            nodes = new Vector<Node>();
            acls = new Vector<Acl>();
        } else {
            nodes = null;
            acls = null;
        }
        
        if(type == DmtEvent.COPIED || type == DmtEvent.RENAMED)
            newNodes = new Vector<Node>();
        else
            newNodes = null;
    }
    
    DmtEventCore(int type, int sessionId, Node node, Node newNode, Acl acl) {
        this(type, sessionId);
        
        addNode(node, newNode, acl);
    }
    
    int getType() {
        return type;
    }
    
    int getSessionId() {
        return sessionId;
    }
    
    List<Node> getNodes() {
        return nodes;
    }
    
    List<Node> getNewNodes() {
        return newNodes;
    }
    
    List<Acl> getAcls() {
        return acls;
    }

    boolean containsNodeUnderRoot(Node root) {
        if(nodes != null && listContainsNodeUnderRoot(root, nodes))
            return true;
        
        if(newNodes != null && listContainsNodeUnderRoot(root, newNodes))
            return true;
        
        return false;
    }
    
    private boolean listContainsNodeUnderRoot(Node root, List<Node> nodeList) {
    	// seems that empty node lists should be allowed and the events passed through
    	if ( nodeList.size() == 0)
    		return true;
    	for( Node node : nodeList )
            if(root.isAncestorOf(node))
                return true;
    	
        return false;
    }
    
    void addNode(Node node, Node newNode, Acl acl) {
        if((nodes == null) != (node == null))
            throw new IllegalArgumentException("Node parameter must be null " +
                    "if and only if the event type is SESSION_OPENED or " +
                    "SESSION_CLOSED.");
        
        if((newNodes == null) != (newNode == null))
            throw new IllegalArgumentException("New node parameter must be " +
                    "null if and only if event type is not RENAMED or COPIED.");
        
        if(node != null) {
            nodes.add(node);
            acls.add(acl);
        }
        
        if(newNode != null)
            newNodes.add(newNode);
    }
    
    void excludeRoot(Node root) {
        if(nodes == null)
            throw new IllegalArgumentException("Cannot exclude nodes from " +
                    "SESSION_OPENED and SESSION_CLOSED events.");
        
        // cannot use iterator because if there is any match, items have to be
        // removed from multiple lists
        for(int k = 0; k < nodes.size(); k++)
            if((newNodes != null && root.isAncestorOf(newNodes.get(k))) ||
                    root.isAncestorOf(nodes.get(k))) {
                nodes.remove(k);
                acls.remove(k);
                if(newNodes != null)
                    newNodes.remove(k);
            }
    }
    
    String getTopic() {
        switch(type) {
        case DmtEvent.ADDED:
				return DmtConstants.EVENT_TOPIC_ADDED;
        case DmtEvent.DELETED:
				return DmtConstants.EVENT_TOPIC_DELETED;
        case DmtEvent.REPLACED:
				return DmtConstants.EVENT_TOPIC_REPLACED;
        case DmtEvent.RENAMED:
				return DmtConstants.EVENT_TOPIC_RENAMED;
        case DmtEvent.COPIED:
				return DmtConstants.EVENT_TOPIC_COPIED;
        case DmtEvent.SESSION_OPENED:
				return DmtConstants.EVENT_TOPIC_SESSION_OPENED;
        case DmtEvent.SESSION_CLOSED:
				return DmtConstants.EVENT_TOPIC_SESSION_CLOSED;
        }
        
        // never reached
        throw new IllegalArgumentException("Unknown event type.");
    }
    
    
    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("DmtEventCore(");
        sb.append(sessionId).append(", ");
        sb.append(getTopic()).append(", ");
        sb.append(nodes).append(", ");
        sb.append(newNodes).append(", ");
        sb.append(acls).append(")");
        
        return sb.toString();
    }

    static void checkType(int type) {
        if(type != DmtEvent.ADDED && type != DmtEvent.DELETED && 
                type != DmtEvent.REPLACED && type != DmtEvent.RENAMED && 
                type != DmtEvent.COPIED && type != DmtEvent.SESSION_OPENED && 
                type != DmtEvent.SESSION_CLOSED)
            throw new IllegalArgumentException("Unknown event type");
    }
    
	Dictionary<String,Object> getProperties() {
    	if (props == null)
			props = new Hashtable<>();
		return props;
    }
    
    void addProperty( String key, Object value ) {
    	getProperties().put(key, value);
    }
    
    String[] getPropertyNames() {
		return Collections.list(getProperties().keys()).toArray(new String[0]);
    }
    
    Object getProperty( String key ) {
    	return getProperties().get(key);
    }
}
