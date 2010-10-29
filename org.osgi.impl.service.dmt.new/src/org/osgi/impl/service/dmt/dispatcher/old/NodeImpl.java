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

package org.osgi.impl.service.dmt.dispatcher.old;

import info.dmtree.*;

import java.util.ArrayList;
import java.util.List;

import org.osgi.impl.service.dmt.dispatcher.Node;

// OPTIMIZE implement operations to work on either path or URI, depending on which is available.
class NodeImpl implements Node {
    /* 
     * Permitted characters in a segment of a relative URI (RFC 2396):
     * - letters and digits: a-z, A-Z, 0-9
     * - mark symbols: "-", "_", ".", "!", "~", "*", "'", "(", ")"
     * - escaped characters: '% hex hex' where 'hex' is 0-9, a-f, A-F
     * - other symbols: ";", "@", "&", "=", "+", "$", ","
     * Additionally, ":" is allowed in all but the first segment of a URI
     * 
     * The API allows any characters, but "/" and "\" characters must be escaped
     * (preceded with "\").
     */
    

    
    private String uri;
    private String[] path;
    
    // precondition: URI is validated and normalized
    NodeImpl(String uri) {
        this.uri = uri;
        path = null;
    }
    
    // precondition: path is valid (originates from a previous Node object)
    NodeImpl(String[] path) {
        this.path = path;
        uri = null;
    }
    
    public String[] getPath() {
        if(path == null)
            path = NodeUtil.convertUriToPath(uri);
        return path;
    }
    
    public String getUri() {
        if(uri == null)
            uri = NodeUtil.convertPathToUri(path);
        return uri;
    }
    
    public String getLastSegment() {
        String[] path = getPath();
        return path.length == 0 ? null : path[path.length-1];
    }
    
    public boolean isAbsolute() {
        String[] path = getPath();
        if(path.length == 0)
            return false;
        return ".".equals(path[0]);
    }
    
    public boolean isRoot() {
        return isAbsolute() && getPath().length == 1;
    }
    
    public boolean isEmpty() {
        return getPath().length == 0;
    }
    
    // precondition: both nodes are absolute
    boolean isParentOf(NodeImpl other) {
        return isAncestorOf(other) && 
                getPath().length == other.getPath().length - 1;
    }
	
    // precondition: both nodes are absolute
    public boolean isAncestorOf(Node other) {
        return isAncestorOf(other, false);
    }
    
    // precondition: both nodes are absolute
    public boolean isAncestorOf(Node other, boolean strict) {
        String[] otherPath = other.getPath();
        String[] path = getPath();
        
        if(otherPath.length < path.length)
            return false;
        
        if(strict && otherPath.length == path.length)
            return false;
        
        for(int i = 0; i < path.length; i++)
            if(!path[i].equals(otherPath[i]))
                return false;
        
        return true;
    }
    
    // precondition: both nodes are absolute
    public boolean isOnSameBranch(Node other) {
        return isAncestorOf(other) || other.isAncestorOf(this);
    }
    
    public Node getParent() {
        if(isEmpty() || isRoot())
            return null;

        String[] path = getPath();
        String[] parent = new String[path.length-1];
        System.arraycopy(path, 0, parent, 0, path.length-1);
        return new NodeImpl(parent);
    }
    
    // precondition: parameter node is not absolute
    public Node appendRelativeNode(Node relativeNode) {
        if(relativeNode.isAbsolute())
            throw new IllegalStateException(
                    "Cannot append an absolute node to another node.");

        if(relativeNode.isEmpty())
            return this;
        
        return new NodeImpl(getUri() + '/' + relativeNode.getUri());
    }

    // precondition: segment parameter is validated and normalized
    public Node appendSegment(String segment) {
        return new NodeImpl(getUri() + '/' + segment);
    }
    
    // precondition: both nodes are absolute
    public Node getRelativeNode(Node descendentNode) {
        if (!isAncestorOf(descendentNode))
            return null;
        String uri = getUri();
        String descendentUri = descendentNode.getUri();
        
        if (uri.length() == descendentUri.length())
            return new NodeImpl("");
        return new NodeImpl(descendentUri.substring(uri.length() + 1));
    }
    
    public boolean equals(Object other) {
        if(!(other instanceof NodeImpl))
            return false;
        
        return getUri().equals(((NodeImpl) other).getUri());
    }
    
    public int hashCode() {
        return getUri().hashCode();
    }
    
    public String toString() {
        return getUri();
    }    
}
