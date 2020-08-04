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
