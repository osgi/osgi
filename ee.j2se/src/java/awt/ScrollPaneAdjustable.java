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
public class ScrollPaneAdjustable implements java.awt.Adjustable, java.io.Serializable {
	public void addAdjustmentListener(java.awt.event.AdjustmentListener var0) { }
	public java.awt.event.AdjustmentListener[] getAdjustmentListeners() { return null; }
	public int getBlockIncrement() { return 0; }
	public int getMaximum() { return 0; }
	public int getMinimum() { return 0; }
	public int getOrientation() { return 0; }
	public int getUnitIncrement() { return 0; }
	public int getValue() { return 0; }
	public boolean getValueIsAdjusting() { return false; }
	public int getVisibleAmount() { return 0; }
	public java.lang.String paramString() { return null; }
	public void removeAdjustmentListener(java.awt.event.AdjustmentListener var0) { }
	public void setBlockIncrement(int var0) { }
	public void setMaximum(int var0) { }
	public void setMinimum(int var0) { }
	public void setUnitIncrement(int var0) { }
	public void setValue(int var0) { }
	public void setValueIsAdjusting(boolean var0) { }
	public void setVisibleAmount(int var0) { }
	private ScrollPaneAdjustable() { } /* generated constructor to prevent compiler adding default public constructor */
}

