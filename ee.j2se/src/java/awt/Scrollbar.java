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

package java.awt;
public class Scrollbar extends java.awt.Component implements java.awt.Adjustable, javax.accessibility.Accessible {
	public Scrollbar() { }
	public Scrollbar(int var0) { }
	public Scrollbar(int var0, int var1, int var2, int var3, int var4) { }
	public void addAdjustmentListener(java.awt.event.AdjustmentListener var0) { }
	public java.awt.event.AdjustmentListener[] getAdjustmentListeners() { return null; }
	public int getBlockIncrement() { return 0; }
	/** @deprecated */ public int getLineIncrement() { return 0; }
	public int getMaximum() { return 0; }
	public int getMinimum() { return 0; }
	public int getOrientation() { return 0; }
	/** @deprecated */ public int getPageIncrement() { return 0; }
	public int getUnitIncrement() { return 0; }
	public int getValue() { return 0; }
	public boolean getValueIsAdjusting() { return false; }
	/** @deprecated */ public int getVisible() { return 0; }
	public int getVisibleAmount() { return 0; }
	protected void processAdjustmentEvent(java.awt.event.AdjustmentEvent var0) { }
	public void removeAdjustmentListener(java.awt.event.AdjustmentListener var0) { }
	public void setBlockIncrement(int var0) { }
	/** @deprecated */ public void setLineIncrement(int var0) { }
	public void setMaximum(int var0) { }
	public void setMinimum(int var0) { }
	public void setOrientation(int var0) { }
	/** @deprecated */ public void setPageIncrement(int var0) { }
	public void setUnitIncrement(int var0) { }
	public void setValue(int var0) { }
	public void setValueIsAdjusting(boolean var0) { }
	public void setValues(int var0, int var1, int var2, int var3) { }
	public void setVisibleAmount(int var0) { }
	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;
	protected class AccessibleAWTScrollBar extends java.awt.Component.AccessibleAWTComponent implements javax.accessibility.AccessibleValue {
		protected AccessibleAWTScrollBar() { }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
}

