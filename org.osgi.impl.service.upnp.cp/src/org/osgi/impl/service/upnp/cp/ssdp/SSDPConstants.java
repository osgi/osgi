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
package org.osgi.impl.service.upnp.cp.ssdp;

public interface SSDPConstants {
	static final String	NOTIFY			= "NOTIFY * HTTP/1.1";
	static final String	MSEARCH			= "M-SEARCH * HTTP/1.1";
	static final String	MSEARCH_RESP	= "HTTP/1.1 200 OK";
	static final String	HOST			= "HOST: ";
	static final String	MADD			= "239.255.255.250";
	static final String	MPORT			= "1900";
	static final String	CACHE			= "CACHE-CONTROL:";
	static final String	LOC				= "LOCATION:";
	static final String	NT				= "NT:";
	static final String	NTS				= "NTS:";
	static final String	SERVER			= "SERVER:";
	static final String	USN				= "USN:";
	static final String	MAXAGE			= "max-age";
	static final String	UUID			= "uuid:";
	static final String	ALIVE			= "ssdp:alive";
	static final String	BYEBYE			= "ssdp:byebye";
	static final String	STALL			= "ssdp:all";
	static final String	DISCOVER		= "\"ssdp:discover\"";
	static final String	ROOTDEVICE		= "upnp:rootdevice";
	static final String	EXT				= "EXT:";
	static final String	ST				= "ST:";
	static final String	DATE1			= "DATE:";
	static final String	MAN				= "MAN:";
	static final String	MX				= "MX:";
	static final String	RN				= "\r\n";
	static final String	OSVERSION		= " UPNP/1.0 SAMSUNG-UPnP-STACK/1.0";
	static final byte	TTL				= 4;
	static final String	MAXWAIT			= "30";
}
