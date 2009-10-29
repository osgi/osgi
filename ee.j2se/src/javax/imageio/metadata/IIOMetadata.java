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

package javax.imageio.metadata;
public abstract class IIOMetadata {
	protected IIOMetadata() { }
	protected IIOMetadata(boolean var0, java.lang.String var1, java.lang.String var2, java.lang.String[] var3, java.lang.String[] var4) { }
	public boolean activateController() { return false; }
	public abstract org.w3c.dom.Node getAsTree(java.lang.String var0);
	public javax.imageio.metadata.IIOMetadataController getController() { return null; }
	public javax.imageio.metadata.IIOMetadataController getDefaultController() { return null; }
	public java.lang.String[] getExtraMetadataFormatNames() { return null; }
	public javax.imageio.metadata.IIOMetadataFormat getMetadataFormat(java.lang.String var0) { return null; }
	public java.lang.String[] getMetadataFormatNames() { return null; }
	public java.lang.String getNativeMetadataFormatName() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardChromaNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardCompressionNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardDataNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardDimensionNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardDocumentNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardTextNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardTileNode() { return null; }
	protected javax.imageio.metadata.IIOMetadataNode getStandardTransparencyNode() { return null; }
	protected final javax.imageio.metadata.IIOMetadataNode getStandardTree() { return null; }
	public boolean hasController() { return false; }
	public abstract boolean isReadOnly();
	public boolean isStandardMetadataFormatSupported() { return false; }
	public abstract void mergeTree(java.lang.String var0, org.w3c.dom.Node var1) throws javax.imageio.metadata.IIOInvalidTreeException;
	public abstract void reset();
	public void setController(javax.imageio.metadata.IIOMetadataController var0) { }
	public void setFromTree(java.lang.String var0, org.w3c.dom.Node var1) throws javax.imageio.metadata.IIOInvalidTreeException { }
	protected javax.imageio.metadata.IIOMetadataController controller;
	protected javax.imageio.metadata.IIOMetadataController defaultController;
	protected java.lang.String[] extraMetadataFormatClassNames;
	protected java.lang.String[] extraMetadataFormatNames;
	protected java.lang.String nativeMetadataFormatClassName;
	protected java.lang.String nativeMetadataFormatName;
	protected boolean standardFormatSupported;
}

