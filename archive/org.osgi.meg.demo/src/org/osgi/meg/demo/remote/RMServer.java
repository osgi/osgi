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

public class RMServer {
	private String			command		= null;
	private RemoteReceiver	receiver	= null;
	private ServerThread	st;
	private boolean			running;

	public RMServer(int port, int timeout) {
		st = new ServerThread(this, port, timeout);
		st.start();
	}

	public synchronized void stop() {
		running = false;
	}

	public void setCommand(String cmd) {
		this.command = cmd;
	}

	public void setReceiver(RemoteReceiver ar) {
		this.receiver = ar;
	}

	private class ServerThread extends Thread {
		private RMServer		parent			= null;
		private ServerSocket	serverSocket	= null;
		private Socket			clientSocket	= null;
		private PrintWriter		out				= null;
		private BufferedReader	in				= null;

		public ServerThread(RMServer parent, int port, int timeout) {
			super("RMServer");
			this.parent = parent;
			try {
				serverSocket = new ServerSocket(port);
				clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(timeout);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket
						.getInputStream()));
			}
			catch (IOException e) {
				System.out.println("ServerThread init failed" + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}

		public void run() {
			String input = null;
			String result = null;
			String output = null;
			running = true;
			try {
				while (running && !(input = in.readLine()).equals("bye")) {
					if (input.startsWith("alert:")) {
						result = readBlock();
						parent.receiver.onAlert(result);
						out.println("alert_ok");
						continue;
					}
					if (input.startsWith("ping")) {
						if (parent.command != null) {
							out.println("cmd:" + parent.command);
							parent.command = null;
						}
						else {
							out.println("ping_ok");
						}
						continue;
					}
					else
						if (input.startsWith("result:")) {
							input = readBlock();
							parent.receiver.onResult(input);
							out.println("result_ok");
							continue;
						}
				} // while
				out.close();
				in.close();
				clientSocket.close();
				serverSocket.close();
			} // try
			catch (IOException e) {
				e.printStackTrace();
			}
		} // run

		private String readBlock() throws IOException {
			String input = "";
			String ret = "";
			while (!(input = in.readLine()).equals("block_end")) {
				ret = ret + input + "\r\n";
			}
			return ret;
		}
	} // sthread
}