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

package java.awt.image.renderable;
public interface RenderableImage {
	public final static java.lang.String HINTS_OBSERVED = "HINTS_OBSERVED";
	java.awt.image.RenderedImage createDefaultRendering();
	java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext var0);
	java.awt.image.RenderedImage createScaledRendering(int var0, int var1, java.awt.RenderingHints var2);
	float getHeight();
	float getMinX();
	float getMinY();
	java.lang.Object getProperty(java.lang.String var0);
	java.lang.String[] getPropertyNames();
	java.util.Vector<java.awt.image.renderable.RenderableImage> getSources();
	float getWidth();
	boolean isDynamic();
}

