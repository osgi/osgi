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

package java.awt.image.renderable;
public class ParameterBlock implements java.io.Serializable, java.lang.Cloneable {
	public ParameterBlock() { }
	public ParameterBlock(java.util.Vector var0) { }
	public ParameterBlock(java.util.Vector var0, java.util.Vector var1) { }
	public java.awt.image.renderable.ParameterBlock add(byte var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(char var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(double var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(float var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(int var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(long var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(java.lang.Object var0) { return null; }
	public java.awt.image.renderable.ParameterBlock add(short var0) { return null; }
	public java.awt.image.renderable.ParameterBlock addSource(java.lang.Object var0) { return null; }
	public java.lang.Object clone() { return null; }
	public byte getByteParameter(int var0) { return 0; }
	public char getCharParameter(int var0) { return '\0'; }
	public double getDoubleParameter(int var0) { return 0.0d; }
	public float getFloatParameter(int var0) { return 0.0f; }
	public int getIntParameter(int var0) { return 0; }
	public long getLongParameter(int var0) { return 0l; }
	public int getNumParameters() { return 0; }
	public int getNumSources() { return 0; }
	public java.lang.Object getObjectParameter(int var0) { return null; }
	public java.lang.Class[] getParamClasses() { return null; }
	public java.util.Vector getParameters() { return null; }
	public java.awt.image.renderable.RenderableImage getRenderableSource(int var0) { return null; }
	public java.awt.image.RenderedImage getRenderedSource(int var0) { return null; }
	public short getShortParameter(int var0) { return 0; }
	public java.lang.Object getSource(int var0) { return null; }
	public java.util.Vector getSources() { return null; }
	public void removeParameters() { }
	public void removeSources() { }
	public java.awt.image.renderable.ParameterBlock set(byte var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(char var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(double var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(float var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(int var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(long var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(java.lang.Object var0, int var1) { return null; }
	public java.awt.image.renderable.ParameterBlock set(short var0, int var1) { return null; }
	public void setParameters(java.util.Vector var0) { }
	public java.awt.image.renderable.ParameterBlock setSource(java.lang.Object var0, int var1) { return null; }
	public void setSources(java.util.Vector var0) { }
	public java.lang.Object shallowClone() { return null; }
	protected java.util.Vector parameters;
	protected java.util.Vector sources;
}

