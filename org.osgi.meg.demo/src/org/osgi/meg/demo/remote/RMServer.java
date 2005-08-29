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
	
	private String			command;
	private RemoteReceiver	receiver;
	private int 			port;
	private ServerSocket	serverSocket;
	private Socket			clientSocket;
	private PrintWriter		out;
	private BufferedReader	in;
	private boolean			running;
	private boolean			connected;
	private int				socketTimeout;

	public RMServer(int port, int socketTimeout) {
		super("RMServer");
		
		this.port = port;
		this.socketTimeout = socketTimeout;
		setRunning(true);
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
		try {
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			e.printStackTrace();
			setRunning(false);
		}
		
		while (isRunning()) {
			System.out.println("Waiting for connection request...");
			
			try {
				clientSocket = serverSocket.accept();
			}
			catch (IOException e) {
				e.printStackTrace();
				setRunning(false);
				continue;
			}
			
			try {
				//clientSocket.setSoTimeout(socketTimeout);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				setConnected(true);
				System.out.println("Client has connected");
			}
			catch (Exception e) {
				e.printStackTrace();
				try {
					if (null != clientSocket)
						clientSocket.close();
				}
				catch (IOException ee) {
					ee.printStackTrace();
				}
				setRunning(false);
				continue;
			}

			String input = null;
			String result = null;
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
						result = readBlock();
                        if(result.startsWith(CommandProcessor.MAGIC_EXCEPTION_PREFIX)) {
                            String[] parts = Splitter.split(result, '\n', 6);
                            receiver.onError(parts[1], convertNull(parts[2]),
                                convertNull(parts[3]), convertNull(parts[4]),
                                convertNull(parts[5]));
                        } else {
                            receiver.onResult(result);
                        }
						out.println("result_ok");
						continue;
					}
				} // while (running && connected)
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					setConnected(false);
					if (null != clientSocket)
						clientSocket.close();
					System.out.println("Client has disconnected");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} // while (running)
		
		try {
			serverSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	} 

	private String readBlock() throws IOException {
		String input = "";
		String ret = "";
		while (!(input = in.readLine()).equals("block_end")) {
			ret = ret + input + "\n";
		}
		return ret;
	}
    
    private String convertNull(String string) {
        return string.equals("null") ? null : string;
    }
}