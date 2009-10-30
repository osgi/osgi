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

package java.awt.font;
public final class NumericShaper implements java.io.Serializable {
	public final static int ALL_RANGES = 524287;
	public final static int ARABIC = 2;
	public final static int BENGALI = 16;
	public final static int DEVANAGARI = 8;
	public final static int EASTERN_ARABIC = 4;
	public final static int ETHIOPIC = 65536;
	public final static int EUROPEAN = 1;
	public final static int GUJARATI = 64;
	public final static int GURMUKHI = 32;
	public final static int KANNADA = 1024;
	public final static int KHMER = 131072;
	public final static int LAO = 8192;
	public final static int MALAYALAM = 2048;
	public final static int MONGOLIAN = 262144;
	public final static int MYANMAR = 32768;
	public final static int ORIYA = 128;
	public final static int TAMIL = 256;
	public final static int TELUGU = 512;
	public final static int THAI = 4096;
	public final static int TIBETAN = 16384;
	public static java.awt.font.NumericShaper getContextualShaper(int var0) { return null; }
	public static java.awt.font.NumericShaper getContextualShaper(int var0, int var1) { return null; }
	public int getRanges() { return 0; }
	public static java.awt.font.NumericShaper getShaper(int var0) { return null; }
	public int hashCode() { return 0; }
	public boolean isContextual() { return false; }
	public void shape(char[] var0, int var1, int var2) { }
	public void shape(char[] var0, int var1, int var2, int var3) { }
	private NumericShaper() { } /* generated constructor to prevent compiler adding default public constructor */
}

