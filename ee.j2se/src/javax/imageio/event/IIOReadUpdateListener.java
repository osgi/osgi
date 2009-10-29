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

package javax.imageio.event;
public abstract interface IIOReadUpdateListener extends java.util.EventListener {
	public abstract void imageUpdate(javax.imageio.ImageReader var0, java.awt.image.BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8);
	public abstract void passComplete(javax.imageio.ImageReader var0, java.awt.image.BufferedImage var1);
	public abstract void passStarted(javax.imageio.ImageReader var0, java.awt.image.BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9);
	public abstract void thumbnailPassComplete(javax.imageio.ImageReader var0, java.awt.image.BufferedImage var1);
	public abstract void thumbnailPassStarted(javax.imageio.ImageReader var0, java.awt.image.BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9);
	public abstract void thumbnailUpdate(javax.imageio.ImageReader var0, java.awt.image.BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8);
}

