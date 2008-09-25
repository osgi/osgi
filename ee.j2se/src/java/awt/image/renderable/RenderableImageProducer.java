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
public class RenderableImageProducer implements java.awt.image.ImageProducer, java.lang.Runnable {
	public RenderableImageProducer(java.awt.image.renderable.RenderableImage var0, java.awt.image.renderable.RenderContext var1) { }
	public void addConsumer(java.awt.image.ImageConsumer var0) { }
	public boolean isConsumer(java.awt.image.ImageConsumer var0) { return false; }
	public void removeConsumer(java.awt.image.ImageConsumer var0) { }
	public void requestTopDownLeftRightResend(java.awt.image.ImageConsumer var0) { }
	public void run() { }
	public void setRenderContext(java.awt.image.renderable.RenderContext var0) { }
	public void startProduction(java.awt.image.ImageConsumer var0) { }
}

