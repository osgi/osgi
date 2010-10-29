package org.osgi.impl.service.dmt.dispatcher.old;

import java.util.ArrayList;
import java.util.List;

import org.osgi.impl.service.dmt.dispatcher.Node;


import info.dmtree.DmtException;
import info.dmtree.Uri;

public class NodeUtil {
	public static final NodeImpl ROOT_NODE = new NodeImpl(".");

	public static String[] getUriArray(Node[] nodes) {
	    String[] uris = new String[nodes.length];
	    for(int i = 0; i < uris.length; i++)
	        uris[i] = nodes[i].getUri();
	    return uris;
	}

	// only exported as a convenience method for plugins
	public static String convertPathToUri(String[] path) {
	    if(path.length == 0)
	        return "";
	    
	    StringBuffer sb = new StringBuffer(path[0]);
	    for(int i = 1; i < path.length; i++)
	        sb.append('/').append(path[i]);
	    return sb.toString();
	}

	// convenience method for plugins
	public static boolean isEqualPath(String[] path1, String[] path2) {
	    if(path1.length != path2.length)
	        return false;
	    
	    for(int i = 0; i < path1.length; i++)
	        if(!path1[i].equals(path2[i]))
	            return false;
	        
	    return true;
	}

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
	
	/**
	 * Checks the node name and returns the canonical form.
	 * <p>
	 * This method ensures that the name is non-<code>null</code> and
	 * non-empty, that it is not equal to "..", and that the length of the
	 * canonical form is not more than the maximum segment size.
	 * <p>
	 * The canonicalization consists of removing all occurrances of the escape 
	 * character <code>'\'</code> that precede characters other than
	 * <code>'/'</code> and <code>'\'</code>.
	 * 
	 * @param nodeName the node name to check and canonicalize
	 * @return the canonicalized form of the given node name
	 * @throws DmtException with the code <code>URI_TOO_LONG</code> if the node
	 *         name is too long, or <code>INVALID_URI</code> if it does not meet
	 *         one of the other constraints described above
	 */
	public static String validateAndNormalizeNodeName(String nodeName) 
	        throws DmtException {
	    if(nodeName == null || nodeName.length() == 0)
	        throw new DmtException(nodeName, DmtException.INVALID_URI,
	                "Node name is null or empty.");
	    
	    StringBuffer sb = new StringBuffer(nodeName);
	    int i = 0;
	    while(i < sb.length()) { // length can decrease during the loop!
	        if(sb.charAt(i) == '\\') {
	            if(i == sb.length() - 1) // last character cannot be a '\'
	                throw new DmtException(nodeName, DmtException.INVALID_URI,
	                        "Node name ends with the escape character.");
	            
	            char nextCh = sb.charAt(i+1);
	            if(nextCh != '\\' && nextCh != '/')
	                sb.deleteCharAt(i); // remove the extra '\'
	            else
	                i++;
	        } else if(sb.charAt(i) == '/')
	            throw new DmtException(nodeName, DmtException.INVALID_URI,
	                    "Node name contains an unescaped '/' character.");
	        
	        i++;
	    }
	
	    if(sb.toString().equals(".."))
	        throw new DmtException(nodeName, DmtException.INVALID_URI, 
	                "Node name must not be \"..\".");
	    
	    if(sb.length() > Uri.getMaxSegmentNameLength())
	        throw new DmtException(nodeName, DmtException.URI_TOO_LONG,
	                "Node name length exceeds maximum segment length limit " +
	                "of " + Uri.getMaxSegmentNameLength() + " characters.");
	    
	    return sb.toString();
	}

	/**
	 * Checks the URI and canonicalizes it, returning the result as a node
	 * object.
	 * <p>
	 * This method ensures that the URI is non-<code>null</code> and does not
	 * end with the <code>'/'</code> character, and that all segments of the URI
	 * are valid (see conditions in {@link #validateAndNormalizeNodeName}).
	 * <p>
	 * The canonicalization consists of removing all occurrances of the escape 
	 * character <code>'\'</code> that precede characters other than
	 * <code>'/'</code> and <code>'\'</code>.
	 *
	 * @param uri the URI string to check and canonicalize
	 * @return a node object representing the canonicalized URI
	 * @throws DmtException with the code <code>URI_TOO_LONG</code> if any
	 *         segment of the URI is too long, or <code>INVALID_URI</code> if 
	 *         the URI does not meet one of the other constraints described
	 *         above
	 */
	public static NodeImpl validateAndNormalizeUri(String uri) throws DmtException {
	    if (uri == null)
	        throw new DmtException(uri, DmtException.INVALID_URI,
	                "The URI parameter is null.");
	    
	    if (uri.length() == 0) // empty relative URI
	        return new NodeImpl(uri);
	    
	    StringBuffer sb = new StringBuffer();
	    int len = uri.length();
	    int start = 0;
	    for(int i = 0; i < len; i++) {
	        if(uri.charAt(i) == '/' && (i == 0 || uri.charAt(i-1) != '\\')) {
	            if(i == len-1) // last character cannot be an unescaped '/'
	                throw new DmtException(uri, DmtException.INVALID_URI,
	                        "The URI string ends with the '/' character.");
	            appendName(sb, uri, start, i);
	            start = i+1;
	        }
	    }
	    
	    appendName(sb, uri, start, len);
	    
	    return new NodeImpl(sb.toString());
	}
	
    static void appendName(StringBuffer sb, String uri, 
            int start, int end) throws DmtException {
        String segment = uri.substring(start, end);
        if(sb.length() != 0) { // this is not the first segment
            if(segment.equals("."))
                throw new DmtException(uri, DmtException.INVALID_URI,
                        "The URI contains the \".\" node name at a position " +
                        "other than the beginning of the URI.");
            sb.append('/');
        }
        sb.append(validateAndNormalizeNodeName(segment));
    }

    static String[] convertUriToPath(String uri) {
        if(uri.length() == 0)
            return new String[] {};
        
        List segments = new ArrayList();
        StringBuffer segment = new StringBuffer();
        
        boolean escape = false;
        for(int i = 0; i < uri.length(); i++) {
            char ch = uri.charAt(i);
            
            if(escape) {
                segment.append(ch);
                escape = false;
            } else if(ch == '/') {
                segments.add(segment.toString());
                segment = new StringBuffer();
            } else if(ch == '\\') {
                segment.append(ch);
                escape = true;
            } else
                segment.append(ch);
        }
        segments.add(segment.toString());
    
        return (String[]) segments.toArray(new String[segments.size()]);
    }

	public static Node validateAndNormalizeAndCreateNode(String nodeUri) throws DmtException {
		return (Node)NodeUtil.validateAndNormalizeUri(nodeUri);
	}
}
