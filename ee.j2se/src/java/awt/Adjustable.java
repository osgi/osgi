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

package java.awt;
public abstract interface Adjustable {
	public abstract void addAdjustmentListener(java.awt.event.AdjustmentListener var0);
	public abstract int getBlockIncrement();
	public abstract int getMaximum();
	public abstract int getMinimum();
	public abstract int getOrientation();
	public abstract int getUnitIncrement();
	public abstract int getValue();
	public abstract int getVisibleAmount();
	public abstract void removeAdjustmentListener(java.awt.event.AdjustmentListener var0);
	public abstract void setBlockIncrement(int var0);
	public abstract void setMaximum(int var0);
	public abstract void setMinimum(int var0);
	public abstract void setUnitIncrement(int var0);
	public abstract void setValue(int var0);
	public abstract void setVisibleAmount(int var0);
	public final static int HORIZONTAL = 0;
	public final static int NO_ORIENTATION = 2;
	public final static int VERTICAL = 1;
}

