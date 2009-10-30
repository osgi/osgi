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

package javax.swing.text;
public class Segment implements java.lang.Cloneable, java.text.CharacterIterator {
	public char[] array;
	public int count;
	public int offset;
	public Segment() { } 
	public Segment(char[] var0, int var1, int var2) { } 
	public java.lang.Object clone() { return null; }
	public char current() { return '\0'; }
	public char first() { return '\0'; }
	public int getBeginIndex() { return 0; }
	public int getEndIndex() { return 0; }
	public int getIndex() { return 0; }
	public boolean isPartialReturn() { return false; }
	public char last() { return '\0'; }
	public char next() { return '\0'; }
	public char previous() { return '\0'; }
	public char setIndex(int var0) { return '\0'; }
	public void setPartialReturn(boolean var0) { }
}

