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

package javax.swing;
public abstract interface ButtonModel extends java.awt.ItemSelectable {
	public abstract void addActionListener(java.awt.event.ActionListener var0);
	public abstract void addChangeListener(javax.swing.event.ChangeListener var0);
	public abstract java.lang.String getActionCommand();
	public abstract int getMnemonic();
	public abstract boolean isArmed();
	public abstract boolean isEnabled();
	public abstract boolean isPressed();
	public abstract boolean isRollover();
	public abstract boolean isSelected();
	public abstract void removeActionListener(java.awt.event.ActionListener var0);
	public abstract void removeChangeListener(javax.swing.event.ChangeListener var0);
	public abstract void setActionCommand(java.lang.String var0);
	public abstract void setArmed(boolean var0);
	public abstract void setEnabled(boolean var0);
	public abstract void setGroup(javax.swing.ButtonGroup var0);
	public abstract void setMnemonic(int var0);
	public abstract void setPressed(boolean var0);
	public abstract void setRollover(boolean var0);
	public abstract void setSelected(boolean var0);
}

