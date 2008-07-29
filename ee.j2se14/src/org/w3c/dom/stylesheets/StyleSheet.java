/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.w3c.dom.stylesheets;
public abstract interface StyleSheet {
	public abstract boolean getDisabled();
	public abstract java.lang.String getHref();
	public abstract org.w3c.dom.stylesheets.MediaList getMedia();
	public abstract org.w3c.dom.Node getOwnerNode();
	public abstract org.w3c.dom.stylesheets.StyleSheet getParentStyleSheet();
	public abstract java.lang.String getTitle();
	public abstract java.lang.String getType();
	public abstract void setDisabled(boolean var0);
}

