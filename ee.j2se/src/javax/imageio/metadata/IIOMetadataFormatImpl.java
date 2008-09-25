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

package javax.imageio.metadata;
public abstract class IIOMetadataFormatImpl implements javax.imageio.metadata.IIOMetadataFormat {
	public IIOMetadataFormatImpl(java.lang.String var0, int var1) { }
	public IIOMetadataFormatImpl(java.lang.String var0, int var1, int var2) { }
	protected void addAttribute(java.lang.String var0, java.lang.String var1, int var2, boolean var3, int var4, int var5) { }
	protected void addAttribute(java.lang.String var0, java.lang.String var1, int var2, boolean var3, java.lang.String var4) { }
	protected void addAttribute(java.lang.String var0, java.lang.String var1, int var2, boolean var3, java.lang.String var4, java.lang.String var5, java.lang.String var6, boolean var7, boolean var8) { }
	protected void addAttribute(java.lang.String var0, java.lang.String var1, int var2, boolean var3, java.lang.String var4, java.util.List var5) { }
	protected void addBooleanAttribute(java.lang.String var0, java.lang.String var1, boolean var2, boolean var3) { }
	protected void addChildElement(java.lang.String var0, java.lang.String var1) { }
	protected void addElement(java.lang.String var0, java.lang.String var1, int var2) { }
	protected void addElement(java.lang.String var0, java.lang.String var1, int var2, int var3) { }
	protected void addObjectValue(java.lang.String var0, java.lang.Class var1, int var2, int var3) { }
	protected void addObjectValue(java.lang.String var0, java.lang.Class var1, java.lang.Object var2, java.lang.Comparable var3, java.lang.Comparable var4, boolean var5, boolean var6) { }
	protected void addObjectValue(java.lang.String var0, java.lang.Class var1, boolean var2, java.lang.Object var3) { }
	protected void addObjectValue(java.lang.String var0, java.lang.Class var1, boolean var2, java.lang.Object var3, java.util.List var4) { }
	public int getAttributeDataType(java.lang.String var0, java.lang.String var1) { return 0; }
	public java.lang.String getAttributeDefaultValue(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String getAttributeDescription(java.lang.String var0, java.lang.String var1, java.util.Locale var2) { return null; }
	public java.lang.String[] getAttributeEnumerations(java.lang.String var0, java.lang.String var1) { return null; }
	public int getAttributeListMaxLength(java.lang.String var0, java.lang.String var1) { return 0; }
	public int getAttributeListMinLength(java.lang.String var0, java.lang.String var1) { return 0; }
	public java.lang.String getAttributeMaxValue(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String getAttributeMinValue(java.lang.String var0, java.lang.String var1) { return null; }
	public java.lang.String[] getAttributeNames(java.lang.String var0) { return null; }
	public int getAttributeValueType(java.lang.String var0, java.lang.String var1) { return 0; }
	public java.lang.String[] getChildNames(java.lang.String var0) { return null; }
	public int getChildPolicy(java.lang.String var0) { return 0; }
	public java.lang.String getElementDescription(java.lang.String var0, java.util.Locale var1) { return null; }
	public int getElementMaxChildren(java.lang.String var0) { return 0; }
	public int getElementMinChildren(java.lang.String var0) { return 0; }
	public int getObjectArrayMaxLength(java.lang.String var0) { return 0; }
	public int getObjectArrayMinLength(java.lang.String var0) { return 0; }
	public java.lang.Class getObjectClass(java.lang.String var0) { return null; }
	public java.lang.Object getObjectDefaultValue(java.lang.String var0) { return null; }
	public java.lang.Object[] getObjectEnumerations(java.lang.String var0) { return null; }
	public java.lang.Comparable getObjectMaxValue(java.lang.String var0) { return null; }
	public java.lang.Comparable getObjectMinValue(java.lang.String var0) { return null; }
	public int getObjectValueType(java.lang.String var0) { return 0; }
	protected java.lang.String getResourceBaseName() { return null; }
	public java.lang.String getRootName() { return null; }
	public static javax.imageio.metadata.IIOMetadataFormat getStandardFormatInstance() { return null; }
	public boolean isAttributeRequired(java.lang.String var0, java.lang.String var1) { return false; }
	protected void removeAttribute(java.lang.String var0, java.lang.String var1) { }
	protected void removeElement(java.lang.String var0) { }
	protected void removeObjectValue(java.lang.String var0) { }
	protected void setResourceBaseName(java.lang.String var0) { }
	public final static java.lang.String standardMetadataFormatName = "javax_imageio_1.0";
}

