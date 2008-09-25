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

package javax.swing.text;
public class DefaultStyledDocument extends javax.swing.text.AbstractDocument implements javax.swing.text.StyledDocument {
	public DefaultStyledDocument() { super((javax.swing.text.AbstractDocument.Content) null, (javax.swing.text.AbstractDocument.AttributeContext) null); }
	public DefaultStyledDocument(javax.swing.text.AbstractDocument.Content var0, javax.swing.text.StyleContext var1) { super((javax.swing.text.AbstractDocument.Content) null, (javax.swing.text.AbstractDocument.AttributeContext) null); }
	public DefaultStyledDocument(javax.swing.text.StyleContext var0) { super((javax.swing.text.AbstractDocument.Content) null, (javax.swing.text.AbstractDocument.AttributeContext) null); }
	public javax.swing.text.Style addStyle(java.lang.String var0, javax.swing.text.Style var1) { return null; }
	protected void create(javax.swing.text.DefaultStyledDocument.ElementSpec[] var0) { }
	protected javax.swing.text.AbstractDocument.AbstractElement createDefaultRoot() { return null; }
	public java.awt.Color getBackground(javax.swing.text.AttributeSet var0) { return null; }
	public javax.swing.text.Element getCharacterElement(int var0) { return null; }
	public javax.swing.text.Element getDefaultRootElement() { return null; }
	public java.awt.Font getFont(javax.swing.text.AttributeSet var0) { return null; }
	public java.awt.Color getForeground(javax.swing.text.AttributeSet var0) { return null; }
	public javax.swing.text.Style getLogicalStyle(int var0) { return null; }
	public javax.swing.text.Element getParagraphElement(int var0) { return null; }
	public javax.swing.text.Style getStyle(java.lang.String var0) { return null; }
	public java.util.Enumeration getStyleNames() { return null; }
	protected void insert(int var0, javax.swing.text.DefaultStyledDocument.ElementSpec[] var1) throws javax.swing.text.BadLocationException { }
	public void removeStyle(java.lang.String var0) { }
	public void setCharacterAttributes(int var0, int var1, javax.swing.text.AttributeSet var2, boolean var3) { }
	public void setLogicalStyle(int var0, javax.swing.text.Style var1) { }
	public void setParagraphAttributes(int var0, int var1, javax.swing.text.AttributeSet var2, boolean var3) { }
	protected void styleChanged(javax.swing.text.Style var0) { }
	public final static int BUFFER_SIZE_DEFAULT = 4096;
	protected javax.swing.text.DefaultStyledDocument.ElementBuffer buffer;
	public static class AttributeUndoableEdit extends javax.swing.undo.AbstractUndoableEdit {
		public AttributeUndoableEdit(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1, boolean var2) { }
		protected javax.swing.text.AttributeSet copy;
		protected javax.swing.text.Element element;
		protected boolean isReplacing;
		protected javax.swing.text.AttributeSet newAttributes;
	}
	public class ElementBuffer implements java.io.Serializable {
		public ElementBuffer(javax.swing.text.Element var0) { }
		public void change(int var0, int var1, javax.swing.text.AbstractDocument.DefaultDocumentEvent var2) { }
		protected void changeUpdate() { }
		public javax.swing.text.Element clone(javax.swing.text.Element var0, javax.swing.text.Element var1) { return null; }
		public javax.swing.text.Element getRootElement() { return null; }
		public void insert(int var0, int var1, javax.swing.text.DefaultStyledDocument.ElementSpec[] var2, javax.swing.text.AbstractDocument.DefaultDocumentEvent var3) { }
		protected void insertUpdate(javax.swing.text.DefaultStyledDocument.ElementSpec[] var0) { }
		public void remove(int var0, int var1, javax.swing.text.AbstractDocument.DefaultDocumentEvent var2) { }
		protected void removeUpdate() { }
	}
	public static class ElementSpec {
		public ElementSpec(javax.swing.text.AttributeSet var0, short var1) { }
		public ElementSpec(javax.swing.text.AttributeSet var0, short var1, int var2) { }
		public ElementSpec(javax.swing.text.AttributeSet var0, short var1, char[] var2, int var3, int var4) { }
		public char[] getArray() { return null; }
		public javax.swing.text.AttributeSet getAttributes() { return null; }
		public short getDirection() { return 0; }
		public int getLength() { return 0; }
		public int getOffset() { return 0; }
		public short getType() { return 0; }
		public void setDirection(short var0) { }
		public void setType(short var0) { }
		public final static short ContentType = 3;
		public final static short EndTagType = 2;
		public final static short JoinFractureDirection = 7;
		public final static short JoinNextDirection = 5;
		public final static short JoinPreviousDirection = 4;
		public final static short OriginateDirection = 6;
		public final static short StartTagType = 1;
	}
	protected class SectionElement extends javax.swing.text.AbstractDocument.BranchElement {
		public SectionElement() { super((javax.swing.text.Element) null, (javax.swing.text.AttributeSet) null); }
	}
}

