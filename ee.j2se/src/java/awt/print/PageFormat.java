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

package java.awt.print;
public class PageFormat implements java.lang.Cloneable {
	public final static int LANDSCAPE = 0;
	public final static int PORTRAIT = 1;
	public final static int REVERSE_LANDSCAPE = 2;
	public PageFormat() { } 
	public java.lang.Object clone() { return null; }
	public double getHeight() { return 0.0d; }
	public double getImageableHeight() { return 0.0d; }
	public double getImageableWidth() { return 0.0d; }
	public double getImageableX() { return 0.0d; }
	public double getImageableY() { return 0.0d; }
	public double[] getMatrix() { return null; }
	public int getOrientation() { return 0; }
	public java.awt.print.Paper getPaper() { return null; }
	public double getWidth() { return 0.0d; }
	public void setOrientation(int var0) { }
	public void setPaper(java.awt.print.Paper var0) { }
}

