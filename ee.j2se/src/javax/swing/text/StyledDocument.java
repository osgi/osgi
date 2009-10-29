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
public abstract interface StyledDocument extends javax.swing.text.Document {
	public abstract javax.swing.text.Style addStyle(java.lang.String var0, javax.swing.text.Style var1);
	public abstract java.awt.Color getBackground(javax.swing.text.AttributeSet var0);
	public abstract javax.swing.text.Element getCharacterElement(int var0);
	public abstract java.awt.Font getFont(javax.swing.text.AttributeSet var0);
	public abstract java.awt.Color getForeground(javax.swing.text.AttributeSet var0);
	public abstract javax.swing.text.Style getLogicalStyle(int var0);
	public abstract javax.swing.text.Element getParagraphElement(int var0);
	public abstract javax.swing.text.Style getStyle(java.lang.String var0);
	public abstract void removeStyle(java.lang.String var0);
	public abstract void setCharacterAttributes(int var0, int var1, javax.swing.text.AttributeSet var2, boolean var3);
	public abstract void setLogicalStyle(int var0, javax.swing.text.Style var1);
	public abstract void setParagraphAttributes(int var0, int var1, javax.swing.text.AttributeSet var2, boolean var3);
}

