/*
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
public class AWTKeyStroke implements java.io.Serializable {
	protected AWTKeyStroke() { }
	protected AWTKeyStroke(char var0, int var1, int var2, boolean var3) { }
	public final boolean equals(java.lang.Object var0) { return false; }
	public static java.awt.AWTKeyStroke getAWTKeyStroke(char var0) { return null; }
	public static java.awt.AWTKeyStroke getAWTKeyStroke(int var0, int var1) { return null; }
	public static java.awt.AWTKeyStroke getAWTKeyStroke(int var0, int var1, boolean var2) { return null; }
	public static java.awt.AWTKeyStroke getAWTKeyStroke(java.lang.Character var0, int var1) { return null; }
	public static java.awt.AWTKeyStroke getAWTKeyStroke(java.lang.String var0) { return null; }
	public static java.awt.AWTKeyStroke getAWTKeyStrokeForEvent(java.awt.event.KeyEvent var0) { return null; }
	public final char getKeyChar() { return '\0'; }
	public final int getKeyCode() { return 0; }
	public final int getKeyEventType() { return 0; }
	public final int getModifiers() { return 0; }
	public int hashCode() { return 0; }
	public final boolean isOnKeyRelease() { return false; }
	protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
	protected static void registerSubclass(java.lang.Class var0) { }
}

