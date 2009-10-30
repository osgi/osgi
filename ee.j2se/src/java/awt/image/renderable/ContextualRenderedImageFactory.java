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
public interface ContextualRenderedImageFactory extends java.awt.image.renderable.RenderedImageFactory {
	java.awt.image.RenderedImage create(java.awt.image.renderable.RenderContext var0, java.awt.image.renderable.ParameterBlock var1);
	java.awt.geom.Rectangle2D getBounds2D(java.awt.image.renderable.ParameterBlock var0);
	java.lang.Object getProperty(java.awt.image.renderable.ParameterBlock var0, java.lang.String var1);
	java.lang.String[] getPropertyNames();
	boolean isDynamic();
	java.awt.image.renderable.RenderContext mapRenderContext(int var0, java.awt.image.renderable.RenderContext var1, java.awt.image.renderable.ParameterBlock var2, java.awt.image.renderable.RenderableImage var3);
}

