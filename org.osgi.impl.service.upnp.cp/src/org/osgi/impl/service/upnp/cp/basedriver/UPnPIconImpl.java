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
package org.osgi.impl.service.upnp.cp.basedriver;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import org.osgi.service.upnp.*;
import org.osgi.impl.service.upnp.cp.description.*;


public class UPnPIconImpl implements UPnPIcon {
	
	private volatile boolean isReleased;
	private Icon		icon;
	private String		baseURL;

	// This constructor creates the UPnPIcon object based on the given Icon
	// object.
	UPnPIconImpl(Icon icon, String baseURL) {
		this.icon = icon;
		this.baseURL = baseURL;
	}

	// This method returns the mimetype of the icon.
	@Override
	public String getMimeType() {
		return icon.getMimeType();
	}

	// This method returns the width of the icon.
	@Override
	public int getWidth() {
		return icon.getWidth();
	}

	// This method returns the height of the icon.
	@Override
	public int getHeight() {
		return icon.getHeight();
	}

	// This method returns the size of the icon.
	@Override
	public int getSize() {
		return -1;
	}

	// This method returns the depth of the icon.
	@Override
	public int getDepth() {
		return icon.getDepth();
	}

	// This method returns the input stream of the icon.
	@Override
	public InputStream getInputStream() throws IOException {
		if (this.isReleased) {
			throw new IllegalStateException(
					"UPnP device has been removed from the network.");
		}
		URL url = new URL(new URL(baseURL), icon.getURL());
		return url.openStream();
	}

	/* package-private */void release() {
		this.isReleased = true;
	}
	
}
