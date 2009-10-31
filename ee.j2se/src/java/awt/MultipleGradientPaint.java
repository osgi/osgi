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
public abstract class MultipleGradientPaint implements java.awt.Paint {
	public enum ColorSpaceType {
		LINEAR_RGB,
		SRGB;
	}
	public enum CycleMethod {
		NO_CYCLE,
		REFLECT,
		REPEAT;
	}
	public final java.awt.MultipleGradientPaint.ColorSpaceType getColorSpace() { return null; }
	public final java.awt.Color[] getColors() { return null; }
	public final java.awt.MultipleGradientPaint.CycleMethod getCycleMethod() { return null; }
	public final float[] getFractions() { return null; }
	public final java.awt.geom.AffineTransform getTransform() { return null; }
	public final int getTransparency() { return 0; }
	MultipleGradientPaint() { } /* generated constructor to prevent compiler adding default public constructor */
}

