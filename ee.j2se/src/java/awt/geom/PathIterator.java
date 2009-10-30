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
public interface PathIterator {
	public final static int SEG_CLOSE = 4;
	public final static int SEG_CUBICTO = 3;
	public final static int SEG_LINETO = 1;
	public final static int SEG_MOVETO = 0;
	public final static int SEG_QUADTO = 2;
	public final static int WIND_EVEN_ODD = 0;
	public final static int WIND_NON_ZERO = 1;
	int currentSegment(double[] var0);
	int currentSegment(float[] var0);
	int getWindingRule();
	boolean isDone();
	void next();
}

