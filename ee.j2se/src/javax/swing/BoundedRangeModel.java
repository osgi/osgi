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
public abstract interface BoundedRangeModel {
	public abstract void addChangeListener(javax.swing.event.ChangeListener var0);
	public abstract int getExtent();
	public abstract int getMaximum();
	public abstract int getMinimum();
	public abstract int getValue();
	public abstract boolean getValueIsAdjusting();
	public abstract void removeChangeListener(javax.swing.event.ChangeListener var0);
	public abstract void setExtent(int var0);
	public abstract void setMaximum(int var0);
	public abstract void setMinimum(int var0);
	public abstract void setRangeProperties(int var0, int var1, int var2, int var3, boolean var4);
	public abstract void setValue(int var0);
	public abstract void setValueIsAdjusting(boolean var0);
}

