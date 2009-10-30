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
public interface Keymap {
	void addActionForKeyStroke(javax.swing.KeyStroke var0, javax.swing.Action var1);
	javax.swing.Action getAction(javax.swing.KeyStroke var0);
	javax.swing.Action[] getBoundActions();
	javax.swing.KeyStroke[] getBoundKeyStrokes();
	javax.swing.Action getDefaultAction();
	javax.swing.KeyStroke[] getKeyStrokesForAction(javax.swing.Action var0);
	java.lang.String getName();
	javax.swing.text.Keymap getResolveParent();
	boolean isLocallyDefined(javax.swing.KeyStroke var0);
	void removeBindings();
	void removeKeyStrokeBinding(javax.swing.KeyStroke var0);
	void setDefaultAction(javax.swing.Action var0);
	void setResolveParent(javax.swing.text.Keymap var0);
}

