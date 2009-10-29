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

package javax.swing.text;
public class StyleContext implements java.io.Serializable, javax.swing.text.AbstractDocument.AttributeContext {
	public StyleContext() { }
	public javax.swing.text.AttributeSet addAttribute(javax.swing.text.AttributeSet var0, java.lang.Object var1, java.lang.Object var2) { return null; }
	public javax.swing.text.AttributeSet addAttributes(javax.swing.text.AttributeSet var0, javax.swing.text.AttributeSet var1) { return null; }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public javax.swing.text.Style addStyle(java.lang.String var0, javax.swing.text.Style var1) { return null; }
	protected javax.swing.text.MutableAttributeSet createLargeAttributeSet(javax.swing.text.AttributeSet var0) { return null; }
	protected javax.swing.text.StyleContext.SmallAttributeSet createSmallAttributeSet(javax.swing.text.AttributeSet var0) { return null; }
	public java.awt.Color getBackground(javax.swing.text.AttributeSet var0) { return null; }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	protected int getCompressionThreshold() { return 0; }
	public final static javax.swing.text.StyleContext getDefaultStyleContext() { return null; }
	public javax.swing.text.AttributeSet getEmptySet() { return null; }
	public java.awt.Font getFont(java.lang.String var0, int var1, int var2) { return null; }
	public java.awt.Font getFont(javax.swing.text.AttributeSet var0) { return null; }
	public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
	public java.awt.Color getForeground(javax.swing.text.AttributeSet var0) { return null; }
	public static java.lang.Object getStaticAttribute(java.lang.Object var0) { return null; }
	public static java.lang.Object getStaticAttributeKey(java.lang.Object var0) { return null; }
	public javax.swing.text.Style getStyle(java.lang.String var0) { return null; }
	public java.util.Enumeration getStyleNames() { return null; }
	public static void readAttributeSet(java.io.ObjectInputStream var0, javax.swing.text.MutableAttributeSet var1) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public void readAttributes(java.io.ObjectInputStream var0, javax.swing.text.MutableAttributeSet var1) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public void reclaim(javax.swing.text.AttributeSet var0) { }
	public static void registerStaticAttributeKey(java.lang.Object var0) { }
	public javax.swing.text.AttributeSet removeAttribute(javax.swing.text.AttributeSet var0, java.lang.Object var1) { return null; }
	public javax.swing.text.AttributeSet removeAttributes(javax.swing.text.AttributeSet var0, java.util.Enumeration var1) { return null; }
	public javax.swing.text.AttributeSet removeAttributes(javax.swing.text.AttributeSet var0, javax.swing.text.AttributeSet var1) { return null; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void removeStyle(java.lang.String var0) { }
	public static void writeAttributeSet(java.io.ObjectOutputStream var0, javax.swing.text.AttributeSet var1) throws java.io.IOException { }
	public void writeAttributes(java.io.ObjectOutputStream var0, javax.swing.text.AttributeSet var1) throws java.io.IOException { }
	public final static java.lang.String DEFAULT_STYLE = "default";
	public class NamedStyle implements java.io.Serializable, javax.swing.text.Style {
		public NamedStyle() { }
		public NamedStyle(java.lang.String var0, javax.swing.text.Style var1) { }
		public NamedStyle(javax.swing.text.Style var0) { }
		public void addAttribute(java.lang.Object var0, java.lang.Object var1) { }
		public void addAttributes(javax.swing.text.AttributeSet var0) { }
		public void addChangeListener(javax.swing.event.ChangeListener var0) { }
		public boolean containsAttribute(java.lang.Object var0, java.lang.Object var1) { return false; }
		public boolean containsAttributes(javax.swing.text.AttributeSet var0) { return false; }
		public javax.swing.text.AttributeSet copyAttributes() { return null; }
		protected void fireStateChanged() { }
		public java.lang.Object getAttribute(java.lang.Object var0) { return null; }
		public int getAttributeCount() { return 0; }
		public java.util.Enumeration getAttributeNames() { return null; }
		public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
		public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
		public java.lang.String getName() { return null; }
		public javax.swing.text.AttributeSet getResolveParent() { return null; }
		public boolean isDefined(java.lang.Object var0) { return false; }
		public boolean isEqual(javax.swing.text.AttributeSet var0) { return false; }
		public void removeAttribute(java.lang.Object var0) { }
		public void removeAttributes(java.util.Enumeration var0) { }
		public void removeAttributes(javax.swing.text.AttributeSet var0) { }
		public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
		public void setName(java.lang.String var0) { }
		public void setResolveParent(javax.swing.text.AttributeSet var0) { }
		protected javax.swing.event.ChangeEvent changeEvent;
		protected javax.swing.event.EventListenerList listenerList;
	}
	public class SmallAttributeSet implements javax.swing.text.AttributeSet {
		public SmallAttributeSet(javax.swing.text.AttributeSet var0) { }
		public SmallAttributeSet(java.lang.Object[] var0) { }
		public java.lang.Object clone() { return null; }
		public boolean containsAttribute(java.lang.Object var0, java.lang.Object var1) { return false; }
		public boolean containsAttributes(javax.swing.text.AttributeSet var0) { return false; }
		public javax.swing.text.AttributeSet copyAttributes() { return null; }
		public java.lang.Object getAttribute(java.lang.Object var0) { return null; }
		public int getAttributeCount() { return 0; }
		public java.util.Enumeration getAttributeNames() { return null; }
		public javax.swing.text.AttributeSet getResolveParent() { return null; }
		public int hashCode() { return 0; }
		public boolean isDefined(java.lang.Object var0) { return false; }
		public boolean isEqual(javax.swing.text.AttributeSet var0) { return false; }
	}
}

