package org.osgi.impl.service.upnp.cd.event;

import java.io.*;
import java.net.Socket;

// Whenever a connection is established on the server port,
// an processor object is created to do the processing for that request. This class parses the
// HTTP request from the buffered stream,generates HttpRequest and HttpResponse .
public final class Processor implements Runnable {
	private GenaServer			genaServer;		//reference of the server
	protected Socket			client;
	private String				method;
	private DataOutputStream	dos;
	private RequestProcessor	reqParser	= null;
	private String				reqMethod;

	// Constructor used for intializing and storing reference variables
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
			dos = new DataOutputStream(new BufferedOutputStream(client
					.getOutputStream(), 1024));
			ins = new BufferedInputStream(client.getInputStream());
			reqParser = new RequestProcessor(ins);
			int result = reqParser.parseRequest();
			if (result != 200) {
				errorOutput(result, reqParser.errorMessage);
			}
			else {
				method = reqParser.reqMethod;
				if (method.equals("GET")) {
					errorOutput(404, "Method Not Implemented");
				}
				else {
					processRequest();
				}
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
			dos.writeBytes(errorResponse);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Processes the request.
	public void processRequest() {
		reqMethod = reqParser.reqMethod.toLowerCase();
		if (reqMethod.equals("subscribe")) {
			if (reqParser.headers.get("nt").equals("upnp:event")
					&& reqParser.headers.get("callback") != "") {
				insertHeaderValue();
			}
			else {
				errorOutput(400, reqMethod);
				return;
			}
			if (reqParser.headers.get("sid") != null) {
				RenewalCheck rc = new RenewalCheck(dos, reqParser.headers);
				rc.renew();
				rc = null;
				return;
			}
			else {
				SubscriptionCheck sc = new SubscriptionCheck(dos,
						reqParser.headers, genaServer.context);
				sc.subscribe();
				sc = null;
				return;
			}
		}
		if (reqMethod.equals("unsubscribe")) {
			insertHeaderValue();
			UnsubscribeCheck uc = new UnsubscribeCheck(dos, reqParser.headers);
			uc.unsubscribe();
			uc = null;
			return;
		}
		if (reqMethod.equals("post")) {
			if (reqParser.headers.get("soapaction") != null) {
				processSoap();
				return;
			}
			else {
				errorOutput(412, reqMethod);
				return;
			}
		}
		if (reqMethod.equals("m-post")) {
			if (reqParser.headers.get("man") != null) {
				if (reqParser.headers.get(reqParser.nameSpace + "-soapaction") != null) {
					processSoap();
					return;
				}
				else {
					errorOutput(412, reqMethod);
					return;
				}
			}
		}
		return;
	}

	// Inserts header info into the headers table
	void insertHeaderValue() {
		reqParser.headers.put("publisherpath", reqParser.reqUriPath);
		reqParser.headers.put("METHOD", reqMethod);
	}

	// processes soap request(post or mpost)
	void processSoap() {
		reqParser.headers.put(reqParser.reqMethod.toLowerCase(),
				reqParser.reqUriPath);
		genaServer.cti.sendHttpRequest(reqParser.headers,
				reqParser.contentBody, dos);
	}
}
