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
public interface Action extends java.awt.event.ActionListener {
	public final static java.lang.String ACCELERATOR_KEY = "AcceleratorKey";
	public final static java.lang.String ACTION_COMMAND_KEY = "ActionCommandKey";
	public final static java.lang.String DEFAULT = "Default";
	public final static java.lang.String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
	public final static java.lang.String LARGE_ICON_KEY = "SwingLargeIconKey";
	public final static java.lang.String LONG_DESCRIPTION = "LongDescription";
	public final static java.lang.String MNEMONIC_KEY = "MnemonicKey";
	public final static java.lang.String NAME = "Name";
	public final static java.lang.String SELECTED_KEY = "SwingSelectedKey";
	public final static java.lang.String SHORT_DESCRIPTION = "ShortDescription";
	public final static java.lang.String SMALL_ICON = "SmallIcon";
	void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
	java.lang.Object getValue(java.lang.String var0);
	boolean isEnabled();
	void putValue(java.lang.String var0, java.lang.Object var1);
	void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
	void setEnabled(boolean var0);
}

