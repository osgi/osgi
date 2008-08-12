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

public class ServerTest implements RemoteReceiver {
	
	private static final int SOCKET_TIMEOUT = 10000;
	
	RMServer	rms	= null;

	public static void main(String[] args) throws IOException {
        int port = 7777;
        if(args.length > 0)
            port = Integer.parseInt(args[0]);
		new ServerTest(port);
	}

	public ServerTest(int port) {
		System.out.println("=============================");
		System.out.println("= Management Server started =");
		System.out.println("=============================\r\n,");
		rms = new RMServer(port, SOCKET_TIMEOUT);
		rms.setReceiver(this);
		Consol c = new Consol();
		c.start();
	}

	public void onAlert(String msg) {
		System.out.println("Alert: " + msg);
	}

	public void onResult(String msg) {
		if (msg == null || (msg.trim()).equals(""))
			msg = "OK";
		System.out.println("Result: " + msg);
	}
    
    public void onError(String exception, String code, String uri, 
            String message, String trace) {
        System.out.println("Error: " + exception + ": " + 
                (code != null ? code + ": " : "") +
                (uri != null ? uri + ": " : "") +
                (message != null ? message : "") +
                "\n" + trace);
    }
    
	private class Consol extends Thread {
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				String cmd;
				while (!(cmd = in.readLine()).equals("bye")) {
					rms.setCommand(cmd);
				}
				in.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param b
	 * @see org.osgi.meg.demo.remote.RemoteReceiver#setConnected(boolean)
	 */
	public void setConnected(boolean b) {
		// TODO Auto-generated method stub
		
	}
}