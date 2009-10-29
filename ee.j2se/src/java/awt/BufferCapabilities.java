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

package java.awt;
public class BufferCapabilities implements java.lang.Cloneable {
	public BufferCapabilities(java.awt.ImageCapabilities var0, java.awt.ImageCapabilities var1, java.awt.BufferCapabilities.FlipContents var2) { }
	public java.lang.Object clone() { return null; }
	public java.awt.ImageCapabilities getBackBufferCapabilities() { return null; }
	public java.awt.BufferCapabilities.FlipContents getFlipContents() { return null; }
	public java.awt.ImageCapabilities getFrontBufferCapabilities() { return null; }
	public boolean isFullScreenRequired() { return false; }
	public boolean isMultiBufferAvailable() { return false; }
	public boolean isPageFlipping() { return false; }
	public static final class FlipContents extends java.awt.AttributeValue {
		public final static java.awt.BufferCapabilities.FlipContents BACKGROUND; static { BACKGROUND = null; }
		public final static java.awt.BufferCapabilities.FlipContents COPIED; static { COPIED = null; }
		public final static java.awt.BufferCapabilities.FlipContents PRIOR; static { PRIOR = null; }
		public final static java.awt.BufferCapabilities.FlipContents UNDEFINED; static { UNDEFINED = null; }
		private FlipContents() { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
}

