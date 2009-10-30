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

package java.awt.font;
public abstract class GraphicAttribute {
	public final static int BOTTOM_ALIGNMENT = -2;
	public final static int CENTER_BASELINE = 1;
	public final static int HANGING_BASELINE = 2;
	public final static int ROMAN_BASELINE = 0;
	public final static int TOP_ALIGNMENT = -1;
	protected GraphicAttribute(int var0) { } 
	public abstract void draw(java.awt.Graphics2D var0, float var1, float var2);
	public abstract float getAdvance();
	public final int getAlignment() { return 0; }
	public abstract float getAscent();
	public java.awt.geom.Rectangle2D getBounds() { return null; }
	public abstract float getDescent();
	public java.awt.font.GlyphJustificationInfo getJustificationInfo() { return null; }
}

