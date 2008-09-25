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

package javax.swing;
public class SizeRequirements implements java.io.Serializable {
	public SizeRequirements() { }
	public SizeRequirements(int var0, int var1, int var2, float var3) { }
	public static int[] adjustSizes(int var0, javax.swing.SizeRequirements[] var1) { return null; }
	public static void calculateAlignedPositions(int var0, javax.swing.SizeRequirements var1, javax.swing.SizeRequirements[] var2, int[] var3, int[] var4) { }
	public static void calculateAlignedPositions(int var0, javax.swing.SizeRequirements var1, javax.swing.SizeRequirements[] var2, int[] var3, int[] var4, boolean var5) { }
	public static void calculateTiledPositions(int var0, javax.swing.SizeRequirements var1, javax.swing.SizeRequirements[] var2, int[] var3, int[] var4) { }
	public static void calculateTiledPositions(int var0, javax.swing.SizeRequirements var1, javax.swing.SizeRequirements[] var2, int[] var3, int[] var4, boolean var5) { }
	public static javax.swing.SizeRequirements getAlignedSizeRequirements(javax.swing.SizeRequirements[] var0) { return null; }
	public static javax.swing.SizeRequirements getTiledSizeRequirements(javax.swing.SizeRequirements[] var0) { return null; }
	public float alignment;
	public int maximum;
	public int minimum;
	public int preferred;
}

