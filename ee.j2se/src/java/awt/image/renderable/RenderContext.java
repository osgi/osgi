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

package java.awt.image.renderable;
public class RenderContext implements java.lang.Cloneable {
	public RenderContext(java.awt.geom.AffineTransform var0) { }
	public RenderContext(java.awt.geom.AffineTransform var0, java.awt.RenderingHints var1) { }
	public RenderContext(java.awt.geom.AffineTransform var0, java.awt.Shape var1) { }
	public RenderContext(java.awt.geom.AffineTransform var0, java.awt.Shape var1, java.awt.RenderingHints var2) { }
	public java.lang.Object clone() { return null; }
	public void concatenateTransform(java.awt.geom.AffineTransform var0) { }
	/** @deprecated */ public void concetenateTransform(java.awt.geom.AffineTransform var0) { }
	public java.awt.Shape getAreaOfInterest() { return null; }
	public java.awt.RenderingHints getRenderingHints() { return null; }
	public java.awt.geom.AffineTransform getTransform() { return null; }
	public void preConcatenateTransform(java.awt.geom.AffineTransform var0) { }
	/** @deprecated */ public void preConcetenateTransform(java.awt.geom.AffineTransform var0) { }
	public void setAreaOfInterest(java.awt.Shape var0) { }
	public void setRenderingHints(java.awt.RenderingHints var0) { }
	public void setTransform(java.awt.geom.AffineTransform var0) { }
}

