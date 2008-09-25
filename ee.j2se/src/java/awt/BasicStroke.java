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
public class BasicStroke implements java.awt.Stroke {
	public BasicStroke() { }
	public BasicStroke(float var0) { }
	public BasicStroke(float var0, int var1, int var2) { }
	public BasicStroke(float var0, int var1, int var2, float var3) { }
	public BasicStroke(float var0, int var1, int var2, float var3, float[] var4, float var5) { }
	public java.awt.Shape createStrokedShape(java.awt.Shape var0) { return null; }
	public float[] getDashArray() { return null; }
	public float getDashPhase() { return 0.0f; }
	public int getEndCap() { return 0; }
	public int getLineJoin() { return 0; }
	public float getLineWidth() { return 0.0f; }
	public float getMiterLimit() { return 0.0f; }
	public int hashCode() { return 0; }
	public final static int CAP_BUTT = 0;
	public final static int CAP_ROUND = 1;
	public final static int CAP_SQUARE = 2;
	public final static int JOIN_BEVEL = 2;
	public final static int JOIN_MITER = 0;
	public final static int JOIN_ROUND = 1;
}

