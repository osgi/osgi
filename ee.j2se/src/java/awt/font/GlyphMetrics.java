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

package java.awt.font;
public final class GlyphMetrics {
	public GlyphMetrics(float var0, java.awt.geom.Rectangle2D var1, byte var2) { }
	public GlyphMetrics(boolean var0, float var1, float var2, java.awt.geom.Rectangle2D var3, byte var4) { }
	public float getAdvance() { return 0.0f; }
	public float getAdvanceX() { return 0.0f; }
	public float getAdvanceY() { return 0.0f; }
	public java.awt.geom.Rectangle2D getBounds2D() { return null; }
	public float getLSB() { return 0.0f; }
	public float getRSB() { return 0.0f; }
	public int getType() { return 0; }
	public boolean isCombining() { return false; }
	public boolean isComponent() { return false; }
	public boolean isLigature() { return false; }
	public boolean isStandard() { return false; }
	public boolean isWhitespace() { return false; }
	public final static byte COMBINING = 2;
	public final static byte COMPONENT = 3;
	public final static byte LIGATURE = 1;
	public final static byte STANDARD = 0;
	public final static byte WHITESPACE = 4;
}

