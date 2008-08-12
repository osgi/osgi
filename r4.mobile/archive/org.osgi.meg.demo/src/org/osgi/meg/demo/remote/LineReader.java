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

import java.io.IOException;
import java.io.InputStreamReader;

public class LineReader {
    private InputStreamReader in;

    private int lastC;
    
    public LineReader(InputStreamReader in) {
        this.in = in;

        lastC = 0;
    }
    
    public String readLine() throws IOException {
        StringBuffer buf = new StringBuffer();
        
        if(lastC == -1) // last read finished with EOF
            return null;
        
        int c = in.read();
        if(lastC == '\r' && c == '\n')
            c = in.read();

        while(c != '\r' && c != '\n' && c != -1) {
            buf.append((char) c);
            c = in.read();
        }

        lastC = c;

        return buf.toString();
    }
}
