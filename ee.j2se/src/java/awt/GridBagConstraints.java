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

package java.awt;
public class GridBagConstraints implements java.io.Serializable, java.lang.Cloneable {
	public GridBagConstraints() { }
	public GridBagConstraints(int var0, int var1, int var2, int var3, double var4, double var5, int var6, int var7, java.awt.Insets var8, int var9, int var10) { }
	public java.lang.Object clone() { return null; }
	public final static int BOTH = 1;
	public final static int CENTER = 10;
	public final static int EAST = 13;
	public final static int FIRST_LINE_END = 24;
	public final static int FIRST_LINE_START = 23;
	public final static int HORIZONTAL = 2;
	public final static int LAST_LINE_END = 26;
	public final static int LAST_LINE_START = 25;
	public final static int LINE_END = 22;
	public final static int LINE_START = 21;
	public final static int NONE = 0;
	public final static int NORTH = 11;
	public final static int NORTHEAST = 12;
	public final static int NORTHWEST = 18;
	public final static int PAGE_END = 20;
	public final static int PAGE_START = 19;
	public final static int RELATIVE = -1;
	public final static int REMAINDER = 0;
	public final static int SOUTH = 15;
	public final static int SOUTHEAST = 14;
	public final static int SOUTHWEST = 16;
	public final static int VERTICAL = 3;
	public final static int WEST = 17;
	public int anchor;
	public int fill;
	public int gridheight;
	public int gridwidth;
	public int gridx;
	public int gridy;
	public java.awt.Insets insets;
	public int ipadx;
	public int ipady;
	public double weightx;
	public double weighty;
}

