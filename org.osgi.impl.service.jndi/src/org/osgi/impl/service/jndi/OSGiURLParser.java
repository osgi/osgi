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

	private static final String	OSGI_SERVICES_PREFIX	= "osgi:services/";

	private final String		m_osgiURL;
	private String				m_serviceInterface		= null;
	private String				m_filter				= null;
	private boolean				m_parsingCompleted		= false;

	public OSGiURLParser(String osgiURL) {
		m_osgiURL = osgiURL;
	}

	public void parse() {
		if (m_osgiURL.startsWith(OSGI_SERVICES_PREFIX)) {
			String urlData = m_osgiURL.substring(OSGI_SERVICES_PREFIX.length());
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
		else {
			throw new IllegalStateException(
					"URL did not conform to the OSGi URL Syntax");
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

	private void checkParserState() {
		if (!m_parsingCompleted)
			throw new IllegalStateException("OSGi URL has not been parsed");
	}
}
