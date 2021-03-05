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
package org.osgi.impl.service.upnp.cp.description;

public class Icon {
	private String	mimeType;
	private String	url;
	private int		width;
	private int		height;
	private int		depth;

	// This method returns the mime type of the icon.
	public String getMimeType() {
		return mimeType;
	}

	// This method returns the width of the icon.
	public int getWidth() {
		return width;
	}

	// This method returns the height of the icon.
	public int getHeight() {
		return height;
	}

	// This method returns the depth of the icon.
	public int getDepth() {
		return depth;
	}

	// This method returns the url of the icon.
	public String getURL() {
		return url;
	}

	// This method sets the URL of the icon.
	public void setURL(String urlValue) {
		url = urlValue;
	}

	// This method sets the mimetype of the icon.
	public void setMimeType(String type) {
		mimeType = type;
	}

	// This method sets the width of the icon.
	public void setWidth(int widthValue) {
		width = widthValue;
	}

	// This method sets the height of the icon.
	public void setHeight(int heightValue) {
		height = heightValue;
	}

	// This method sets the depth of the icon.
	public void setDepth(int depthValue) {
		depth = depthValue;
	}
}