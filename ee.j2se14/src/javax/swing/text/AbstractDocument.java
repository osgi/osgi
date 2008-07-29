/*
 * $Date$
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
public abstract class AbstractDocument implements java.io.Serializable, javax.swing.text.Document {
	protected AbstractDocument(javax.swing.text.AbstractDocument.Content var0) { }
	protected AbstractDocument(javax.swing.text.AbstractDocument.Content var0, javax.swing.text.AbstractDocument.AttributeContext var1) { }
	public void addDocumentListener(javax.swing.event.DocumentListener var0) { }
	public void addUndoableEditListener(javax.swing.event.UndoableEditListener var0) { }
	protected javax.swing.text.Element createBranchElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1) { return null; }
	protected javax.swing.text.Element createLeafElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1, int var2, int var3) { return null; }
	public javax.swing.text.Position createPosition(int var0) throws javax.swing.text.BadLocationException { return null; }
	public void dump(java.io.PrintStream var0) { }
	protected void fireChangedUpdate(javax.swing.event.DocumentEvent var0) { }
	protected void fireInsertUpdate(javax.swing.event.DocumentEvent var0) { }
	protected void fireRemoveUpdate(javax.swing.event.DocumentEvent var0) { }
	protected void fireUndoableEditUpdate(javax.swing.event.UndoableEditEvent var0) { }
	public int getAsynchronousLoadPriority() { return 0; }
	protected final javax.swing.text.AbstractDocument.AttributeContext getAttributeContext() { return null; }
	public javax.swing.text.Element getBidiRootElement() { return null; }
	protected final javax.swing.text.AbstractDocument.Content getContent() { return null; }
	protected final java.lang.Thread getCurrentWriter() { return null; }
	public javax.swing.text.DocumentFilter getDocumentFilter() { return null; }
	public javax.swing.event.DocumentListener[] getDocumentListeners() { return null; }
	public java.util.Dictionary getDocumentProperties() { return null; }
	public final javax.swing.text.Position getEndPosition() { return null; }
	public int getLength() { return 0; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public abstract javax.swing.text.Element getParagraphElement(int var0);
	public final java.lang.Object getProperty(java.lang.Object var0) { return null; }
	public javax.swing.text.Element[] getRootElements() { return null; }
	public final javax.swing.text.Position getStartPosition() { return null; }
	public java.lang.String getText(int var0, int var1) throws javax.swing.text.BadLocationException { return null; }
	public void getText(int var0, int var1, javax.swing.text.Segment var2) throws javax.swing.text.BadLocationException { }
	public javax.swing.event.UndoableEditListener[] getUndoableEditListeners() { return null; }
	public void insertString(int var0, java.lang.String var1, javax.swing.text.AttributeSet var2) throws javax.swing.text.BadLocationException { }
	protected void insertUpdate(javax.swing.text.AbstractDocument.DefaultDocumentEvent var0, javax.swing.text.AttributeSet var1) { }
	protected void postRemoveUpdate(javax.swing.text.AbstractDocument.DefaultDocumentEvent var0) { }
	public final void putProperty(java.lang.Object var0, java.lang.Object var1) { }
	public final void readLock() { }
	public final void readUnlock() { }
	public void remove(int var0, int var1) throws javax.swing.text.BadLocationException { }
	public void removeDocumentListener(javax.swing.event.DocumentListener var0) { }
	public void removeUndoableEditListener(javax.swing.event.UndoableEditListener var0) { }
	protected void removeUpdate(javax.swing.text.AbstractDocument.DefaultDocumentEvent var0) { }
	public void render(java.lang.Runnable var0) { }
	public void replace(int var0, int var1, java.lang.String var2, javax.swing.text.AttributeSet var3) throws javax.swing.text.BadLocationException { }
	public void setAsynchronousLoadPriority(int var0) { }
	public void setDocumentFilter(javax.swing.text.DocumentFilter var0) { }
	public void setDocumentProperties(java.util.Dictionary var0) { }
	protected final void writeLock() { }
	protected final void writeUnlock() { }
	protected final static java.lang.String BAD_LOCATION = "document location failure";
	public final static java.lang.String BidiElementName = "bidi level";
	public final static java.lang.String ContentElementName = "content";
	public final static java.lang.String ElementNameAttribute = "$ename";
	public final static java.lang.String ParagraphElementName = "paragraph";
	public final static java.lang.String SectionElementName = "section";
	protected javax.swing.event.EventListenerList listenerList;
	public abstract class AbstractElement implements java.io.Serializable, javax.swing.text.Element, javax.swing.text.MutableAttributeSet, javax.swing.tree.TreeNode {
		public AbstractElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1) { }
		public void addAttribute(java.lang.Object var0, java.lang.Object var1) { }
		public void addAttributes(javax.swing.text.AttributeSet var0) { }
		public boolean containsAttribute(java.lang.Object var0, java.lang.Object var1) { return false; }
		public boolean containsAttributes(javax.swing.text.AttributeSet var0) { return false; }
		public javax.swing.text.AttributeSet copyAttributes() { return null; }
		public void dump(java.io.PrintStream var0, int var1) { }
		public java.lang.Object getAttribute(java.lang.Object var0) { return null; }
		public int getAttributeCount() { return 0; }
		public java.util.Enumeration getAttributeNames() { return null; }
		public javax.swing.text.AttributeSet getAttributes() { return null; }
		public javax.swing.tree.TreeNode getChildAt(int var0) { return null; }
		public int getChildCount() { return 0; }
		public javax.swing.text.Document getDocument() { return null; }
		public int getIndex(javax.swing.tree.TreeNode var0) { return 0; }
		public java.lang.String getName() { return null; }
		public javax.swing.tree.TreeNode getParent() { return null; }
		public javax.swing.text.Element getParentElement() { return null; }
		public javax.swing.text.AttributeSet getResolveParent() { return null; }
		public boolean isDefined(java.lang.Object var0) { return false; }
		public boolean isEqual(javax.swing.text.AttributeSet var0) { return false; }
		public void removeAttribute(java.lang.Object var0) { }
		public void removeAttributes(java.util.Enumeration var0) { }
		public void removeAttributes(javax.swing.text.AttributeSet var0) { }
		public void setResolveParent(javax.swing.text.AttributeSet var0) { }
	}
	public static abstract interface AttributeContext {
		public abstract javax.swing.text.AttributeSet addAttribute(javax.swing.text.AttributeSet var0, java.lang.Object var1, java.lang.Object var2);
		public abstract javax.swing.text.AttributeSet addAttributes(javax.swing.text.AttributeSet var0, javax.swing.text.AttributeSet var1);
		public abstract javax.swing.text.AttributeSet getEmptySet();
		public abstract void reclaim(javax.swing.text.AttributeSet var0);
		public abstract javax.swing.text.AttributeSet removeAttribute(javax.swing.text.AttributeSet var0, java.lang.Object var1);
		public abstract javax.swing.text.AttributeSet removeAttributes(javax.swing.text.AttributeSet var0, java.util.Enumeration var1);
		public abstract javax.swing.text.AttributeSet removeAttributes(javax.swing.text.AttributeSet var0, javax.swing.text.AttributeSet var1);
	}
	public class BranchElement extends javax.swing.text.AbstractDocument.AbstractElement {
		public BranchElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1) { super((javax.swing.text.Element) null, (javax.swing.text.AttributeSet) null); }
		public java.util.Enumeration children() { return null; }
		public boolean getAllowsChildren() { return false; }
		public javax.swing.text.Element getElement(int var0) { return null; }
		public int getElementCount() { return 0; }
		public int getElementIndex(int var0) { return 0; }
		public int getEndOffset() { return 0; }
		public int getStartOffset() { return 0; }
		public boolean isLeaf() { return false; }
		public javax.swing.text.Element positionToElement(int var0) { return null; }
		public void replace(int var0, int var1, javax.swing.text.Element[] var2) { }
	}
	public static abstract interface Content {
		public abstract javax.swing.text.Position createPosition(int var0) throws javax.swing.text.BadLocationException;
		public abstract void getChars(int var0, int var1, javax.swing.text.Segment var2) throws javax.swing.text.BadLocationException;
		public abstract java.lang.String getString(int var0, int var1) throws javax.swing.text.BadLocationException;
		public abstract javax.swing.undo.UndoableEdit insertString(int var0, java.lang.String var1) throws javax.swing.text.BadLocationException;
		public abstract int length();
		public abstract javax.swing.undo.UndoableEdit remove(int var0, int var1) throws javax.swing.text.BadLocationException;
	}
	public class DefaultDocumentEvent extends javax.swing.undo.CompoundEdit implements javax.swing.event.DocumentEvent {
		public DefaultDocumentEvent(int var0, int var1, javax.swing.event.DocumentEvent.EventType var2) { }
		public javax.swing.event.DocumentEvent.ElementChange getChange(javax.swing.text.Element var0) { return null; }
		public javax.swing.text.Document getDocument() { return null; }
		public int getLength() { return 0; }
		public int getOffset() { return 0; }
		public javax.swing.event.DocumentEvent.EventType getType() { return null; }
	}
	public static class ElementEdit extends javax.swing.undo.AbstractUndoableEdit implements javax.swing.event.DocumentEvent.ElementChange {
		public ElementEdit(javax.swing.text.Element var0, int var1, javax.swing.text.Element[] var2, javax.swing.text.Element[] var3) { }
		public javax.swing.text.Element[] getChildrenAdded() { return null; }
		public javax.swing.text.Element[] getChildrenRemoved() { return null; }
		public javax.swing.text.Element getElement() { return null; }
		public int getIndex() { return 0; }
	}
	public class LeafElement extends javax.swing.text.AbstractDocument.AbstractElement {
		public LeafElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1, int var2, int var3) { super((javax.swing.text.Element) null, (javax.swing.text.AttributeSet) null); }
		public java.util.Enumeration children() { return null; }
		public boolean getAllowsChildren() { return false; }
		public javax.swing.text.Element getElement(int var0) { return null; }
		public int getElementCount() { return 0; }
		public int getElementIndex(int var0) { return 0; }
		public int getEndOffset() { return 0; }
		public int getStartOffset() { return 0; }
		public boolean isLeaf() { return false; }
	}
}

