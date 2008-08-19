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

package javax.imageio;
public abstract class IIOParam {
	protected IIOParam() { }
	public boolean activateController() { return false; }
	public javax.imageio.IIOParamController getController() { return null; }
	public javax.imageio.IIOParamController getDefaultController() { return null; }
	public java.awt.Point getDestinationOffset() { return null; }
	public javax.imageio.ImageTypeSpecifier getDestinationType() { return null; }
	public int[] getSourceBands() { return null; }
	public java.awt.Rectangle getSourceRegion() { return null; }
	public int getSourceXSubsampling() { return 0; }
	public int getSourceYSubsampling() { return 0; }
	public int getSubsamplingXOffset() { return 0; }
	public int getSubsamplingYOffset() { return 0; }
	public boolean hasController() { return false; }
	public void setController(javax.imageio.IIOParamController var0) { }
	public void setDestinationOffset(java.awt.Point var0) { }
	public void setDestinationType(javax.imageio.ImageTypeSpecifier var0) { }
	public void setSourceBands(int[] var0) { }
	public void setSourceRegion(java.awt.Rectangle var0) { }
	public void setSourceSubsampling(int var0, int var1, int var2, int var3) { }
	protected javax.imageio.IIOParamController controller;
	protected javax.imageio.IIOParamController defaultController;
	protected java.awt.Point destinationOffset;
	protected javax.imageio.ImageTypeSpecifier destinationType;
	protected int[] sourceBands;
	protected java.awt.Rectangle sourceRegion;
	protected int sourceXSubsampling;
	protected int sourceYSubsampling;
	protected int subsamplingXOffset;
	protected int subsamplingYOffset;
}

