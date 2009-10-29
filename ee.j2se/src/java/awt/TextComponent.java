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
public class TextComponent extends java.awt.Component implements javax.accessibility.Accessible {
	public void addTextListener(java.awt.event.TextListener var0) { }
	public int getCaretPosition() { return 0; }
	public java.lang.String getSelectedText() { return null; }
	public int getSelectionEnd() { return 0; }
	public int getSelectionStart() { return 0; }
	public java.lang.String getText() { return null; }
	public java.awt.event.TextListener[] getTextListeners() { return null; }
	public boolean isEditable() { return false; }
	protected void processTextEvent(java.awt.event.TextEvent var0) { }
	public void removeTextListener(java.awt.event.TextListener var0) { }
	public void select(int var0, int var1) { }
	public void selectAll() { }
	public void setCaretPosition(int var0) { }
	public void setEditable(boolean var0) { }
	public void setSelectionEnd(int var0) { }
	public void setSelectionStart(int var0) { }
	public void setText(java.lang.String var0) { }
	protected java.awt.event.TextListener textListener;
	protected class AccessibleAWTTextComponent extends java.awt.Component.AccessibleAWTComponent implements java.awt.event.TextListener, javax.accessibility.AccessibleText {
		public AccessibleAWTTextComponent() { }
		public java.lang.String getAfterIndex(int var0, int var1) { return null; }
		public java.lang.String getAtIndex(int var0, int var1) { return null; }
		public java.lang.String getBeforeIndex(int var0, int var1) { return null; }
		public int getCaretPosition() { return 0; }
		public int getCharCount() { return 0; }
		public javax.swing.text.AttributeSet getCharacterAttribute(int var0) { return null; }
		public java.awt.Rectangle getCharacterBounds(int var0) { return null; }
		public int getIndexAtPoint(java.awt.Point var0) { return 0; }
		public java.lang.String getSelectedText() { return null; }
		public int getSelectionEnd() { return 0; }
		public int getSelectionStart() { return 0; }
		public void textValueChanged(java.awt.event.TextEvent var0) { }
	}
	TextComponent() { } /* generated constructor to prevent compiler adding default public constructor */
}

