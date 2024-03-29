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

package org.osgi.service.url;

import java.net.URL;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface used by {@code URLStreamHandlerService} objects to call the
 * {@code setURL} method on the proxy {@code URLStreamHandler} object.
 * 
 * <p>
 * Objects of this type are passed to the
 * {@link URLStreamHandlerService#parseURL(URLStreamHandlerSetter, URL, String, int, int)}
 * method. Invoking the {@code setURL} method on the
 * {@code URLStreamHandlerSetter} object will invoke the {@code setURL} method
 * on the proxy {@code URLStreamHandler} object that is actually registered with
 * {@code java.net.URL} for the protocol.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface URLStreamHandlerSetter {
	/**
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
	 * 
	 * @deprecated This method is only for compatibility with handlers written
	 *             for JDK 1.1.
	 */
	@SuppressWarnings("javadoc")
	public void setURL(URL u, String protocol, String host, int port, String file, String ref);

	/**
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
	 */
	@SuppressWarnings("javadoc")
	public void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref);
}
