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

package java.awt.geom;
public class FlatteningPathIterator implements java.awt.geom.PathIterator {
	public FlatteningPathIterator(java.awt.geom.PathIterator var0, double var1) { } 
	public FlatteningPathIterator(java.awt.geom.PathIterator var0, double var1, int var2) { } 
	public int currentSegment(double[] var0) { return 0; }
	public int currentSegment(float[] var0) { return 0; }
	public double getFlatness() { return 0.0d; }
	public int getRecursionLimit() { return 0; }
	public int getWindingRule() { return 0; }
	public boolean isDone() { return false; }
	public void next() { }
}

