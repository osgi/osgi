package org.osgi.impl.service.upnp.cp.control;

public class SOAPParser {
	public ParsedRequest	theParsedRequest;

	// This method is called to parse a SOAP error response.
	public String[] errorResponse(String xml) {
		if (xml == null) {
			return null;
		}
		int i = xml.indexOf(SOAPConstants.startErrCode);
		int j = xml.lastIndexOf(SOAPConstants.stopErrCode);
		if (i != -1 && j != -1 && i != j) {
			String code = xml.substring(i + SOAPConstants.startErrCode.length()
					- 1, j);
			int x = xml.indexOf(SOAPConstants.startErrDesc);
			int y = xml.lastIndexOf(SOAPConstants.stopErrDesc);
			if (x != -1 && y != -1 && x != y) {
				String desc = xml.substring(x
						+ SOAPConstants.stopErrDesc.length() - 1, y);
				return new String[] {code, desc};
			}
			else {
				return new String[] {code};
			}
		}
		return null;
	}

	// This method is used to parse Control response which is successful.
	public ParsedRequest controlResOKParse(String xml) {
		String serviceType;
		String actionName;
		ParsedRequest req = new ParsedRequest();
		if (xml == null) {
			return null;
		}
		int i = xml.indexOf(SOAPConstants.startBody);
		int j = xml.indexOf(SOAPConstants.stopBody);
		xml = (xml.substring(i + SOAPConstants.startBody.length(), j)).trim();
		int x = xml.indexOf(SOAPConstants.response);
		int y = xml.indexOf("\">");
		actionName = xml.substring(3, x);
		serviceType = xml.substring(xml.indexOf(":service:") + 9, y);
		xml = (xml.substring(y + 2, xml.lastIndexOf("</u:" + actionName
				+ SOAPConstants.response))).trim();
		req.setServiceType(serviceType);
		req.setActionName(actionName);
		while (true) {
			int startArgIndex = xml.indexOf('<');
			if (startArgIndex == -1) {
				break;
			}
			String argName = xml.substring(1, xml.indexOf('>'));
			int endArgIndex = xml.indexOf("</" + argName + ">");
			String argValue = xml.substring(argName.length() + 2, endArgIndex);
			xml = (xml.substring(endArgIndex + 3 + argName.length())).trim();
			req.setArgument(argName, argValue);
		}
		return req;
	}
}
