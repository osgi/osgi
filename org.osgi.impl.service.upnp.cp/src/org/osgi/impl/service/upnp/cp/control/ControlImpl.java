package org.osgi.impl.service.upnp.cp.control;

import java.io.*;
import java.net.Socket;
import java.util.*;
import org.osgi.impl.service.upnp.cp.util.*;

public class ControlImpl implements Control, SOAPConstants {
	private Hashtable		serviceObjects;
	private SOAPMaker		maker;
	private SOAPErrorCodes	errorCodes;
	private SOAPParser		parser;
	UPnPController			controller;

	// Default constructor
	public ControlImpl(UPnPController contr) {
		controller = contr;
		maker = new SOAPMaker();
		errorCodes = new SOAPErrorCodes();
		parser = new SOAPParser();
		serviceObjects = new Hashtable();
	}

	// This method is used to invoke a control request for a particular service.
	public Dictionary sendControlRequest(String path, String host,
			String serviceType, String actionName, Dictionary parameters,
			boolean first) throws Exception {
		String soapRequest;
		if (first) {
			soapRequest = maker.createControlRequest(SOAPConstants.post, path,
					host, serviceType, actionName, parameters);
		}
		else {
			soapRequest = maker.createControlRequest(SOAPConstants.mpost, path,
					host, serviceType, actionName, parameters);
		}
		String response = sendRequest(host, soapRequest);
		int i = response.indexOf("\r\n");
		if (i != -1) {
			String firstHeader = (response.substring(0, i)).trim();
			if (firstHeader.endsWith(ERROR_405) && first) {
				return sendControlRequest(path, host, serviceType, actionName,
						parameters, false);
			}
			else
				if (firstHeader.endsWith(RES_OK)) {
					ParsedRequest pr = parser.controlResOKParse(response);
					return (Dictionary) pr.getArguments();
				}
				else
					throw new Exception();
		}
		throw new Exception();
	}

	// This method will send http request to the given host.
	public String sendRequest(String hostport, String request) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String host;
		int port;
		int i = hostport.indexOf(':');
		if (i == -1) {
			host = hostport;
			port = 80;
		}
		else {
			host = hostport.substring(0, i);
			port = Integer.parseInt(hostport.substring(i + 1));
		}
		Socket soc = new Socket(host, port);
		soc.setSoTimeout(25 * 1000);
		OutputStream out = soc.getOutputStream();
		out.write(request.getBytes());
		out.flush();
		java.io.InputStream in = soc.getInputStream();
		int j;
		while ((j = in.read()) != -1) {
			baos.write(j);
		}
		return baos.toString();
	}
}
