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

public class RMServer extends Thread {
	
	private static final int SOCKET_TIMEOUT = 5000;
	
	private String			command;
	private RemoteReceiver	receiver;
	private int 			port;
	private ServerSocket	serverSocket;
	private Socket			clientSocket;
	private PrintWriter		out;
	private BufferedReader	in;
	private boolean			running;
	private boolean			connected;

	public RMServer(int port) {
		super("RMServer");
		this.port = port;
		start();
	}

	public synchronized boolean isRunning() {
		return running;
	}
	
	public synchronized void setRunning(boolean b) {
		running = b;
	}

	public synchronized boolean isConnected() {
		return connected;
	}
	
	private synchronized void setConnected(boolean b) {
		receiver.setConnected(b);
		connected = b;
	}

	public void setCommand(String cmd) {
		this.command = cmd;
	}

	public void setReceiver(RemoteReceiver ar) {
		this.receiver = ar;
	}

	public void run() {
		setRunning(true);
		
		while (isRunning()) {
			System.out.println("Waiting for connection request...");
			try {
				serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(SOCKET_TIMEOUT);
				clientSocket = serverSocket.accept();
			} catch (SocketTimeoutException e) {
				try {
					serverSocket.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				setRunning(false);
				continue;
			}
			
			try {
				clientSocket.setSoTimeout(SOCKET_TIMEOUT);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				setConnected(true);
				System.out.println("Client has connected");
			}
			catch (Exception e) {
				e.printStackTrace();
				if (null != out)
					out.close();
				if (null != in) {
					try {
						in.close();
						clientSocket.close();
					}
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				setRunning(false);
				continue;
			}

			String input = null;
			String result = null;
			String output = null;
			try {
				while (isRunning() && isConnected()) {
					input = in.readLine();
					if (input.equals("bye")) {
						in.close();
						out.close();
						setConnected(false);
						continue;
					}
					if (input.startsWith("alert:")) {
						result = readBlock();
						receiver.onAlert(result);
						out.println("alert_ok");
						continue;
					}
					if (input.startsWith("ping")) {
						if (command != null) {
							out.println("cmd:" + command);
							command = null;
						}
						else {
							out.println("ping_ok");
						}
						continue;
					}
					if (input.startsWith("result:")) {
						input = readBlock();
						receiver.onResult(input);
						out.println("result_ok");
						continue;
					}
				} // while (running && connected)
			} catch (IOException e) {
			} finally {
				try {
					setConnected(false);
					if (null != in)
						in.close();
					if (null != out)
						out.close();
					if (null != clientSocket)
						clientSocket.close();
					serverSocket.close();
					System.out.println("Client has disconnected");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} // while (running)
	} 

	private String readBlock() throws IOException {
		String input = "";
		String ret = "";
		while (!(input = in.readLine()).equals("block_end")) {
			ret = ret + input + "\r\n";
		}
		return ret;
	}

}