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

package org.osgi.test.cases.webcontainer.util;

import org.osgi.framework.BundleContext;

/**
 * @version $Rev$ $Date$
 */
public class Server {

    int port;
    String host;

	public Server(BundleContext context) {
        // when host is not specified, let's try to read it from the system
        // property.
		String p = context.getProperty("org.osgi.service.webcontainer.host");
        this.host = (p == null) ? "localhost" : p;
		this.port = guessHttpPort(context);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    // TODO: the http CT has this method impl that we may be able to borrow
    // here.
	int guessHttpPort(BundleContext context) {
        // Try to find the HTTP port.
		String p = context
				.getProperty("org.osgi.service.webcontainer.port.http");
        return (p == null) ? 8080 : Integer.parseInt(p);
    }
}
