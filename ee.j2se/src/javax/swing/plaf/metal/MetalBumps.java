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

package javax.swing.plaf.metal;
class MetalBumps implements javax.swing.Icon {
	protected java.awt.Color backColor;
	protected javax.swing.plaf.metal.BumpBuffer buffer;
	protected static java.util.Vector buffers;
	protected java.awt.Color shadowColor;
	protected java.awt.Color topColor;
	protected int xBumps;
	protected int yBumps;
	public MetalBumps(int var0, int var1) { } 
	public MetalBumps(int var0, int var1, java.awt.Color var2, java.awt.Color var3, java.awt.Color var4) { } 
	public MetalBumps(java.awt.Dimension var0) { } 
	public int getIconHeight() { return 0; }
	public int getIconWidth() { return 0; }
	public void paintIcon(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
	public void setBumpArea(int var0, int var1) { }
	public void setBumpArea(java.awt.Dimension var0) { }
	public void setBumpColors(java.awt.Color var0, java.awt.Color var1, java.awt.Color var2) { }
}

