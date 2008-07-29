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

package org.w3c.dom.css;
public abstract interface CSSPrimitiveValue extends org.w3c.dom.css.CSSValue {
	public abstract org.w3c.dom.css.Counter getCounterValue();
	public abstract float getFloatValue(short var0);
	public abstract short getPrimitiveType();
	public abstract org.w3c.dom.css.RGBColor getRGBColorValue();
	public abstract org.w3c.dom.css.Rect getRectValue();
	public abstract java.lang.String getStringValue();
	public abstract void setFloatValue(short var0, float var1);
	public abstract void setStringValue(short var0, java.lang.String var1);
	public final static short CSS_ATTR = 22;
	public final static short CSS_CM = 6;
	public final static short CSS_COUNTER = 23;
	public final static short CSS_DEG = 11;
	public final static short CSS_DIMENSION = 18;
	public final static short CSS_EMS = 3;
	public final static short CSS_EXS = 4;
	public final static short CSS_GRAD = 13;
	public final static short CSS_HZ = 16;
	public final static short CSS_IDENT = 21;
	public final static short CSS_IN = 8;
	public final static short CSS_KHZ = 17;
	public final static short CSS_MM = 7;
	public final static short CSS_MS = 14;
	public final static short CSS_NUMBER = 1;
	public final static short CSS_PC = 10;
	public final static short CSS_PERCENTAGE = 2;
	public final static short CSS_PT = 9;
	public final static short CSS_PX = 5;
	public final static short CSS_RAD = 12;
	public final static short CSS_RECT = 24;
	public final static short CSS_RGBCOLOR = 25;
	public final static short CSS_S = 15;
	public final static short CSS_STRING = 19;
	public final static short CSS_UNKNOWN = 0;
	public final static short CSS_URI = 20;
}

