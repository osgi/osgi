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
public abstract interface Shape {
	public abstract boolean contains(double var0, double var1);
	public abstract boolean contains(double var0, double var1, double var2, double var3);
	public abstract boolean contains(java.awt.geom.Point2D var0);
	public abstract boolean contains(java.awt.geom.Rectangle2D var0);
	public abstract java.awt.Rectangle getBounds();
	public abstract java.awt.geom.Rectangle2D getBounds2D();
	public abstract java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0);
	public abstract java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1);
	public abstract boolean intersects(double var0, double var1, double var2, double var3);
	public abstract boolean intersects(java.awt.geom.Rectangle2D var0);
}

