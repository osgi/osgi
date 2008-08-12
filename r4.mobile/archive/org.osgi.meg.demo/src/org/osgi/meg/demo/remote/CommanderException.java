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
package org.osgi.meg.demo.remote;


public class CommanderException extends Exception {
    
    private String exception;
    private String code;
    private String uri;
    private String trace;
    
    public String getException() {
        return exception;
    }
    
	public String getCode() {
		return code;
	}
    
	public String getUri() {
		return uri;
	}
    
    public String getTrace() {
        return trace;
    }
    
    public CommanderException(String exception, String code, String uri, 
            String message, String trace) {
        super(message);
        this.exception = exception;
        this.code = code;
        this.uri = uri;
        this.trace = trace;
    }
    
    public String getString() {
        String str = null;
        str = add(str, "Exception", exception); 
        str = add(str, "Code", code);
        str = add(str, "Uri", uri);
        str = add(str, "Message", getMessage());
        str = add(str, "Trace", trace);
        
        return str;
    }
    
    private String add(String orig, String name, String value) {
        if(value == null)
            return orig;
        
        String line = name + ": " + value;
        
        return (orig == null ? "" : orig + "\n") + line;
    }
}
