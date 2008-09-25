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

package java.awt.datatransfer;
public class DataFlavor implements java.io.Externalizable, java.lang.Cloneable {
	public DataFlavor() { }
	public DataFlavor(java.lang.Class var0, java.lang.String var1) { }
	public DataFlavor(java.lang.String var0) throws java.lang.ClassNotFoundException { }
	public DataFlavor(java.lang.String var0, java.lang.String var1) { }
	public DataFlavor(java.lang.String var0, java.lang.String var1, java.lang.ClassLoader var2) throws java.lang.ClassNotFoundException { }
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	public boolean equals(java.awt.datatransfer.DataFlavor var0) { return false; }
	/** @deprecated */ public boolean equals(java.lang.String var0) { return false; }
	public final java.lang.Class getDefaultRepresentationClass() { return null; }
	public final java.lang.String getDefaultRepresentationClassAsString() { return null; }
	public java.lang.String getHumanPresentableName() { return null; }
	public java.lang.String getMimeType() { return null; }
	public java.lang.String getParameter(java.lang.String var0) { return null; }
	public java.lang.String getPrimaryType() { return null; }
	public java.io.Reader getReaderForText(java.awt.datatransfer.Transferable var0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { return null; }
	public java.lang.Class getRepresentationClass() { return null; }
	public java.lang.String getSubType() { return null; }
	public final static java.awt.datatransfer.DataFlavor getTextPlainUnicodeFlavor() { return null; }
	public int hashCode() { return 0; }
	public boolean isFlavorJavaFileListType() { return false; }
	public boolean isFlavorRemoteObjectType() { return false; }
	public boolean isFlavorSerializedObjectType() { return false; }
	public boolean isFlavorTextType() { return false; }
	public final boolean isMimeTypeEqual(java.awt.datatransfer.DataFlavor var0) { return false; }
	public boolean isMimeTypeEqual(java.lang.String var0) { return false; }
	public boolean isMimeTypeSerializedObject() { return false; }
	public boolean isRepresentationClassByteBuffer() { return false; }
	public boolean isRepresentationClassCharBuffer() { return false; }
	public boolean isRepresentationClassInputStream() { return false; }
	public boolean isRepresentationClassReader() { return false; }
	public boolean isRepresentationClassRemote() { return false; }
	public boolean isRepresentationClassSerializable() { return false; }
	public boolean match(java.awt.datatransfer.DataFlavor var0) { return false; }
	/** @deprecated */ protected java.lang.String normalizeMimeType(java.lang.String var0) { return null; }
	/** @deprecated */ protected java.lang.String normalizeMimeTypeParameter(java.lang.String var0, java.lang.String var1) { return null; }
	public void readExternal(java.io.ObjectInput var0) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public final static java.awt.datatransfer.DataFlavor selectBestTextFlavor(java.awt.datatransfer.DataFlavor[] var0) { return null; }
	public void setHumanPresentableName(java.lang.String var0) { }
	protected final static java.lang.Class tryToLoadClass(java.lang.String var0, java.lang.ClassLoader var1) throws java.lang.ClassNotFoundException { return null; }
	public void writeExternal(java.io.ObjectOutput var0) throws java.io.IOException { }
	public final static java.awt.datatransfer.DataFlavor imageFlavor; static { imageFlavor = null; }
	public final static java.awt.datatransfer.DataFlavor javaFileListFlavor; static { javaFileListFlavor = null; }
	public final static java.lang.String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
	public final static java.lang.String javaRemoteObjectMimeType = "application/x-java-remote-object";
	public final static java.lang.String javaSerializedObjectMimeType = "application/x-java-serialized-object";
	/** @deprecated */ public final static java.awt.datatransfer.DataFlavor plainTextFlavor; static { plainTextFlavor = null; }
	public final static java.awt.datatransfer.DataFlavor stringFlavor; static { stringFlavor = null; }
}

