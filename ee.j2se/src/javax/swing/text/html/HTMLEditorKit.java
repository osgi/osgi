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

package javax.swing.text.html;
public class HTMLEditorKit extends javax.swing.text.StyledEditorKit implements javax.accessibility.Accessible {
	public static class HTMLFactory implements javax.swing.text.ViewFactory {
		public HTMLFactory() { } 
		public javax.swing.text.View create(javax.swing.text.Element var0) { return null; }
	}
	public static abstract class HTMLTextAction extends javax.swing.text.StyledEditorKit.StyledTextAction {
		public HTMLTextAction(java.lang.String var0)  { super((java.lang.String) null); } 
		protected int elementCountToTag(javax.swing.text.html.HTMLDocument var0, int var1, javax.swing.text.html.HTML.Tag var2) { return 0; }
		protected javax.swing.text.Element findElementMatchingTag(javax.swing.text.html.HTMLDocument var0, int var1, javax.swing.text.html.HTML.Tag var2) { return null; }
		protected javax.swing.text.Element[] getElementsAt(javax.swing.text.html.HTMLDocument var0, int var1) { return null; }
		protected javax.swing.text.html.HTMLDocument getHTMLDocument(javax.swing.JEditorPane var0) { return null; }
		protected javax.swing.text.html.HTMLEditorKit getHTMLEditorKit(javax.swing.JEditorPane var0) { return null; }
	}
	public static class InsertHTMLTextAction extends javax.swing.text.html.HTMLEditorKit.HTMLTextAction {
		protected javax.swing.text.html.HTML.Tag addTag;
		protected javax.swing.text.html.HTML.Tag alternateAddTag;
		protected javax.swing.text.html.HTML.Tag alternateParentTag;
		protected java.lang.String html;
		protected javax.swing.text.html.HTML.Tag parentTag;
		public InsertHTMLTextAction(java.lang.String var0, java.lang.String var1, javax.swing.text.html.HTML.Tag var2, javax.swing.text.html.HTML.Tag var3)  { super((java.lang.String) null); } 
		public InsertHTMLTextAction(java.lang.String var0, java.lang.String var1, javax.swing.text.html.HTML.Tag var2, javax.swing.text.html.HTML.Tag var3, javax.swing.text.html.HTML.Tag var4, javax.swing.text.html.HTML.Tag var5)  { super((java.lang.String) null); } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected void insertAtBoundary(javax.swing.JEditorPane var0, javax.swing.text.html.HTMLDocument var1, int var2, javax.swing.text.Element var3, java.lang.String var4, javax.swing.text.html.HTML.Tag var5, javax.swing.text.html.HTML.Tag var6) { }
		/** @deprecated */
		@java.lang.Deprecated
		protected void insertAtBoundry(javax.swing.JEditorPane var0, javax.swing.text.html.HTMLDocument var1, int var2, javax.swing.text.Element var3, java.lang.String var4, javax.swing.text.html.HTML.Tag var5, javax.swing.text.html.HTML.Tag var6) { }
		protected void insertHTML(javax.swing.JEditorPane var0, javax.swing.text.html.HTMLDocument var1, int var2, java.lang.String var3, int var4, int var5, javax.swing.text.html.HTML.Tag var6) { }
	}
	public static class LinkController extends java.awt.event.MouseAdapter implements java.awt.event.MouseMotionListener, java.io.Serializable {
		public LinkController() { } 
		protected void activateLink(int var0, javax.swing.JEditorPane var1) { }
	}
	public static abstract class Parser {
		public Parser() { } 
		public abstract void parse(java.io.Reader var0, javax.swing.text.html.HTMLEditorKit.ParserCallback var1, boolean var2) throws java.io.IOException;
	}
	public static class ParserCallback {
		public final static java.lang.Object IMPLIED; static { IMPLIED = null; }
		public ParserCallback() { } 
		public void flush() throws javax.swing.text.BadLocationException { }
		public void handleComment(char[] var0, int var1) { }
		public void handleEndOfLineString(java.lang.String var0) { }
		public void handleEndTag(javax.swing.text.html.HTML.Tag var0, int var1) { }
		public void handleError(java.lang.String var0, int var1) { }
		public void handleSimpleTag(javax.swing.text.html.HTML.Tag var0, javax.swing.text.MutableAttributeSet var1, int var2) { }
		public void handleStartTag(javax.swing.text.html.HTML.Tag var0, javax.swing.text.MutableAttributeSet var1, int var2) { }
		public void handleText(char[] var0, int var1) { }
	}
	public final static java.lang.String BOLD_ACTION = "html-bold-action";
	public final static java.lang.String COLOR_ACTION = "html-color-action";
	public final static java.lang.String DEFAULT_CSS = "default.css";
	public final static java.lang.String FONT_CHANGE_BIGGER = "html-font-bigger";
	public final static java.lang.String FONT_CHANGE_SMALLER = "html-font-smaller";
	public final static java.lang.String IMG_ALIGN_BOTTOM = "html-image-align-bottom";
	public final static java.lang.String IMG_ALIGN_MIDDLE = "html-image-align-middle";
	public final static java.lang.String IMG_ALIGN_TOP = "html-image-align-top";
	public final static java.lang.String IMG_BORDER = "html-image-border";
	public final static java.lang.String ITALIC_ACTION = "html-italic-action";
	public final static java.lang.String LOGICAL_STYLE_ACTION = "html-logical-style-action";
	public final static java.lang.String PARA_INDENT_LEFT = "html-para-indent-left";
	public final static java.lang.String PARA_INDENT_RIGHT = "html-para-indent-right";
	public HTMLEditorKit() { } 
	public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
	public java.awt.Cursor getDefaultCursor() { return null; }
	public java.awt.Cursor getLinkCursor() { return null; }
	protected javax.swing.text.html.HTMLEditorKit.Parser getParser() { return null; }
	public javax.swing.text.html.StyleSheet getStyleSheet() { return null; }
	public void insertHTML(javax.swing.text.html.HTMLDocument var0, int var1, java.lang.String var2, int var3, int var4, javax.swing.text.html.HTML.Tag var5) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public boolean isAutoFormSubmission() { return false; }
	public void setAutoFormSubmission(boolean var0) { }
	public void setDefaultCursor(java.awt.Cursor var0) { }
	public void setLinkCursor(java.awt.Cursor var0) { }
	public void setStyleSheet(javax.swing.text.html.StyleSheet var0) { }
}

