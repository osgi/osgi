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

package java.awt;
public final class AlphaComposite implements java.awt.Composite {
	public final static int CLEAR = 1;
	public final static java.awt.AlphaComposite Clear; static { Clear = null; }
	public final static int DST = 9;
	public final static int DST_ATOP = 11;
	public final static int DST_IN = 6;
	public final static int DST_OUT = 8;
	public final static int DST_OVER = 4;
	public final static java.awt.AlphaComposite Dst; static { Dst = null; }
	public final static java.awt.AlphaComposite DstAtop; static { DstAtop = null; }
	public final static java.awt.AlphaComposite DstIn; static { DstIn = null; }
	public final static java.awt.AlphaComposite DstOut; static { DstOut = null; }
	public final static java.awt.AlphaComposite DstOver; static { DstOver = null; }
	public final static int SRC = 2;
	public final static int SRC_ATOP = 10;
	public final static int SRC_IN = 5;
	public final static int SRC_OUT = 7;
	public final static int SRC_OVER = 3;
	public final static java.awt.AlphaComposite Src; static { Src = null; }
	public final static java.awt.AlphaComposite SrcAtop; static { SrcAtop = null; }
	public final static java.awt.AlphaComposite SrcIn; static { SrcIn = null; }
	public final static java.awt.AlphaComposite SrcOut; static { SrcOut = null; }
	public final static java.awt.AlphaComposite SrcOver; static { SrcOver = null; }
	public final static int XOR = 12;
	public final static java.awt.AlphaComposite Xor; static { Xor = null; }
	public java.awt.CompositeContext createContext(java.awt.image.ColorModel var0, java.awt.image.ColorModel var1, java.awt.RenderingHints var2) { return null; }
	public java.awt.AlphaComposite derive(float var0) { return null; }
	public java.awt.AlphaComposite derive(int var0) { return null; }
	public float getAlpha() { return 0.0f; }
	public static java.awt.AlphaComposite getInstance(int var0) { return null; }
	public static java.awt.AlphaComposite getInstance(int var0, float var1) { return null; }
	public int getRule() { return 0; }
	private AlphaComposite() { } /* generated constructor to prevent compiler adding default public constructor */
}

