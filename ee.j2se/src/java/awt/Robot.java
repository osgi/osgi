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
public class Robot {
	public Robot() throws java.awt.AWTException { }
	public Robot(java.awt.GraphicsDevice var0) throws java.awt.AWTException { }
	public java.awt.image.BufferedImage createScreenCapture(java.awt.Rectangle var0) { return null; }
	public void delay(int var0) { }
	public int getAutoDelay() { return 0; }
	public java.awt.Color getPixelColor(int var0, int var1) { return null; }
	public boolean isAutoWaitForIdle() { return false; }
	public void keyPress(int var0) { }
	public void keyRelease(int var0) { }
	public void mouseMove(int var0, int var1) { }
	public void mousePress(int var0) { }
	public void mouseRelease(int var0) { }
	public void mouseWheel(int var0) { }
	public void setAutoDelay(int var0) { }
	public void setAutoWaitForIdle(boolean var0) { }
	public java.lang.String toString() { return null; }
	public void waitForIdle() { }
}

