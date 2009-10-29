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

package java.awt.color;
public abstract class ColorSpace implements java.io.Serializable {
	protected ColorSpace(int var0, int var1) { }
	public abstract float[] fromCIEXYZ(float[] var0);
	public abstract float[] fromRGB(float[] var0);
	public static java.awt.color.ColorSpace getInstance(int var0) { return null; }
	public float getMaxValue(int var0) { return 0.0f; }
	public float getMinValue(int var0) { return 0.0f; }
	public java.lang.String getName(int var0) { return null; }
	public int getNumComponents() { return 0; }
	public int getType() { return 0; }
	public boolean isCS_sRGB() { return false; }
	public abstract float[] toCIEXYZ(float[] var0);
	public abstract float[] toRGB(float[] var0);
	public final static int CS_CIEXYZ = 1001;
	public final static int CS_GRAY = 1003;
	public final static int CS_LINEAR_RGB = 1004;
	public final static int CS_PYCC = 1002;
	public final static int CS_sRGB = 1000;
	public final static int TYPE_2CLR = 12;
	public final static int TYPE_3CLR = 13;
	public final static int TYPE_4CLR = 14;
	public final static int TYPE_5CLR = 15;
	public final static int TYPE_6CLR = 16;
	public final static int TYPE_7CLR = 17;
	public final static int TYPE_8CLR = 18;
	public final static int TYPE_9CLR = 19;
	public final static int TYPE_ACLR = 20;
	public final static int TYPE_BCLR = 21;
	public final static int TYPE_CCLR = 22;
	public final static int TYPE_CMY = 11;
	public final static int TYPE_CMYK = 9;
	public final static int TYPE_DCLR = 23;
	public final static int TYPE_ECLR = 24;
	public final static int TYPE_FCLR = 25;
	public final static int TYPE_GRAY = 6;
	public final static int TYPE_HLS = 8;
	public final static int TYPE_HSV = 7;
	public final static int TYPE_Lab = 1;
	public final static int TYPE_Luv = 2;
	public final static int TYPE_RGB = 5;
	public final static int TYPE_XYZ = 0;
	public final static int TYPE_YCbCr = 3;
	public final static int TYPE_Yxy = 4;
}

