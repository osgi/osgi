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

package java.awt;
public abstract class GraphicsDevice {
	protected GraphicsDevice() { }
	public int getAvailableAcceleratedMemory() { return 0; }
	public java.awt.GraphicsConfiguration getBestConfiguration(java.awt.GraphicsConfigTemplate var0) { return null; }
	public abstract java.awt.GraphicsConfiguration[] getConfigurations();
	public abstract java.awt.GraphicsConfiguration getDefaultConfiguration();
	public java.awt.DisplayMode getDisplayMode() { return null; }
	public java.awt.DisplayMode[] getDisplayModes() { return null; }
	public java.awt.Window getFullScreenWindow() { return null; }
	public abstract java.lang.String getIDstring();
	public abstract int getType();
	public boolean isDisplayChangeSupported() { return false; }
	public boolean isFullScreenSupported() { return false; }
	public void setDisplayMode(java.awt.DisplayMode var0) { }
	public void setFullScreenWindow(java.awt.Window var0) { }
	public final static int TYPE_IMAGE_BUFFER = 2;
	public final static int TYPE_PRINTER = 1;
	public final static int TYPE_RASTER_SCREEN = 0;
}

