/*
 * $Revision$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2007). All Rights Reserved.
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

package java.util.zip;
public class ZipOutputStream extends java.util.zip.DeflaterOutputStream implements java.util.zip.ZipConstants {
	public ZipOutputStream(java.io.OutputStream var0) { super((java.io.OutputStream) null, (java.util.zip.Deflater) null, 0); }
	public void closeEntry() throws java.io.IOException { }
	public void putNextEntry(java.util.zip.ZipEntry var0) throws java.io.IOException { }
	public void setComment(java.lang.String var0) { }
	public void setLevel(int var0) { }
	public void setMethod(int var0) { }
	public final static int DEFLATED = 8;
	public final static int STORED = 0;
}

