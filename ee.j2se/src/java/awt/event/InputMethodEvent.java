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

package java.awt.event;
public class InputMethodEvent extends java.awt.AWTEvent {
	public InputMethodEvent(java.awt.Component var0, int var1, long var2, java.text.AttributedCharacterIterator var3, int var4, java.awt.font.TextHitInfo var5, java.awt.font.TextHitInfo var6) { super((java.lang.Object) null, 0); }
	public InputMethodEvent(java.awt.Component var0, int var1, java.awt.font.TextHitInfo var2, java.awt.font.TextHitInfo var3) { super((java.lang.Object) null, 0); }
	public InputMethodEvent(java.awt.Component var0, int var1, java.text.AttributedCharacterIterator var2, int var3, java.awt.font.TextHitInfo var4, java.awt.font.TextHitInfo var5) { super((java.lang.Object) null, 0); }
	public void consume() { }
	public java.awt.font.TextHitInfo getCaret() { return null; }
	public int getCommittedCharacterCount() { return 0; }
	public java.text.AttributedCharacterIterator getText() { return null; }
	public java.awt.font.TextHitInfo getVisiblePosition() { return null; }
	public long getWhen() { return 0l; }
	public boolean isConsumed() { return false; }
	public final static int CARET_POSITION_CHANGED = 1101;
	public final static int INPUT_METHOD_FIRST = 1100;
	public final static int INPUT_METHOD_LAST = 1101;
	public final static int INPUT_METHOD_TEXT_CHANGED = 1100;
}

