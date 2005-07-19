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

import java.util.ArrayList;
import java.util.List;
import org.osgi.service.dmt.DmtException;

class Utils {
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
     * @throws DmtException with code <code>INVALID_URI</code> if the node name
     *         does not meet the constraints described above
     */
    static String validateAndNormalizeNodeName(String nodeName) 
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
        
        if(sb.length() > DmtAdminImpl.segmentLengthLimit)
            throw new DmtException(nodeName, DmtException.INVALID_URI,
                    "Node name length exceeds maximum segment length limit " +
                    "of " + DmtAdminImpl.segmentLengthLimit + " characters.");
        
        return sb.toString();
    }
    
    /**
     * Checks the URI and returns the canonical form.
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
     * @return the canonicalized form of the given URI
     * @throws DmtException with code <code>INVALID_URI</code> if the URI does
     *         not meet the constraints described above
     */
    static String validateAndNormalizeUri(String uri) throws DmtException {
        if (uri == null)
            throw new DmtException(uri, DmtException.INVALID_URI,
                    "The URI parameter is null.");
        
        StringBuffer sb = new StringBuffer();
        int len = uri.length();
        int start = 0;
        for(int i = 0; i < len; i++) {
            if(uri.charAt(i) == '/') {
                if(i == len-1) // last character cannot be a '/'
                    throw new DmtException(uri, DmtException.INVALID_URI,
                            "The URI string ends with the '/' character.");
                
                if(i == 0 || uri.charAt(i-1) != '\\') {
                    appendName(sb, uri, start, i);
                    start = i+1;
                }
            }
        }
        
        appendName(sb, uri, start, len);
        
        return sb.toString();
    }

    /**
     * Checks the URI and returns the canonical form, assuming that it is an
     * absolute URI.
     * <p>
     * This method ensures that the URI is valid (see conditions in 
     * {@link #validateAndNormalizeUri}).
     * <p>
     * The canonicalization consists of removing all occurrances of the escape 
     * character <code>'\'</code> that precede characters other than
     * <code>'/'</code> and <code>'\'</code>.  If the given URI does not 
     * specify the root node <code>"."</code> as the first segment, this is
     * prepended to the canonicalized URI before returning.
     *
     * @param uri the URI string to check and canonicalize
     * @return the canonicalized form of the given URI
     * @throws DmtException with code <code>INVALID_URI</code> if the URI does
     *         not meet the constraints described above
     */
    static String validateAndNormalizeAbsoluteUri(String uri) 
            throws DmtException {
        uri = validateAndNormalizeUri(uri);
        if (isAbsoluteUri(uri))
            return uri;
        return "./" + uri;
    }
    
    private static void appendName(StringBuffer sb, String uri, 
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
    
    // precondition: URI is validated and normalized
	static boolean isAbsoluteUri(String uri) {
		return uri.equals(".") || uri.startsWith("./");
	}

    // precondition: both URIs are validated and normalized
	static boolean isAncestor(String ancestor, String node) {
		if (node.equals(ancestor))
			return true;
		return node.startsWith(ancestor + '/');
	}
    
    // precondition: both URIs are validated and normalized
    static boolean isParent(String parent, String node) {
        String nodeParent = parentUri(node);
        return nodeParent != null && nodeParent.equals(parent);
    }
    
    // returns whether two nodes are on the same branch, i.e. one is the
    // ancestor of the other
    // precondition: both URIs are validated and normalized
    static boolean isOnSameBranch(String uri1, String uri2) {
        return isAncestor(uri1, uri2) || isAncestor(uri2, uri1);
    }

    // precondition: both URIs are validated and normalized
	static String relativeUri(String ancestor, String node) {
		if (!isAncestor(ancestor, node))
			return null;
		if (node.length() == ancestor.length())
			return "";
		return node.substring(ancestor.length() + 1);
	}

    // precondition: both URIs are validated and normalized
	static String createAbsoluteUri(String ancestor, String subNode) {
		return subNode.equals("") ? ancestor : ancestor + '/' + subNode;
	}

    // precondition: URI is validated and normalized
    static String[] splitUri(String uri) {
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
                escape = true;
            } else
                segment.append(ch);
        }
        segments.add(segment.toString());
    
        return (String[]) segments.toArray(new String[] {});
    }
    
    // convenience method
    // precondition: both URIs are validated and normalized
    static String parentUri(String uri) {
        return getUriPart(uri, false, false);
    }
    
    /**
     * Returns some segment or segments of the given URI as selected by the
     * boolean parameters. First the method finds the first segment separator
     * from the direction defined by <code>forward</code>. If
     * <code>firstMatch</code> is <code>true</code>, the single URI segment
     * between the start of the search and the separator is returned (or the
     * whole URI if no separator was found). If <code>firstMatch</code> is
     * <code>false</code>, the rest of the segments are returned (or
     * <code>null</code> if there was no separator).
     * <p>
     * Precondition: the <code>uri</code> parameter is validated and normalized.
     */
    static String getUriPart(String uri, boolean forward, boolean firstMatch) {
        int sep = forward ? indexOfUnescaped(uri, '/') 
                : lastIndexOfUnescaped(uri, '/');

        if(sep == -1)
            return firstMatch ? uri : null;
        
        return forward ^ firstMatch ? uri.substring(sep + 1) 
                : uri.substring(0, sep);
    }
    
    private static int lastIndexOfUnescaped(String str, int ch) {
        int sep = str.lastIndexOf(ch);
        while(sep != -1) {
            if(sep == 0 || str.charAt(sep-1) != '\\')
                return sep;
            sep = str.lastIndexOf(ch, sep-1);
        }            
        
        return -1;
    }
    
    private static int indexOfUnescaped(String str, int ch) {
        int sep = str.indexOf(ch);
        while(sep != -1) {
            if(sep == 0 || str.charAt(sep-1) != '\\')
                return sep;
            sep = str.indexOf(ch, sep+1);
        }
        return -1;
    }
}
