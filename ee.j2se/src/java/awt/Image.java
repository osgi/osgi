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
public abstract class Image {
	public final static int SCALE_AREA_AVERAGING = 16;
	public final static int SCALE_DEFAULT = 1;
	public final static int SCALE_FAST = 2;
	public final static int SCALE_REPLICATE = 8;
	public final static int SCALE_SMOOTH = 4;
	public final static java.lang.Object UndefinedProperty; static { UndefinedProperty = null; }
	protected float accelerationPriority;
	public Image() { } 
	public abstract void flush();
	public float getAccelerationPriority() { return 0.0f; }
	public java.awt.ImageCapabilities getCapabilities(java.awt.GraphicsConfiguration var0) { return null; }
	public abstract java.awt.Graphics getGraphics();
	public abstract int getHeight(java.awt.image.ImageObserver var0);
	public abstract java.lang.Object getProperty(java.lang.String var0, java.awt.image.ImageObserver var1);
	public java.awt.Image getScaledInstance(int var0, int var1, int var2) { return null; }
	public abstract java.awt.image.ImageProducer getSource();
	public abstract int getWidth(java.awt.image.ImageObserver var0);
	public void setAccelerationPriority(float var0) { }
}

