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

package sun.java2d.pipe;
public class Region {
	public final static sun.java2d.pipe.Region EMPTY_REGION; static { EMPTY_REGION = null; }
	public final static sun.java2d.pipe.Region WHOLE_REGION; static { WHOLE_REGION = null; }
	protected Region(int var0, int var1, int var2, int var3) { } 
	public void appendSpans(sun.java2d.pipe.SpanIterator var0) { }
	public static int clipAdd(int var0, int var1) { return 0; }
	public void clipBoxToBounds(int[] var0) { }
	public boolean contains(int var0, int var1) { return false; }
	public static int dimAdd(int var0, int var1) { return 0; }
	public boolean encompasses(sun.java2d.pipe.Region var0) { return false; }
	public boolean encompassesXYWH(int var0, int var1, int var2, int var3) { return false; }
	public boolean encompassesXYXY(int var0, int var1, int var2, int var3) { return false; }
	public sun.java2d.pipe.SpanIterator filter(sun.java2d.pipe.SpanIterator var0) { return null; }
	public void getBounds(int[] var0) { }
	public sun.java2d.pipe.Region getBoundsIntersection(java.awt.Rectangle var0) { return null; }
	public sun.java2d.pipe.Region getBoundsIntersection(sun.java2d.pipe.Region var0) { return null; }
	public sun.java2d.pipe.Region getBoundsIntersectionXYWH(int var0, int var1, int var2, int var3) { return null; }
	public sun.java2d.pipe.Region getBoundsIntersectionXYXY(int var0, int var1, int var2, int var3) { return null; }
	public sun.java2d.pipe.Region getDifference(sun.java2d.pipe.Region var0) { return null; }
	public sun.java2d.pipe.Region getExclusiveOr(sun.java2d.pipe.Region var0) { return null; }
	public final int getHeight() { return 0; }
	public final int getHiX() { return 0; }
	public final int getHiY() { return 0; }
	public static sun.java2d.pipe.Region getInstance(java.awt.Rectangle var0) { return null; }
	public static sun.java2d.pipe.Region getInstance(java.awt.Shape var0, java.awt.geom.AffineTransform var1) { return null; }
	public static sun.java2d.pipe.Region getInstance(sun.java2d.pipe.Region var0, java.awt.Shape var1, java.awt.geom.AffineTransform var2) { return null; }
	public static sun.java2d.pipe.Region getInstance(sun.java2d.pipe.Region var0, boolean var1, java.awt.Shape var2, java.awt.geom.AffineTransform var3) { return null; }
	public static sun.java2d.pipe.Region getInstance(int[] var0) { return null; }
	public static sun.java2d.pipe.Region getInstanceXYWH(int var0, int var1, int var2, int var3) { return null; }
	public static sun.java2d.pipe.Region getInstanceXYXY(int var0, int var1, int var2, int var3) { return null; }
	public sun.java2d.pipe.Region getIntersection(java.awt.Rectangle var0) { return null; }
	public sun.java2d.pipe.Region getIntersection(sun.java2d.pipe.Region var0) { return null; }
	public sun.java2d.pipe.Region getIntersectionXYWH(int var0, int var1, int var2, int var3) { return null; }
	public sun.java2d.pipe.Region getIntersectionXYXY(int var0, int var1, int var2, int var3) { return null; }
	public sun.java2d.pipe.RegionIterator getIterator() { return null; }
	public final int getLoX() { return 0; }
	public final int getLoY() { return 0; }
	public sun.java2d.pipe.SpanIterator getSpanIterator() { return null; }
	public sun.java2d.pipe.SpanIterator getSpanIterator(int[] var0) { return null; }
	public sun.java2d.pipe.Region getTranslatedRegion(int var0, int var1) { return null; }
	public sun.java2d.pipe.Region getUnion(sun.java2d.pipe.Region var0) { return null; }
	public final int getWidth() { return 0; }
	public boolean intersectsQuickCheck(sun.java2d.pipe.Region var0) { return false; }
	public boolean intersectsQuickCheckXYXY(int var0, int var1, int var2, int var3) { return false; }
	public boolean isEmpty() { return false; }
	public boolean isInsideQuickCheck(sun.java2d.pipe.Region var0) { return false; }
	public boolean isInsideXYWH(int var0, int var1, int var2, int var3) { return false; }
	public boolean isInsideXYXY(int var0, int var1, int var2, int var3) { return false; }
	public boolean isRectangular() { return false; }
	public void setOutputArea(java.awt.Rectangle var0) { }
	public void setOutputArea(int[] var0) { }
	public void setOutputAreaXYWH(int var0, int var1, int var2, int var3) { }
	public void setOutputAreaXYXY(int var0, int var1, int var2, int var3) { }
}

