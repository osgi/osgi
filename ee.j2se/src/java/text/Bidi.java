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

package java.text;
public final class Bidi {
	public final static int DIRECTION_DEFAULT_LEFT_TO_RIGHT = -2;
	public final static int DIRECTION_DEFAULT_RIGHT_TO_LEFT = -1;
	public final static int DIRECTION_LEFT_TO_RIGHT = 0;
	public final static int DIRECTION_RIGHT_TO_LEFT = 1;
	public Bidi(java.lang.String var0, int var1) { } 
	public Bidi(java.text.AttributedCharacterIterator var0) { } 
	public Bidi(char[] var0, int var1, byte[] var2, int var3, int var4, int var5) { } 
	public boolean baseIsLeftToRight() { return false; }
	public java.text.Bidi createLineBidi(int var0, int var1) { return null; }
	public int getBaseLevel() { return 0; }
	public int getLength() { return 0; }
	public int getLevelAt(int var0) { return 0; }
	public int getRunCount() { return 0; }
	public int getRunLevel(int var0) { return 0; }
	public int getRunLimit(int var0) { return 0; }
	public int getRunStart(int var0) { return 0; }
	public boolean isLeftToRight() { return false; }
	public boolean isMixed() { return false; }
	public boolean isRightToLeft() { return false; }
	public static void reorderVisually(byte[] var0, int var1, java.lang.Object[] var2, int var3, int var4) { }
	public static boolean requiresBidi(char[] var0, int var1, int var2) { return false; }
}

