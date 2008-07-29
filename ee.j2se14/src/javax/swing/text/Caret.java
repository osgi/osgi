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

package javax.swing.text;
public abstract interface Caret {
	public abstract void addChangeListener(javax.swing.event.ChangeListener var0);
	public abstract void deinstall(javax.swing.text.JTextComponent var0);
	public abstract int getBlinkRate();
	public abstract int getDot();
	public abstract java.awt.Point getMagicCaretPosition();
	public abstract int getMark();
	public abstract void install(javax.swing.text.JTextComponent var0);
	public abstract boolean isSelectionVisible();
	public abstract boolean isVisible();
	public abstract void moveDot(int var0);
	public abstract void paint(java.awt.Graphics var0);
	public abstract void removeChangeListener(javax.swing.event.ChangeListener var0);
	public abstract void setBlinkRate(int var0);
	public abstract void setDot(int var0);
	public abstract void setMagicCaretPosition(java.awt.Point var0);
	public abstract void setSelectionVisible(boolean var0);
	public abstract void setVisible(boolean var0);
}

