/*
 * $Revision$
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

package java.applet;
public abstract interface AppletContext {
	public abstract java.applet.Applet getApplet(java.lang.String var0);
	public abstract java.util.Enumeration getApplets();
	public abstract java.applet.AudioClip getAudioClip(java.net.URL var0);
	public abstract java.awt.Image getImage(java.net.URL var0);
	public abstract java.io.InputStream getStream(java.lang.String var0);
	public abstract java.util.Iterator getStreamKeys();
	public abstract void setStream(java.lang.String var0, java.io.InputStream var1) throws java.io.IOException;
	public abstract void showDocument(java.net.URL var0);
	public abstract void showDocument(java.net.URL var0, java.lang.String var1);
	public abstract void showStatus(java.lang.String var0);
}

