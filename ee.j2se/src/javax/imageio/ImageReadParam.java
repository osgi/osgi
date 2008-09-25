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

package javax.imageio;
public class ImageReadParam extends javax.imageio.IIOParam {
	public ImageReadParam() { }
	public boolean canSetSourceRenderSize() { return false; }
	public java.awt.image.BufferedImage getDestination() { return null; }
	public int[] getDestinationBands() { return null; }
	public int getSourceMaxProgressivePass() { return 0; }
	public int getSourceMinProgressivePass() { return 0; }
	public int getSourceNumProgressivePasses() { return 0; }
	public java.awt.Dimension getSourceRenderSize() { return null; }
	public void setDestination(java.awt.image.BufferedImage var0) { }
	public void setDestinationBands(int[] var0) { }
	public void setSourceProgressivePasses(int var0, int var1) { }
	public void setSourceRenderSize(java.awt.Dimension var0) { }
	protected boolean canSetSourceRenderSize;
	protected java.awt.image.BufferedImage destination;
	protected int[] destinationBands;
	protected int minProgressivePass;
	protected int numProgressivePasses;
	protected java.awt.Dimension sourceRenderSize;
}

