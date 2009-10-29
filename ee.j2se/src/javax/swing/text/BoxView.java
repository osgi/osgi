/*
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

package javax.swing.text;
public class BoxView extends javax.swing.text.CompositeView {
	public BoxView(javax.swing.text.Element var0, int var1) { super((javax.swing.text.Element) null); }
	protected void baselineLayout(int var0, int var1, int[] var2, int[] var3) { }
	protected javax.swing.SizeRequirements baselineRequirements(int var0, javax.swing.SizeRequirements var1) { return null; }
	protected javax.swing.SizeRequirements calculateMajorAxisRequirements(int var0, javax.swing.SizeRequirements var1) { return null; }
	protected javax.swing.SizeRequirements calculateMinorAxisRequirements(int var0, javax.swing.SizeRequirements var1) { return null; }
	protected void childAllocation(int var0, java.awt.Rectangle var1) { }
	public int getAxis() { return 0; }
	public int getHeight() { return 0; }
	protected int getOffset(int var0, int var1) { return 0; }
	public float getPreferredSpan(int var0) { return 0.0f; }
	protected int getSpan(int var0, int var1) { return 0; }
	protected javax.swing.text.View getViewAtPoint(int var0, int var1, java.awt.Rectangle var2) { return null; }
	public int getWidth() { return 0; }
	protected boolean isAfter(int var0, int var1, java.awt.Rectangle var2) { return false; }
	protected boolean isAllocationValid() { return false; }
	protected boolean isBefore(int var0, int var1, java.awt.Rectangle var2) { return false; }
	protected boolean isLayoutValid(int var0) { return false; }
	protected void layout(int var0, int var1) { }
	public void layoutChanged(int var0) { }
	protected void layoutMajorAxis(int var0, int var1, int[] var2, int[] var3) { }
	protected void layoutMinorAxis(int var0, int var1, int[] var2, int[] var3) { }
	public void paint(java.awt.Graphics var0, java.awt.Shape var1) { }
	protected void paintChild(java.awt.Graphics var0, java.awt.Rectangle var1, int var2) { }
	public void setAxis(int var0) { }
}

