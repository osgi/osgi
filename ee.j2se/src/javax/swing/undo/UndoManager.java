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

package javax.swing.undo;
public class UndoManager extends javax.swing.undo.CompoundEdit implements javax.swing.event.UndoableEditListener {
	public UndoManager() { }
	public boolean addEdit(javax.swing.undo.UndoableEdit var0) { return false; }
	public boolean canRedo() { return false; }
	public boolean canUndo() { return false; }
	public boolean canUndoOrRedo() { return false; }
	public void discardAllEdits() { }
	protected javax.swing.undo.UndoableEdit editToBeRedone() { return null; }
	protected javax.swing.undo.UndoableEdit editToBeUndone() { return null; }
	public void end() { }
	public int getLimit() { return 0; }
	public java.lang.String getRedoPresentationName() { return null; }
	public java.lang.String getUndoOrRedoPresentationName() { return null; }
	public java.lang.String getUndoPresentationName() { return null; }
	public void redo() { }
	protected void redoTo(javax.swing.undo.UndoableEdit var0) { }
	public void setLimit(int var0) { }
	protected void trimEdits(int var0, int var1) { }
	protected void trimForLimit() { }
	public void undo() { }
	public void undoOrRedo() { }
	protected void undoTo(javax.swing.undo.UndoableEdit var0) { }
	public void undoableEditHappened(javax.swing.event.UndoableEditEvent var0) { }
}

