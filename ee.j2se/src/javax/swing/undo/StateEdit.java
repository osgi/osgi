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
public class StateEdit extends javax.swing.undo.AbstractUndoableEdit {
	public StateEdit(javax.swing.undo.StateEditable var0) { }
	public StateEdit(javax.swing.undo.StateEditable var0, java.lang.String var1) { }
	public void end() { }
	protected void init(javax.swing.undo.StateEditable var0, java.lang.String var1) { }
	public void redo() { }
	protected void removeRedundantState() { }
	public void undo() { }
	protected final static java.lang.String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";
	protected javax.swing.undo.StateEditable object;
	protected java.util.Hashtable postState;
	protected java.util.Hashtable preState;
	protected java.lang.String undoRedoName;
}

