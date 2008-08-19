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

package javax.swing;
public class DefaultButtonModel implements java.io.Serializable, javax.swing.ButtonModel {
	public DefaultButtonModel() { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public void addItemListener(java.awt.event.ItemListener var0) { }
	protected void fireActionPerformed(java.awt.event.ActionEvent var0) { }
	protected void fireItemStateChanged(java.awt.event.ItemEvent var0) { }
	protected void fireStateChanged() { }
	public java.lang.String getActionCommand() { return null; }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public javax.swing.ButtonGroup getGroup() { return null; }
	public java.awt.event.ItemListener[] getItemListeners() { return null; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public int getMnemonic() { return 0; }
	public java.lang.Object[] getSelectedObjects() { return null; }
	public boolean isArmed() { return false; }
	public boolean isEnabled() { return false; }
	public boolean isPressed() { return false; }
	public boolean isRollover() { return false; }
	public boolean isSelected() { return false; }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void removeItemListener(java.awt.event.ItemListener var0) { }
	public void setActionCommand(java.lang.String var0) { }
	public void setArmed(boolean var0) { }
	public void setEnabled(boolean var0) { }
	public void setGroup(javax.swing.ButtonGroup var0) { }
	public void setMnemonic(int var0) { }
	public void setPressed(boolean var0) { }
	public void setRollover(boolean var0) { }
	public void setSelected(boolean var0) { }
	public final static int ARMED = 1;
	public final static int ENABLED = 8;
	public final static int PRESSED = 4;
	public final static int ROLLOVER = 16;
	public final static int SELECTED = 2;
	protected java.lang.String actionCommand;
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.ButtonGroup group;
	protected javax.swing.event.EventListenerList listenerList;
	protected int mnemonic;
	protected int stateMask;
}

