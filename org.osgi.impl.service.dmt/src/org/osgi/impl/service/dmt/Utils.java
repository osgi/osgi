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

class Utils {
    // TODO do not allow ":" in the first segment of a node URI (?)
    // TODO allow escaped (%HH) characters in URIs?  (--> segments must be passed separately to plugins)
    /* 
     * Permitted characters in a segment of a relative URI (RFC 2396):
     * - letters and digits: a-z, A-Z, 0-9
     * - mark symbols: "-", "_", ".", "!", "~", "*", "'", "(", ")"
     * - escaped characters: '% hex hex' where 'hex' is 0-9, a-f, A-F
     * - other symbols: ";", "@", "&", "=", "+", "$", ","
     * Additionally, ":" is allowed in all but the first segment of a URI 
     */
    private static final String URI_SYMBOLS = ";:@&=+$,-_.!~*'()";
    private static final String URI_DIGITS = "0123456789";
    private static final String URI_LETTERS = 
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

//    private static final String URI_HEX_DIGITS = URI_DIGITS + "abcdefABCDEF";

    private static boolean isValidChar(char c) {
    	return 
            URI_SYMBOLS.indexOf(c) != -1 ||
            URI_LETTERS.indexOf(c) != -1 ||
            URI_DIGITS.indexOf(c)  != -1;
    }
    
//    private static boolean isValidHexDigit(char c) {
//    	return URI_HEX_DIGITS.indexOf(c) != -1;
//    }
    
	static boolean isValidNodeName(String nodeName) {
        if(nodeName == null || nodeName.length() == 0 || nodeName.equals(".."))
            return false;
        
		char[] chars = nodeName.toCharArray();
        int i = 0;
		while(i < chars.length) {
            /* escaped characters not allowed currently */
//			if (chars[i] == '%') { // escaped character (in '% hex hex' form)
//				if (i + 2 >= chars.length)
//					return false; // no room for the hexadecimal digits
//				if (!isValidHexDigit(chars[i+1]) || !isValidHexDigit(chars[i+2]))
//					return false; // invalid character in escaped ch. sequence
//				i += 2;
//			}
//            else
            if(!isValidChar(chars[i]))
            	return false;
            
            i++;
		}
        
        return true;
	}

	static boolean isValidUri(String uri) {
		if (uri == null)
			return false;
		String[] nodeNames = Splitter.split(uri, '/', -1);
		for (int i = 0; i < nodeNames.length; i++)
			if (!isValidNodeName(nodeNames[i])
					|| (i != 0 && nodeNames[i].equals(".")))
				return false;
		return true;
	}

	static boolean isAbsoluteUri(String uri) {
		return uri.equals(".") || uri.startsWith("./");
	}

	// precondition: uri != null
	static String normalizeUri(String uri) {
		if (uri.charAt(uri.length() - 1) == '/')
			uri = uri.substring(0, uri.length() - 1);
		return uri;
	}

	static String normalizeAbsoluteUri(String uri) {
		if (uri == null) // not much point as long as isValidUri does not accept
						 // null
			return ".";
		uri = normalizeUri(uri);
		if (isAbsoluteUri(uri))
			return uri;
		return "./" + uri;
	}

	static String parentUri(String uri) {
		int sep = uri.lastIndexOf('/');
		if (sep == -1)
			return null;
		return uri.substring(0, sep);
	}

	static String[] normalizeAbsoluteUris(String[] uris) {
		if (uris == null)
			return null;
		String[] newUris = new String[uris.length];
		for (int i = 0; i < uris.length; i++)
			newUris[i] = normalizeAbsoluteUri(uris[i]);
		return newUris;
	}

	static boolean isAncestor(String ancestor, String node) {
		if (node.equals(ancestor))
			return true;
		return node.startsWith(ancestor + "/");
	}

	static String relativeUri(String ancestor, String node) {
		if (!isAncestor(ancestor, node))
			return null;
		if (node.length() == ancestor.length())
			return "";
		return node.substring(ancestor.length() + 1);
	}

	static String createAbsoluteUri(String ancestor, String subNode) {
		return subNode.equals("") ? ancestor : ancestor + '/' + subNode;
	}

	static String firstSegment(String uri) {
		int sep = uri.indexOf('/');
		if (sep == -1)
			return uri;
		return uri.substring(0, sep);
	}

	static String lastSegments(String uri) {
		int sep = uri.indexOf('/');
		if (sep == -1)
			return "";
		return uri.substring(sep + 1);
	}
}
