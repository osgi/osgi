package org.osgi.impl.service.upnp.cp.event;

import java.io.*;
import java.net.Socket;

public final class Processor implements Runnable {
	private GenaServer			genaServer;
	protected Socket			client;
	private String				method;
	private OutputStream		dos;
	private RequestProcessor	reqParser	= null;

	Processor(GenaServer genaServer, Socket client) {
		this.genaServer = genaServer;
		this.client = client;
	}

	// Run method of the processor thread. parses the given request and sets the
	// appropriate
	// headers . This object will be passed to the servlet request object to
	// fullfill servlet
	// httprequest functionality. Creates a http response object based on the
	// given request
	// and processes the request and sends the output back to the client.
	public void run() {
		BufferedInputStream ins = null;
		try {
			dos = client.getOutputStream();
			ins = new BufferedInputStream(client.getInputStream());
			reqParser = new RequestProcessor(ins);
			int result = reqParser.parseRequest();
			if (result != 200) {
				errorOutput(result, reqParser.errorMessage);
			}
			else {
				method = reqParser.reqMethod;
				if (method.equals("NOTIFY")) {
					genaNotify();
				}
				else {
					errorOutput(404, "Method Not Implemented");
				}
			}
			if (dos != null) {
				dos.close();
				dos = null;
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This function is used for setting the outbuffer with the proper error
	// message
	private void errorOutput(int i, String message) {
		try {
			String errorResponse = "HTTP/1.1 " + i + " " + message + "\r\n"
					+ "Connection: close\r\n" + "SERVER: "
					+ System.getProperty("os.name") + "/"
					+ System.getProperty("os.version")
					+ " UPnP/1.0 SamsungUPnP/1.0\r\n\r\n";
			dos.write(errorResponse.getBytes());
			dos.flush();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This function is used for notifing the clients.
	private void genaNotify() {
		reqParser.headers.put(GenaConstants.GENA_BODY, reqParser.contentBody);
		String result = genaServer.esi.notifyListeners(reqParser.headers);
		try {
			dos.write(result.getBytes());
			dos.flush();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
