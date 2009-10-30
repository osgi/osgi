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
public interface Caret {
	void addChangeListener(javax.swing.event.ChangeListener var0);
	void deinstall(javax.swing.text.JTextComponent var0);
	int getBlinkRate();
	int getDot();
	java.awt.Point getMagicCaretPosition();
	int getMark();
	void install(javax.swing.text.JTextComponent var0);
	boolean isSelectionVisible();
	boolean isVisible();
	void moveDot(int var0);
	void paint(java.awt.Graphics var0);
	void removeChangeListener(javax.swing.event.ChangeListener var0);
	void setBlinkRate(int var0);
	void setDot(int var0);
	void setMagicCaretPosition(java.awt.Point var0);
	void setSelectionVisible(boolean var0);
	void setVisible(boolean var0);
}

