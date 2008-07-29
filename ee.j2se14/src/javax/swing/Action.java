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
public abstract interface Action extends java.awt.event.ActionListener {
	public abstract void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
	public abstract java.lang.Object getValue(java.lang.String var0);
	public abstract boolean isEnabled();
	public abstract void putValue(java.lang.String var0, java.lang.Object var1);
	public abstract void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
	public abstract void setEnabled(boolean var0);
	public final static java.lang.String ACCELERATOR_KEY = "AcceleratorKey";
	public final static java.lang.String ACTION_COMMAND_KEY = "ActionCommandKey";
	public final static java.lang.String DEFAULT = "Default";
	public final static java.lang.String LONG_DESCRIPTION = "LongDescription";
	public final static java.lang.String MNEMONIC_KEY = "MnemonicKey";
	public final static java.lang.String NAME = "Name";
	public final static java.lang.String SHORT_DESCRIPTION = "ShortDescription";
	public final static java.lang.String SMALL_ICON = "SmallIcon";
}

