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

// Utility class to parse and store Status Variable path entries
class Path {
    //----- Static fields and methods -----//
    
    // character set copied from org.osgi.impl.service.dmt.Utils
    // duplicated in org.osgi.service.monitor.StatusVariable, keep synchronized!
    private static final String URI_CHARACTERS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" +
        "-_.!~*'()";   // ";:@&=+$," not allowed by Monitoring RFC 
        
    private static boolean containsValidChars(String name) {
        char[] chars = name.toCharArray();
        int i = 0;
        while(i < chars.length) {
            if(URI_CHARACTERS.indexOf(chars[i]) == -1)
                return false;
            i++;
        }
        
        return true;        
    }
    
    static void checkString(String str, String errorPrefix) 
            throws IllegalArgumentException {
        if(str == null)
            throw new IllegalArgumentException(errorPrefix + " is null.");
        if(str.length() == 0)
            throw new IllegalArgumentException(errorPrefix + " is empty.");
    }
    
    static void checkName(String name, String errorPrefix)
            throws IllegalArgumentException {
        checkString(name, errorPrefix);
        
        if(name.equals(".."))
            throw new IllegalArgumentException(errorPrefix + " is invalid.");
        
        if(!containsValidChars(name))
            throw new IllegalArgumentException(errorPrefix + 
                    " contains invalid characters.");
    }
    
    static Path getPath(String pathStr) throws IllegalArgumentException {
        if(pathStr == null)
            throw new IllegalArgumentException("Path argument is null.");
        
        int pos = pathStr.indexOf('/');
        if(pos < 0)
            throw new IllegalArgumentException(
                    "Path '" + pathStr + "' invalid, should have the form " + 
                    "'<Monitorable ID>/<Status Variable ID>'");
        
        Path path = new Path(pathStr.substring(0, pos), 
                pathStr.substring(pos + 1));
        
        checkName(path.monId, "Monitorable ID");
        checkName(path.varId, "Status Variable ID");

        return path;
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
