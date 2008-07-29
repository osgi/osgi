/*
 * $Date$
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

package java.awt.geom;
public abstract class Dimension2D implements java.lang.Cloneable {
	protected Dimension2D() { }
	public java.lang.Object clone() { return null; }
	public abstract double getHeight();
	public abstract double getWidth();
	public abstract void setSize(double var0, double var1);
	public void setSize(java.awt.geom.Dimension2D var0) { }
}

