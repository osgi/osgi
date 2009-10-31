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

package javax.print.attribute.standard;
public final class MediaPrintableArea implements javax.print.attribute.DocAttribute, javax.print.attribute.PrintJobAttribute, javax.print.attribute.PrintRequestAttribute {
	public final static int INCH = 25400;
	public final static int MM = 1000;
	public MediaPrintableArea(float var0, float var1, float var2, float var3, int var4) { } 
	public MediaPrintableArea(int var0, int var1, int var2, int var3, int var4) { } 
	public final java.lang.Class<? extends javax.print.attribute.Attribute> getCategory() { return null; }
	public float getHeight(int var0) { return 0.0f; }
	public final java.lang.String getName() { return null; }
	public float[] getPrintableArea(int var0) { return null; }
	public float getWidth(int var0) { return 0.0f; }
	public float getX(int var0) { return 0.0f; }
	public float getY(int var0) { return 0.0f; }
	public java.lang.String toString(int var0, java.lang.String var1) { return null; }
}

