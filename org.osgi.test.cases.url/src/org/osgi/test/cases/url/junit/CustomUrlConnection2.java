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
package org.osgi.test.cases.url.junit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class CustomUrlConnection2 extends URLConnection {
	private InputStream	in;

	public CustomUrlConnection2(URL url) {
		super(url);
	}

	public String getContentType() {
		return "osgi/test";
	}

	public InputStream getInputStream() throws IOException {
		connect();
		return in;
	}

	public void connect() throws IOException {
		if (in == null) {
			in = new ByteArrayInputStream("OSGiTest".getBytes());
		}
	}
}
