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
public class Applet extends java.awt.Panel {
	public Applet() { }
	public void destroy() { }
	public java.applet.AppletContext getAppletContext() { return null; }
	public java.lang.String getAppletInfo() { return null; }
	public java.applet.AudioClip getAudioClip(java.net.URL var0) { return null; }
	public java.applet.AudioClip getAudioClip(java.net.URL var0, java.lang.String var1) { return null; }
	public java.net.URL getCodeBase() { return null; }
	public java.net.URL getDocumentBase() { return null; }
	public java.awt.Image getImage(java.net.URL var0) { return null; }
	public java.awt.Image getImage(java.net.URL var0, java.lang.String var1) { return null; }
	public java.lang.String getParameter(java.lang.String var0) { return null; }
	public java.lang.String[][] getParameterInfo() { return null; }
	public void init() { }
	public boolean isActive() { return false; }
	public final static java.applet.AudioClip newAudioClip(java.net.URL var0) { return null; }
	public void play(java.net.URL var0) { }
	public void play(java.net.URL var0, java.lang.String var1) { }
	public final void setStub(java.applet.AppletStub var0) { }
	public void showStatus(java.lang.String var0) { }
	public void start() { }
	public void stop() { }
	protected class AccessibleApplet extends java.awt.Panel.AccessibleAWTPanel {
		protected AccessibleApplet() { }
	}
}

