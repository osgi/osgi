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

package javax.swing;
public interface ButtonModel extends java.awt.ItemSelectable {
	void addActionListener(java.awt.event.ActionListener var0);
	void addChangeListener(javax.swing.event.ChangeListener var0);
	java.lang.String getActionCommand();
	int getMnemonic();
	boolean isArmed();
	boolean isEnabled();
	boolean isPressed();
	boolean isRollover();
	boolean isSelected();
	void removeActionListener(java.awt.event.ActionListener var0);
	void removeChangeListener(javax.swing.event.ChangeListener var0);
	void setActionCommand(java.lang.String var0);
	void setArmed(boolean var0);
	void setEnabled(boolean var0);
	void setGroup(javax.swing.ButtonGroup var0);
	void setMnemonic(int var0);
	void setPressed(boolean var0);
	void setRollover(boolean var0);
	void setSelected(boolean var0);
}

