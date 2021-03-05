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
package org.osgi.impl.service.upnp.cd.control;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;


// This interface contains the constant strings, used in the control layer.
public class SOAPConstants {
	public static final String	post			= "POST ";
	public static final String	host			= "HOST: ";
	public static final String	http			= "HTTP/1.1";
	public static final String	contentLength	= "CONTENT-LENGTH: ";
	public static final String	contentType		= "CONTENT-TYPE: text/xml; charset=\"utf-8\"";
	public static final String	soapAction		= "SOAPACTION: ";
	public static final String	startEnvelope	= "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">";
	public static final String	startBody		= "<s:Body>";
	public static final String	mpost			= "M-POST ";
	public static final String	stopEnvelope	= "</s:Envelope>";
	public static final String	stopBody		= "</s:Body>";
	public static final String	saUrn			= "\"urn:schemas-upnp-org:service:";
	public static final String	date			= "DATE: ";
	public static final String	ext				= "EXT: ";
	public static final String	server			= "SERVER :";
	public static final String	upnpVersion		= " UPnP/1.0 ";
	public static final String	response		= "Response";
	public static final String	startFault		= "<s:Fault>";
	public static final String	stopFault		= "</s:Fault>";
	public static final String	startDetail		= "<detail>";
	public static final String	stopDetail		= "</detail>";
	public static final String	faultCode		= "<faultcode>s:Client</faultcode>";
	public static final String	faultString		= "<faultstring>UPnPError</faultstring>";
	public static final String	startErrCode	= "<errorCode>";
	public static final String	stopErrCode		= "</errorCode>";
	public static final String	startErrDesc	= "<errorDescription>";
	public static final String	stopErrDesc		= "</errorDescription>";
	public static final String	upnpErr			= "UPnPError";
	public static final String	upnpXmlns		= "xmlns=\"urn:schemas-upnp-org:control-1-0\"";
	public static final String	queryStateVar	= "u:QueryStateVariable";
	public static final String	varName			= "u:varName";
	public static final String	result			= "return";
	public static final String	rn				= "\r\n";
	public static final String	httpEnv			= "\"http://schemas.xmlsoap.org/soap/envelope/\"";
	public static final String	osNameVersion	= getProperty("os.name")
														+ "/"
														+ getProperty("os.version");
	public static final String	productVersion	= "Samsung-UPnP/1.0";
	public static final String	ERROR_412		= " 412 Precondition Failed";
	public static final String	ERROR_415		= " 415 Unsupported Media Type";
	public static final String	ERROR_400		= " 400 Bad Request";
	public static final String	ERROR_500		= " 500 Internal Server Error";
	public static final String	OK_200			= " 200 OK";

	/**
	 * Return the property value from the bundle context properties.
	 * 
	 * @param key The property key name.
	 * @return The property value or null if the property is not set.
	 */
	public static String getProperty(String key) {
		Bundle bundle = FrameworkUtil.getBundle(SOAPConstants.class);
		if (bundle != null) {
			BundleContext context = bundle.getBundleContext();
			if (context != null) {
				return context.getProperty(key);
			}
		}
		return System.getProperty(key);
	}
}
