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

import info.dmtree.DmtAdmin;
import info.dmtree.notification.AlertItem;
import info.dmtree.notification.spi.RemoteAlertSender;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* Description of the protocol at the end of this file */

public class ClientAdaptor implements RemoteAlertSender {
	
	private boolean				goon	 = true;
	private String				message	 = null;
	private CommandProcessor	cp		 = null;
	private int					pingTime;

	public ClientAdaptor(DmtAdmin fact, String host, int port, int pingTime) throws Exception {
		this.pingTime = pingTime;
		CommandThread ct = new CommandThread(host, port);
		cp = new CommandProcessor(fact);
		ct.start();
	}

	public void stop() {
		goon = false;
	}

	public void sendAlert(String serverid, int code, String correlator, 
                          AlertItem[] items)
            throws Exception {
        String msg = "Code:" + code + "\n";
        msg += "Correlator:" + correlator + "\n";
        
        String s = ",\n";
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                msg += "Item# " + i + ":" + "Source:" + items[i].getSource()
                        + s + "Data:" + items[i].getData() + s + "Type:"
                        + items[i].getType() + "\n";
            }
        }
        this.message = msg;
    }

	private class CommandThread extends Thread {

		private Socket			s		= null;
		private PrintWriter		out		= null;
		private LineReader	    in		= null;

		public CommandThread(String host, int port) throws Exception {
			super("ClientAdaptor");
			try {
				s = new Socket(host, port);
				out = new PrintWriter(s.getOutputStream(), true);
				in = new LineReader(new InputStreamReader(s.getInputStream()));
			}
			catch (Exception e) {
				if (null != s)
					try {
						s.close();
					}
					catch (IOException ee) {
						ee.printStackTrace();
					}
				throw e;
			}
		}

		public void run() {
			String command;
			String result;
			try {
				while (goon) {
					try {
						Thread.sleep(pingTime);
					}
					catch (InterruptedException e) {
					}
					if (message != null) { //alert
						out.println("alert:");
						out.println(message);
						out.println("block_end");
						message = null;
						result = in.readLine();
						if (!result.equals("alert_ok")) {
							System.out
									.println("Protocol error, alert_ok expected");
						}
						continue;
					}
					out.println("ping");
					result = in.readLine();
					if (null == result) {
						goon = false;
						continue;
					}
					if (result.equals("ping_ok"))
						continue;
					if (result.startsWith("cmd:")) {
						command = result.substring(4);
						result = cp.processCommand(command);
						out.println("result:");
						out.println(result);
						out.println("block_end");
						result = in.readLine();
						if (!result.equals("result_ok")) {
							System.out
									.println("Protocol error, result_ok expected");
						}
					}
				} // while
				out.println("bye");
			} // try
			catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					s.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} // run
	} // commandthread
}


/*       Admin  RMServer        ClientAdaptor App
 * ---------------------------------------------
 *        |       |     ping         |       |
 *        |       |  <----------     |       |
 *        |       |     ping_ok      |       |
 *        |       |   --------->     |       |
 *        |       |       ...        |       |
 *        | <cmd> |                  |       |
 *        | ----> |     ping         |       |
 *        |       |  <----------     |       |
 *        |       |     cmd: <text>  |       |
 *        |       |   --------->     |       |
 *        |       |     Result:      |       |
 *        |       |  <----------     |       |
 *        |       |     <text lines> |       |
 *        |       |  <----------     |       |
 *        |       |     block_end    |       |
 *        |       |  <----------     |       |
 *        |       |     result_ok    |       |
 *        |       |   --------->     |       |
 *        |on_result                 |       |
 *        | <---- |                  |       |
 *        |       |       ...        |       |
 *        |       |                  |<alert>|
 *        |       |     Alert:       | <---- |
 *        |       |  <----------     |       |
 *        |       |     <text lines> |       |
 *        |       |  <----------     |       |
 *        |       |     block_end    |       |
 *        |       |  <----------     |       |
 *        |       |     alert_ok     |       |
 *        |       |   --------->     |       |
 *        |on_alert                  |       |
 *        | <---- |                  |       |
 *        |       |       ...        |       |
 */
