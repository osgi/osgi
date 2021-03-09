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
package org.osgi.impl.service.upnp.cp.event;

public class GenaConstants {
	public static final String	GENA_SUBSCRIBE		= "SUBSCRIBE";
	public static final String	GENA_UNSUBSCRIBE	= "UNSUBSCRIBE";
	public static final String	GENA_NOTIFY			= "NOTIFY";
	public static final String	GENA_SERVER_VERSION	= "HTTP/1.1";
	public static final String	GENA_HOST			= "host";
	public static final String	GENA_CALLBACK		= "callback";
	public static final String	GENA_NT				= "nt";
	public static final String	GENA_NTS			= "nts";
	public static final String	GENA_SEQ			= "seq";
	public static final String	GENA_SID			= "sid";
	public static String		GENA_CALLBACK_URL;
	public static final String	GENA_BODY			= "GENA-BODY";
	public static final String	GENA_CONTENT_TYPE	= "content-type";
	public static final String	GENA_CONTENT_LENGTH	= "content-length";
	public static final String	GENA_EVENT			= "upnp:event";
	public static final String	GENA_PROP			= "upnp:propchange";
	public static final String	GENA_TIMEOUT		= "timeout";
	public static final String	GENA_SUCESS			= "sucess";
	public static final String	GENA_RESOK			= "Http/1.1 200 OK";
	public static final String	GENA_ERROR1			= "Http/1.1 412 PreCondition Failed";
	public static final String	GENA_ERROR2			= "Http/1.1 400 Bad Request";
	public static final String	GENA_ERROR3			= "Http/1.1 503 Service Unavailable";

	public GenaConstants(String ip) {
		GENA_CALLBACK_URL = "http://" + ip;
	}

	// This method sets the port number
	public void setPort(int port) {
		GENA_CALLBACK_URL = GENA_CALLBACK_URL + ":" + port;
	}
}
