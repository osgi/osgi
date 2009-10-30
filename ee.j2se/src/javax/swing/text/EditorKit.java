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
public abstract class EditorKit implements java.io.Serializable, java.lang.Cloneable {
	public EditorKit() { } 
	public java.lang.Object clone() { return null; }
	public abstract javax.swing.text.Caret createCaret();
	public abstract javax.swing.text.Document createDefaultDocument();
	public void deinstall(javax.swing.JEditorPane var0) { }
	public abstract javax.swing.Action[] getActions();
	public abstract java.lang.String getContentType();
	public abstract javax.swing.text.ViewFactory getViewFactory();
	public void install(javax.swing.JEditorPane var0) { }
	public abstract void read(java.io.InputStream var0, javax.swing.text.Document var1, int var2) throws java.io.IOException, javax.swing.text.BadLocationException;
	public abstract void read(java.io.Reader var0, javax.swing.text.Document var1, int var2) throws java.io.IOException, javax.swing.text.BadLocationException;
	public abstract void write(java.io.OutputStream var0, javax.swing.text.Document var1, int var2, int var3) throws java.io.IOException, javax.swing.text.BadLocationException;
	public abstract void write(java.io.Writer var0, javax.swing.text.Document var1, int var2, int var3) throws java.io.IOException, javax.swing.text.BadLocationException;
}

