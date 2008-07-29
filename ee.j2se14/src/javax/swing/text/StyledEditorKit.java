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
public class StyledEditorKit extends javax.swing.text.DefaultEditorKit {
	public StyledEditorKit() { }
	protected void createInputAttributes(javax.swing.text.Element var0, javax.swing.text.MutableAttributeSet var1) { }
	public javax.swing.text.Element getCharacterAttributeRun() { return null; }
	public javax.swing.text.MutableAttributeSet getInputAttributes() { return null; }
	public static class AlignmentAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public AlignmentAction(java.lang.String var0, int var1) { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class BoldAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public BoldAction() { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class FontFamilyAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public FontFamilyAction(java.lang.String var0, java.lang.String var1) { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class FontSizeAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public FontSizeAction(java.lang.String var0, int var1) { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class ForegroundAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public ForegroundAction(java.lang.String var0, java.awt.Color var1) { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static class ItalicAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public ItalicAction() { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public static abstract class StyledTextAction extends javax.swing.text.TextAction {
		public StyledTextAction(java.lang.String var0) { super((java.lang.String) null); }
		protected final javax.swing.JEditorPane getEditor(java.awt.event.ActionEvent var0) { return null; }
		protected final javax.swing.text.StyledDocument getStyledDocument(javax.swing.JEditorPane var0) { return null; }
		protected final javax.swing.text.StyledEditorKit getStyledEditorKit(javax.swing.JEditorPane var0) { return null; }
		protected final void setCharacterAttributes(javax.swing.JEditorPane var0, javax.swing.text.AttributeSet var1, boolean var2) { }
		protected final void setParagraphAttributes(javax.swing.JEditorPane var0, javax.swing.text.AttributeSet var1, boolean var2) { }
	}
	public static class UnderlineAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public UnderlineAction() { super((java.lang.String) null); }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
}

