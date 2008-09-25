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

package java.awt.image.renderable;
public abstract interface RenderableImage {
	public abstract java.awt.image.RenderedImage createDefaultRendering();
	public abstract java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext var0);
	public abstract java.awt.image.RenderedImage createScaledRendering(int var0, int var1, java.awt.RenderingHints var2);
	public abstract float getHeight();
	public abstract float getMinX();
	public abstract float getMinY();
	public abstract java.lang.Object getProperty(java.lang.String var0);
	public abstract java.lang.String[] getPropertyNames();
	public abstract java.util.Vector getSources();
	public abstract float getWidth();
	public abstract boolean isDynamic();
	public final static java.lang.String HINTS_OBSERVED = "HINTS_OBSERVED";
}

