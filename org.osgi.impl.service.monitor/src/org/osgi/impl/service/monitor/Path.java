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

package org.osgi.impl.service.monitor;

import java.io.UnsupportedEncodingException;

// Utility class to parse and store Status Variable path entries
class Path {
    //----- Static fields and methods -----//
    
    // duplicated in org.osgi.service.monitor.StatusVariable, keep synchronized!
    private static final String SYMBOLIC_NAME_CHARACTERS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" +
        "-_.";   // a subset of the characters allowed in DMT URIs  
     
    // duplicated in org.osgi.service.monitor.StatusVariable, keep synchronized!
    private static final int MAX_ID_LENGTH = 32;
    
    static void checkString(String str, String errorPrefix) 
            throws IllegalArgumentException {
        if(str == null)
            throw new IllegalArgumentException(errorPrefix + " is null.");
        if(str.length() == 0)
            throw new IllegalArgumentException(errorPrefix + " is empty.");
    }
    
    static void checkName(String name, String errorPrefix, boolean allowWildcard)
            throws IllegalArgumentException {
        checkString(name, errorPrefix);
        
        byte[] nameBytes;
        try {
            nameBytes = name.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // never happens, "UTF-8" must always be supported
            throw new IllegalStateException(e.getMessage());
        }
        if(nameBytes.length > MAX_ID_LENGTH)
            throw new IllegalArgumentException(errorPrefix + " is too long " + 
                    "(over " + MAX_ID_LENGTH + " bytes in UTF-8 encoding).");
        if(name.equals(".") || name.equals(".."))
            throw new IllegalArgumentException(errorPrefix + " is invalid.");
        
        char[] chars = name.toCharArray();
        int length = chars.length;
        
        // if there is a wildcard at the end, and it is allowed, then checking
        // is stopped before it is reached
        if(allowWildcard && chars[length-1] == '*') // length != 0 checked previously
            length--;
        
        for(int i = 0; i < length; i++)
            if(SYMBOLIC_NAME_CHARACTERS.indexOf(chars[i]) == -1)
                throw new IllegalArgumentException(errorPrefix
                        + " contains invalid characters.");
    }
    
    static Path getPath(String pathStr, boolean allowWildcard)
            throws IllegalArgumentException {
        if(pathStr == null)
            throw new IllegalArgumentException("Path argument is null.");
        
        int pos = pathStr.indexOf('/');
        if(pos < 0)
            throw new IllegalArgumentException(
                    "Path '" + pathStr + "' invalid, should have the form " + 
                    "'<Monitorable ID>/<Status Variable ID>'");
        
        Path path = new Path(pathStr.substring(0, pos), 
                pathStr.substring(pos + 1));
        
        checkName(path.monId, "Monitorable ID", allowWildcard);
        checkName(path.varId, "Status Variable ID", allowWildcard);

        return path;
    }
    
    static void checkName(String name, String errorPrefix)
            throws IllegalArgumentException {
        checkName(name, errorPrefix, false);
    }

    static Path getPath(String pathStr) throws IllegalArgumentException {
        return getPath(pathStr, false);
    }
    
    static Path getPathNoCheck(String pathStr) {
        int pos = pathStr.indexOf('/');
        
        return new Path(pathStr.substring(0, pos), 
                pathStr.substring(pos + 1));
    }

    
    //----- Non-static fields and methods -----//
    
    private String monId;
    private String varId;
    
    Path(String monId, String varId) {
        this.monId = monId;
        this.varId = varId;
    }

    String getMonId() {
        return monId;
    }
    
    String getVarId() {
        return varId;
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof Path))
            return false;
        
        return ((Path) o).monId.equals(monId) && ((Path) o).varId.equals(varId);
    }
    
    public int hashCode() {
        return monId.hashCode() ^ varId.hashCode();
    }
    
    public String toString() {
        return monId + '/' + varId;
    }
}
