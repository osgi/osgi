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

package javax.print.attribute;
public abstract class Size2DSyntax implements java.io.Serializable, java.lang.Cloneable {
	public final static int INCH = 25400;
	public final static int MM = 1000;
	protected Size2DSyntax(float var0, float var1, int var2) { } 
	protected Size2DSyntax(int var0, int var1, int var2) { } 
	public float[] getSize(int var0) { return null; }
	public float getX(int var0) { return 0.0f; }
	protected int getXMicrometers() { return 0; }
	public float getY(int var0) { return 0.0f; }
	protected int getYMicrometers() { return 0; }
	public java.lang.String toString(int var0, java.lang.String var1) { return null; }
}

