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
import java.net.*;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.*;

/* Description of the protocol at the end of this file */
// TODO what should be used instead of the System.exit() calls?
public class ClientAdaptor implements RemoteAlertSender {
	private boolean				goon	 = true;
	private String				message	 = null;
	private CommandProcessor	cp		 = null;
	private int					pingTime;

	public ClientAdaptor(DmtFactory fact, String host, int port, int pingTime) {
		this.pingTime = pingTime;
		CommandThread ct = new CommandThread(this, host, port);
		cp = new CommandProcessor(fact);
		ct.start();
	}

	public void stop() {
		goon = false;
	}

	public boolean acceptServerId(String serverid) {
		return true; // todo
	}

	public void sendAlert(String serverid, String sessionid, int code,
			DmtAlertItem[] items) throws Exception {
		String msg = "Code:" + code + "\r\n";
		String s = ",\n";
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				msg += "Item# " + i + ":" + "Source:" + items[i].getSource()
						+ s + "Data:" + items[i].getData() + s + "Markup:"
						+ items[i].getMark() + s + "Type:" + items[i].getType()
						+ s + "Format:" + items[i].getFormat() + "\r\n";
			}
		}
		this.message = msg;
	}

	private class CommandThread extends Thread {
		private ClientAdaptor	parent	= null;
		private Socket			s		= null;
		private PrintWriter		out		= null;
		private BufferedReader	in		= null;

		public CommandThread(ClientAdaptor parent, String host, int port) {
			super("ClientAdaptor");
			this.parent = parent;
			try {
				s = new Socket(host, port);
				s.setSoTimeout(10000);
				out = new PrintWriter(s.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			}
			catch (UnknownHostException e) {
				System.err.println("Don't know about host: " + host);
				System.exit(1);
			}
			catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to: "
						+ host + ":" + port);
				System.exit(1);
			}
		}

		public void run() {
			String command;
			String result;
			try {
				while (parent.goon) {
					try {
						Thread.sleep(pingTime);
					}
					catch (InterruptedException e) {
					}
					if (parent.message != null) { //alert
						out.println("alert:");
						out.println(parent.message);
						out.println("block_end");
						parent.message = null;
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
						parent.goon = false;
						continue;
					}
					if (result.equals("ping_ok"))
						continue;
					if (result.startsWith("cmd:")) {
						command = result.substring(4);
						result = parent.cp.processCommand(command);
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
				out.close();
				in.close();
				s.close();
			} // try
			catch (IOException e) {
				e.printStackTrace();
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
