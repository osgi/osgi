/*
 * $Revision$
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

package javax.swing.text;
public abstract interface Keymap {
	public abstract void addActionForKeyStroke(javax.swing.KeyStroke var0, javax.swing.Action var1);
	public abstract javax.swing.Action getAction(javax.swing.KeyStroke var0);
	public abstract javax.swing.Action[] getBoundActions();
	public abstract javax.swing.KeyStroke[] getBoundKeyStrokes();
	public abstract javax.swing.Action getDefaultAction();
	public abstract javax.swing.KeyStroke[] getKeyStrokesForAction(javax.swing.Action var0);
	public abstract java.lang.String getName();
	public abstract javax.swing.text.Keymap getResolveParent();
	public abstract boolean isLocallyDefined(javax.swing.KeyStroke var0);
	public abstract void removeBindings();
	public abstract void removeKeyStrokeBinding(javax.swing.KeyStroke var0);
	public abstract void setDefaultAction(javax.swing.Action var0);
	public abstract void setResolveParent(javax.swing.text.Keymap var0);
}

