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
public abstract interface Document {
	public abstract void addDocumentListener(javax.swing.event.DocumentListener var0);
	public abstract void addUndoableEditListener(javax.swing.event.UndoableEditListener var0);
	public abstract javax.swing.text.Position createPosition(int var0) throws javax.swing.text.BadLocationException;
	public abstract javax.swing.text.Element getDefaultRootElement();
	public abstract javax.swing.text.Position getEndPosition();
	public abstract int getLength();
	public abstract java.lang.Object getProperty(java.lang.Object var0);
	public abstract javax.swing.text.Element[] getRootElements();
	public abstract javax.swing.text.Position getStartPosition();
	public abstract java.lang.String getText(int var0, int var1) throws javax.swing.text.BadLocationException;
	public abstract void getText(int var0, int var1, javax.swing.text.Segment var2) throws javax.swing.text.BadLocationException;
	public abstract void insertString(int var0, java.lang.String var1, javax.swing.text.AttributeSet var2) throws javax.swing.text.BadLocationException;
	public abstract void putProperty(java.lang.Object var0, java.lang.Object var1);
	public abstract void remove(int var0, int var1) throws javax.swing.text.BadLocationException;
	public abstract void removeDocumentListener(javax.swing.event.DocumentListener var0);
	public abstract void removeUndoableEditListener(javax.swing.event.UndoableEditListener var0);
	public abstract void render(java.lang.Runnable var0);
	public final static java.lang.String StreamDescriptionProperty = "stream";
	public final static java.lang.String TitleProperty = "title";
}

