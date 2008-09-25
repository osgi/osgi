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

package java.util.zip;
public class DeflaterOutputStream extends java.io.FilterOutputStream {
	public DeflaterOutputStream(java.io.OutputStream var0) { super((java.io.OutputStream) null); }
	public DeflaterOutputStream(java.io.OutputStream var0, java.util.zip.Deflater var1) { super((java.io.OutputStream) null); }
	public DeflaterOutputStream(java.io.OutputStream var0, java.util.zip.Deflater var1, int var2) { super((java.io.OutputStream) null); }
	protected void deflate() throws java.io.IOException { }
	public void finish() throws java.io.IOException { }
	protected byte[] buf;
	protected java.util.zip.Deflater def;
}

