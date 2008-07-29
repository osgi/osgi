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

package javax.swing;
public class JEditorPane extends javax.swing.text.JTextComponent {
	public JEditorPane() { }
	public JEditorPane(java.lang.String var0) throws java.io.IOException { }
	public JEditorPane(java.lang.String var0, java.lang.String var1) { }
	public JEditorPane(java.net.URL var0) throws java.io.IOException { }
	public void addHyperlinkListener(javax.swing.event.HyperlinkListener var0) { }
	protected javax.swing.text.EditorKit createDefaultEditorKit() { return null; }
	public static javax.swing.text.EditorKit createEditorKitForContentType(java.lang.String var0) { return null; }
	public void fireHyperlinkUpdate(javax.swing.event.HyperlinkEvent var0) { }
	public final java.lang.String getContentType() { return null; }
	public javax.swing.text.EditorKit getEditorKit() { return null; }
	public static java.lang.String getEditorKitClassNameForContentType(java.lang.String var0) { return null; }
	public javax.swing.text.EditorKit getEditorKitForContentType(java.lang.String var0) { return null; }
	public javax.swing.event.HyperlinkListener[] getHyperlinkListeners() { return null; }
	public java.net.URL getPage() { return null; }
	protected java.io.InputStream getStream(java.net.URL var0) throws java.io.IOException { return null; }
	public void read(java.io.InputStream var0, java.lang.Object var1) throws java.io.IOException { }
	public static void registerEditorKitForContentType(java.lang.String var0, java.lang.String var1) { }
	public static void registerEditorKitForContentType(java.lang.String var0, java.lang.String var1, java.lang.ClassLoader var2) { }
	public void removeHyperlinkListener(javax.swing.event.HyperlinkListener var0) { }
	public void scrollToReference(java.lang.String var0) { }
	public final void setContentType(java.lang.String var0) { }
	public void setEditorKit(javax.swing.text.EditorKit var0) { }
	public void setEditorKitForContentType(java.lang.String var0, javax.swing.text.EditorKit var1) { }
	public void setPage(java.lang.String var0) throws java.io.IOException { }
	public void setPage(java.net.URL var0) throws java.io.IOException { }
	protected class AccessibleJEditorPane extends javax.swing.text.JTextComponent.AccessibleJTextComponent {
		protected AccessibleJEditorPane() { }
	}
	protected class AccessibleJEditorPaneHTML extends javax.swing.JEditorPane.AccessibleJEditorPane {
		protected AccessibleJEditorPaneHTML() { }
	}
	protected class JEditorPaneAccessibleHypertextSupport extends javax.swing.JEditorPane.AccessibleJEditorPane implements javax.accessibility.AccessibleHypertext {
		public JEditorPaneAccessibleHypertextSupport() { }
		public javax.accessibility.AccessibleHyperlink getLink(int var0) { return null; }
		public int getLinkCount() { return 0; }
		public int getLinkIndex(int var0) { return 0; }
		public java.lang.String getLinkText(int var0) { return null; }
		public class HTMLLink extends javax.accessibility.AccessibleHyperlink {
			public HTMLLink(javax.swing.text.Element var0) { }
			public boolean doAccessibleAction(int var0) { return false; }
			public java.lang.Object getAccessibleActionAnchor(int var0) { return null; }
			public int getAccessibleActionCount() { return 0; }
			public java.lang.String getAccessibleActionDescription(int var0) { return null; }
			public java.lang.Object getAccessibleActionObject(int var0) { return null; }
			public int getEndIndex() { return 0; }
			public int getStartIndex() { return 0; }
			public boolean isValid() { return false; }
		}
	}
}

