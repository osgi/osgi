/*
 * Copyright 2009 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.impl.service.jndi;

/**
 * Utility Class to parse the "osgi:services" URL syntax
 * 
 */
class OSGiURLParser {

	//TODO, remove the OLD prefix once the test cases are ported over
	private static final String	OLD_OSGI_SERVICES_PREFIX	= "osgi:services/";
	// TODO, remove NEW from this name once the test cases are ported over
	private static final String NEW_OSGI_SERVICES_PREFIX    = "osgi:service/";
	
	private static final String OSGI_SERVICE_LIST_PREFIX = "osgi:servicelist/";

	private final String		m_osgiURL;
	private String				m_serviceInterface		= null;
	private String				m_filter				= null;
	private boolean				m_parsingCompleted		= false;
	private boolean             m_isServiceList           = false;

	public OSGiURLParser(String osgiURL) {
		m_osgiURL = osgiURL;
	}

	public void parse() {
		if (m_osgiURL.startsWith(OLD_OSGI_SERVICES_PREFIX)) {
			parseURLData(OLD_OSGI_SERVICES_PREFIX);
		}
		else {
			if(m_osgiURL.startsWith(NEW_OSGI_SERVICES_PREFIX)) {
				parseURLData(NEW_OSGI_SERVICES_PREFIX);
			} else {
				if(m_osgiURL.startsWith(OSGI_SERVICE_LIST_PREFIX)) {
					parseURLData(OSGI_SERVICE_LIST_PREFIX);
					m_isServiceList = true;
				} else {
					throw new IllegalStateException(
					"URL did not conform to the OSGi URL Syntax");
				}
				
			}
		}
	}
	

	public String getServiceInterface() {
		checkParserState();
		return m_serviceInterface;
	}

	public String getFilter() {
		checkParserState();
		return m_filter;
	}

	public boolean hasFilter() {
		checkParserState();
		return getFilter() != null;
	}
	
	public boolean isServiceListURL() {
		return m_isServiceList;
	}

	private void checkParserState() {
		if (!m_parsingCompleted)
			throw new IllegalStateException("OSGi URL has not been parsed");
	}
	
	private void parseURLData(final String prefix) {
		String urlData = m_osgiURL.substring(prefix.length());
		int indexOfSlash = urlData.indexOf("/");
		if (indexOfSlash != -1) {
			// interpret everything after the slash to be an OSGi filter
			// string
			m_serviceInterface = urlData.substring(0, indexOfSlash);
			m_filter = urlData.substring(indexOfSlash + 1);
		}
		else {
			m_serviceInterface = urlData;
		}

		if (m_serviceInterface.length() == 0) {
			throw new IllegalStateException(
					"URL did not conform to the OSGi URL Syntax - No Service Interface specified");
		}

		m_parsingCompleted = true;
	}
}
