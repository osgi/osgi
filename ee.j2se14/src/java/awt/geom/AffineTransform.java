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
public class AffineTransform implements java.io.Serializable, java.lang.Cloneable {
	public AffineTransform() { }
	public AffineTransform(double var0, double var1, double var2, double var3, double var4, double var5) { }
	public AffineTransform(float var0, float var1, float var2, float var3, float var4, float var5) { }
	public AffineTransform(java.awt.geom.AffineTransform var0) { }
	public AffineTransform(double[] var0) { }
	public AffineTransform(float[] var0) { }
	public java.lang.Object clone() { return null; }
	public void concatenate(java.awt.geom.AffineTransform var0) { }
	public java.awt.geom.AffineTransform createInverse() throws java.awt.geom.NoninvertibleTransformException { return null; }
	public java.awt.Shape createTransformedShape(java.awt.Shape var0) { return null; }
	public java.awt.geom.Point2D deltaTransform(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { return null; }
	public void deltaTransform(double[] var0, int var1, double[] var2, int var3, int var4) { }
	public double getDeterminant() { return 0.0d; }
	public void getMatrix(double[] var0) { }
	public static java.awt.geom.AffineTransform getRotateInstance(double var0) { return null; }
	public static java.awt.geom.AffineTransform getRotateInstance(double var0, double var1, double var2) { return null; }
	public static java.awt.geom.AffineTransform getScaleInstance(double var0, double var1) { return null; }
	public double getScaleX() { return 0.0d; }
	public double getScaleY() { return 0.0d; }
	public static java.awt.geom.AffineTransform getShearInstance(double var0, double var1) { return null; }
	public double getShearX() { return 0.0d; }
	public double getShearY() { return 0.0d; }
	public static java.awt.geom.AffineTransform getTranslateInstance(double var0, double var1) { return null; }
	public double getTranslateX() { return 0.0d; }
	public double getTranslateY() { return 0.0d; }
	public int getType() { return 0; }
	public int hashCode() { return 0; }
	public java.awt.geom.Point2D inverseTransform(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) throws java.awt.geom.NoninvertibleTransformException { return null; }
	public void inverseTransform(double[] var0, int var1, double[] var2, int var3, int var4) throws java.awt.geom.NoninvertibleTransformException { }
	public boolean isIdentity() { return false; }
	public void preConcatenate(java.awt.geom.AffineTransform var0) { }
	public void rotate(double var0) { }
	public void rotate(double var0, double var1, double var2) { }
	public void scale(double var0, double var1) { }
	public void setToIdentity() { }
	public void setToRotation(double var0) { }
	public void setToRotation(double var0, double var1, double var2) { }
	public void setToScale(double var0, double var1) { }
	public void setToShear(double var0, double var1) { }
	public void setToTranslation(double var0, double var1) { }
	public void setTransform(double var0, double var1, double var2, double var3, double var4, double var5) { }
	public void setTransform(java.awt.geom.AffineTransform var0) { }
	public void shear(double var0, double var1) { }
	public java.awt.geom.Point2D transform(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { return null; }
	public void transform(double[] var0, int var1, double[] var2, int var3, int var4) { }
	public void transform(double[] var0, int var1, float[] var2, int var3, int var4) { }
	public void transform(float[] var0, int var1, double[] var2, int var3, int var4) { }
	public void transform(float[] var0, int var1, float[] var2, int var3, int var4) { }
	public void transform(java.awt.geom.Point2D[] var0, int var1, java.awt.geom.Point2D[] var2, int var3, int var4) { }
	public void translate(double var0, double var1) { }
	public final static int TYPE_FLIP = 64;
	public final static int TYPE_GENERAL_ROTATION = 16;
	public final static int TYPE_GENERAL_SCALE = 4;
	public final static int TYPE_GENERAL_TRANSFORM = 32;
	public final static int TYPE_IDENTITY = 0;
	public final static int TYPE_MASK_ROTATION = 24;
	public final static int TYPE_MASK_SCALE = 6;
	public final static int TYPE_QUADRANT_ROTATION = 8;
	public final static int TYPE_TRANSLATION = 1;
	public final static int TYPE_UNIFORM_SCALE = 2;
}

