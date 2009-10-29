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

package javax.swing.undo;
public abstract interface UndoableEdit {
	public abstract boolean addEdit(javax.swing.undo.UndoableEdit var0);
	public abstract boolean canRedo();
	public abstract boolean canUndo();
	public abstract void die();
	public abstract java.lang.String getPresentationName();
	public abstract java.lang.String getRedoPresentationName();
	public abstract java.lang.String getUndoPresentationName();
	public abstract boolean isSignificant();
	public abstract void redo();
	public abstract boolean replaceEdit(javax.swing.undo.UndoableEdit var0);
	public abstract void undo();
}

