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

package javax.swing.undo;
public class AbstractUndoableEdit implements java.io.Serializable, javax.swing.undo.UndoableEdit {
	protected final static java.lang.String RedoName = "Redo";
	protected final static java.lang.String UndoName = "Undo";
	public AbstractUndoableEdit() { } 
	public boolean addEdit(javax.swing.undo.UndoableEdit var0) { return false; }
	public boolean canRedo() { return false; }
	public boolean canUndo() { return false; }
	public void die() { }
	public java.lang.String getPresentationName() { return null; }
	public java.lang.String getRedoPresentationName() { return null; }
	public java.lang.String getUndoPresentationName() { return null; }
	public boolean isSignificant() { return false; }
	public void redo() { }
	public boolean replaceEdit(javax.swing.undo.UndoableEdit var0) { return false; }
	public void undo() { }
}

