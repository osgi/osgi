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
public interface Shape {
	boolean contains(double var0, double var1);
	boolean contains(double var0, double var1, double var2, double var3);
	boolean contains(java.awt.geom.Point2D var0);
	boolean contains(java.awt.geom.Rectangle2D var0);
	java.awt.Rectangle getBounds();
	java.awt.geom.Rectangle2D getBounds2D();
	java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0);
	java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform var0, double var1);
	boolean intersects(double var0, double var1, double var2, double var3);
	boolean intersects(java.awt.geom.Rectangle2D var0);
}

