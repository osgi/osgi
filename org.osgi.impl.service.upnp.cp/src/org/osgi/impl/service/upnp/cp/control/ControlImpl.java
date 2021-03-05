/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cp.control;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Dictionary;

import org.osgi.impl.service.upnp.cp.util.Control;
import org.osgi.impl.service.upnp.cp.util.UPnPController;

public class ControlImpl implements Control {
	private SOAPMaker		maker;
	@SuppressWarnings("unused")
	private SOAPErrorCodes	errorCodes;
	private SOAPParser		parser;
	UPnPController			controller;

	// Default constructor
	public ControlImpl(UPnPController contr) {
		controller = contr;
		maker = new SOAPMaker();
		errorCodes = new SOAPErrorCodes();
		parser = new SOAPParser();
	}

	// This method is used to invoke a control request for a particular service.
	@Override
	public Dictionary<String,Object> sendControlRequest(String path,
			String host, String serviceType, String actionName,
			Dictionary<String,Object> parameters,
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
			if (firstHeader.endsWith(SOAPConstants.ERROR_405) && first) {
				return sendControlRequest(path, host, serviceType, actionName,
						parameters, false);
			}
			else
				if (firstHeader.endsWith(SOAPConstants.RES_OK)) {
					ParsedRequest pr = parser.controlResOKParse(response);
					return pr.getArguments();
				}
				else
					throw new Exception("Expected ok: " + path + " :: " + actionName + " :: "  + firstHeader);
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
		try {
			soc.setSoTimeout(5000);
			OutputStream out = soc.getOutputStream();
			out.write(request.getBytes());
			out.flush();
			soc.shutdownOutput();
			java.io.InputStream in = soc.getInputStream();
			int j;
			while ((j = in.read()) != -1) {
				baos.write(j);
			}
			in.close();
			return baos.toString();
		} finally {
			soc.close();
		}
	}
}
