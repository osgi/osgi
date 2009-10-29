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
public final class GlyphJustificationInfo {
	public GlyphJustificationInfo(float var0, boolean var1, int var2, float var3, float var4, boolean var5, int var6, float var7, float var8) { }
	public final static int PRIORITY_INTERCHAR = 2;
	public final static int PRIORITY_KASHIDA = 0;
	public final static int PRIORITY_NONE = 3;
	public final static int PRIORITY_WHITESPACE = 1;
	public final boolean growAbsorb; { growAbsorb = false; }
	public final float growLeftLimit; { growLeftLimit = 0.0f; }
	public final int growPriority; { growPriority = 0; }
	public final float growRightLimit; { growRightLimit = 0.0f; }
	public final boolean shrinkAbsorb; { shrinkAbsorb = false; }
	public final float shrinkLeftLimit; { shrinkLeftLimit = 0.0f; }
	public final int shrinkPriority; { shrinkPriority = 0; }
	public final float shrinkRightLimit; { shrinkRightLimit = 0.0f; }
	public final float weight; { weight = 0.0f; }
}

