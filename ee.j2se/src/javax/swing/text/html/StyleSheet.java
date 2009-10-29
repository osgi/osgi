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

package javax.swing.text.html;
public class StyleSheet extends javax.swing.text.StyleContext {
	public StyleSheet() { }
	public javax.swing.text.AttributeSet addAttribute(javax.swing.text.AttributeSet var0, java.lang.Object var1, java.lang.Object var2) { return null; }
	public javax.swing.text.AttributeSet addAttributes(javax.swing.text.AttributeSet var0, javax.swing.text.AttributeSet var1) { return null; }
	public void addCSSAttribute(javax.swing.text.MutableAttributeSet var0, javax.swing.text.html.CSS.Attribute var1, java.lang.String var2) { }
	public boolean addCSSAttributeFromHTML(javax.swing.text.MutableAttributeSet var0, javax.swing.text.html.CSS.Attribute var1, java.lang.String var2) { return false; }
	public void addRule(java.lang.String var0) { }
	public void addStyleSheet(javax.swing.text.html.StyleSheet var0) { }
	public java.net.URL getBase() { return null; }
	public javax.swing.text.html.StyleSheet.BoxPainter getBoxPainter(javax.swing.text.AttributeSet var0) { return null; }
	public javax.swing.text.AttributeSet getDeclaration(java.lang.String var0) { return null; }
	public static int getIndexOfSize(float var0) { return 0; }
	public javax.swing.text.html.StyleSheet.ListPainter getListPainter(javax.swing.text.AttributeSet var0) { return null; }
	public float getPointSize(int var0) { return 0.0f; }
	public float getPointSize(java.lang.String var0) { return 0.0f; }
	public javax.swing.text.Style getRule(java.lang.String var0) { return null; }
	public javax.swing.text.Style getRule(javax.swing.text.html.HTML.Tag var0, javax.swing.text.Element var1) { return null; }
	public javax.swing.text.html.StyleSheet[] getStyleSheets() { return null; }
	public javax.swing.text.AttributeSet getViewAttributes(javax.swing.text.View var0) { return null; }
	public void importStyleSheet(java.net.URL var0) { }
	public void loadRules(java.io.Reader var0, java.net.URL var1) throws java.io.IOException { }
	public javax.swing.text.AttributeSet removeAttribute(javax.swing.text.AttributeSet var0, java.lang.Object var1) { return null; }
	public javax.swing.text.AttributeSet removeAttributes(javax.swing.text.AttributeSet var0, java.util.Enumeration var1) { return null; }
	public javax.swing.text.AttributeSet removeAttributes(javax.swing.text.AttributeSet var0, javax.swing.text.AttributeSet var1) { return null; }
	public void removeStyleSheet(javax.swing.text.html.StyleSheet var0) { }
	public void setBase(java.net.URL var0) { }
	public void setBaseFontSize(int var0) { }
	public void setBaseFontSize(java.lang.String var0) { }
	public java.awt.Color stringToColor(java.lang.String var0) { return null; }
	public javax.swing.text.AttributeSet translateHTMLToCSS(javax.swing.text.AttributeSet var0) { return null; }
	public static class BoxPainter implements java.io.Serializable {
		public float getInset(int var0, javax.swing.text.View var1) { return 0.0f; }
		public void paint(java.awt.Graphics var0, float var1, float var2, float var3, float var4, javax.swing.text.View var5) { }
		private BoxPainter() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static class ListPainter implements java.io.Serializable {
		public void paint(java.awt.Graphics var0, float var1, float var2, float var3, float var4, javax.swing.text.View var5, int var6) { }
		private ListPainter() { } /* generated constructor to prevent compiler adding default public constructor */
	}
}

