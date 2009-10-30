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
public class DefaultEditorKit extends javax.swing.text.EditorKit {
	public static class BeepAction extends javax.swing.text.TextAction {
		public BeepAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class CopyAction extends javax.swing.text.TextAction {
		public CopyAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class CutAction extends javax.swing.text.TextAction {
		public CutAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class DefaultKeyTypedAction extends javax.swing.text.TextAction {
		public DefaultKeyTypedAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class InsertBreakAction extends javax.swing.text.TextAction {
		public InsertBreakAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class InsertContentAction extends javax.swing.text.TextAction {
		public InsertContentAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class InsertTabAction extends javax.swing.text.TextAction {
		public InsertTabAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class PasteAction extends javax.swing.text.TextAction {
		public PasteAction()  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public final static java.lang.String EndOfLineStringProperty = "__EndOfLine__";
	public final static java.lang.String backwardAction = "caret-backward";
	public final static java.lang.String beepAction = "beep";
	public final static java.lang.String beginAction = "caret-begin";
	public final static java.lang.String beginLineAction = "caret-begin-line";
	public final static java.lang.String beginParagraphAction = "caret-begin-paragraph";
	public final static java.lang.String beginWordAction = "caret-begin-word";
	public final static java.lang.String copyAction = "copy-to-clipboard";
	public final static java.lang.String cutAction = "cut-to-clipboard";
	public final static java.lang.String defaultKeyTypedAction = "default-typed";
	public final static java.lang.String deleteNextCharAction = "delete-next";
	public final static java.lang.String deletePrevCharAction = "delete-previous";
	public final static java.lang.String downAction = "caret-down";
	public final static java.lang.String endAction = "caret-end";
	public final static java.lang.String endLineAction = "caret-end-line";
	public final static java.lang.String endParagraphAction = "caret-end-paragraph";
	public final static java.lang.String endWordAction = "caret-end-word";
	public final static java.lang.String forwardAction = "caret-forward";
	public final static java.lang.String insertBreakAction = "insert-break";
	public final static java.lang.String insertContentAction = "insert-content";
	public final static java.lang.String insertTabAction = "insert-tab";
	public final static java.lang.String nextWordAction = "caret-next-word";
	public final static java.lang.String pageDownAction = "page-down";
	public final static java.lang.String pageUpAction = "page-up";
	public final static java.lang.String pasteAction = "paste-from-clipboard";
	public final static java.lang.String previousWordAction = "caret-previous-word";
	public final static java.lang.String readOnlyAction = "set-read-only";
	public final static java.lang.String selectAllAction = "select-all";
	public final static java.lang.String selectLineAction = "select-line";
	public final static java.lang.String selectParagraphAction = "select-paragraph";
	public final static java.lang.String selectWordAction = "select-word";
	public final static java.lang.String selectionBackwardAction = "selection-backward";
	public final static java.lang.String selectionBeginAction = "selection-begin";
	public final static java.lang.String selectionBeginLineAction = "selection-begin-line";
	public final static java.lang.String selectionBeginParagraphAction = "selection-begin-paragraph";
	public final static java.lang.String selectionBeginWordAction = "selection-begin-word";
	public final static java.lang.String selectionDownAction = "selection-down";
	public final static java.lang.String selectionEndAction = "selection-end";
	public final static java.lang.String selectionEndLineAction = "selection-end-line";
	public final static java.lang.String selectionEndParagraphAction = "selection-end-paragraph";
	public final static java.lang.String selectionEndWordAction = "selection-end-word";
	public final static java.lang.String selectionForwardAction = "selection-forward";
	public final static java.lang.String selectionNextWordAction = "selection-next-word";
	public final static java.lang.String selectionPreviousWordAction = "selection-previous-word";
	public final static java.lang.String selectionUpAction = "selection-up";
	public final static java.lang.String upAction = "caret-up";
	public final static java.lang.String writableAction = "set-writable";
	public DefaultEditorKit() { } 
	public javax.swing.text.Caret createCaret() { return null; }
	public javax.swing.text.Document createDefaultDocument() { return null; }
	public javax.swing.Action[] getActions() { return null; }
	public java.lang.String getContentType() { return null; }
	public javax.swing.text.ViewFactory getViewFactory() { return null; }
	public void read(java.io.InputStream var0, javax.swing.text.Document var1, int var2) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void read(java.io.Reader var0, javax.swing.text.Document var1, int var2) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void write(java.io.OutputStream var0, javax.swing.text.Document var1, int var2, int var3) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void write(java.io.Writer var0, javax.swing.text.Document var1, int var2, int var3) throws java.io.IOException, javax.swing.text.BadLocationException { }
}

