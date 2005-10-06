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

import java.io.*;

public class ClientTest {
	
	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(args[1]);
		ClientAdaptor ca = new ClientAdaptor(null, args[0], port, 10);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (!in.readLine().equals("bye")) {
			try {
				ca.sendAlert("serverid", 1224, null, null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		ca.stop();
		in.close();
	}
	
}
