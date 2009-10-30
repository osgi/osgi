/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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
public interface AppletContext {
	java.applet.Applet getApplet(java.lang.String var0);
	java.util.Enumeration<java.applet.Applet> getApplets();
	java.applet.AudioClip getAudioClip(java.net.URL var0);
	java.awt.Image getImage(java.net.URL var0);
	java.io.InputStream getStream(java.lang.String var0);
	java.util.Iterator<java.lang.String> getStreamKeys();
	void setStream(java.lang.String var0, java.io.InputStream var1) throws java.io.IOException;
	void showDocument(java.net.URL var0);
	void showDocument(java.net.URL var0, java.lang.String var1);
	void showStatus(java.lang.String var0);
}

