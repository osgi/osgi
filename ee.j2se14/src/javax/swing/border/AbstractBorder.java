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

package javax.swing.border;
public abstract class AbstractBorder implements java.io.Serializable, javax.swing.border.Border {
	public AbstractBorder() { }
	public java.awt.Insets getBorderInsets(java.awt.Component var0) { return null; }
	public java.awt.Insets getBorderInsets(java.awt.Component var0, java.awt.Insets var1) { return null; }
	public java.awt.Rectangle getInteriorRectangle(java.awt.Component var0, int var1, int var2, int var3, int var4) { return null; }
	public static java.awt.Rectangle getInteriorRectangle(java.awt.Component var0, javax.swing.border.Border var1, int var2, int var3, int var4, int var5) { return null; }
	public boolean isBorderOpaque() { return false; }
	public void paintBorder(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5) { }
}

