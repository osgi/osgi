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

import java.util.Dictionary;
import java.util.Enumeration;

public class SOAPMaker extends SOAPConstants {
	private Object	controlLock	= new Object();

	// This method will take the parameters and then create a SOAP message for
	// the Control request.
	public String createControlRequest(String method, String path,
			String hostport, String serviceType, String actionName,
			Dictionary<String,Object> arguments) {
		synchronized (controlLock) {
			StringBuffer soapBuf = new StringBuffer();
			@SuppressWarnings("unused")
			String soap;
			soapBuf.append(startEnvelope);
			soapBuf.append(rn);
			soapBuf.append(startBody);
			soapBuf.append(rn);
			soapBuf.append("<u:" + actionName + " xmlns:u=" + saUrn
					+ serviceType + "\">");
			soapBuf.append(rn);
			for (Enumeration<String> e = arguments.keys(); e
					.hasMoreElements();) {
				String argName = e.nextElement();
				soapBuf.append("<" + argName + ">"
						+ arguments.get(argName) + "</" + argName
						+ ">");
				soapBuf.append(rn);
			}
			soapBuf.append("</u:" + actionName + ">");
			soapBuf.append(rn);
			soapBuf.append(stopBody);
			soapBuf.append(rn);
			soapBuf.append(stopEnvelope);
			soapBuf.append(rn);
			soap = soapBuf.toString();
			StringBuffer controlBuf = new StringBuffer();
			if (method.equals(SOAPConstants.post)) {
				controlBuf.append(post);
			}
			else {
				controlBuf.append(mpost);
			}
			controlBuf.append(path);
			controlBuf.append(" " + http);
			controlBuf.append(rn);
			controlBuf.append(host);
			controlBuf.append(hostport);
			controlBuf.append(rn);
			controlBuf.append(contentLength);
			controlBuf.append(soapBuf.length());
			controlBuf.append(rn);
			controlBuf.append(contentType);
			controlBuf.append(rn);
			if (method.equals(SOAPConstants.mpost)) {
				controlBuf.append("MAN: " + httpEnv + "; ns=01");
				controlBuf.append(rn);
				controlBuf.append("01-");
			}
			controlBuf.append(soapAction);
			controlBuf.append(saUrn);
			controlBuf.append(serviceType + "#" + actionName + "\"");
			controlBuf.append(rn);
			controlBuf.append(rn);
			controlBuf.append(soapBuf);
			return controlBuf.toString();
		}
	}
}
