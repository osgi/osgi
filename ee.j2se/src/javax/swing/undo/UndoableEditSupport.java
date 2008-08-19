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
public class UndoableEditSupport {
	public UndoableEditSupport() { }
	public UndoableEditSupport(java.lang.Object var0) { }
	protected void _postEdit(javax.swing.undo.UndoableEdit var0) { }
	public void addUndoableEditListener(javax.swing.event.UndoableEditListener var0) { }
	public void beginUpdate() { }
	protected javax.swing.undo.CompoundEdit createCompoundEdit() { return null; }
	public void endUpdate() { }
	public javax.swing.event.UndoableEditListener[] getUndoableEditListeners() { return null; }
	public int getUpdateLevel() { return 0; }
	public void postEdit(javax.swing.undo.UndoableEdit var0) { }
	public void removeUndoableEditListener(javax.swing.event.UndoableEditListener var0) { }
	protected javax.swing.undo.CompoundEdit compoundEdit;
	protected java.util.Vector listeners;
	protected java.lang.Object realSource;
	protected int updateLevel;
}

