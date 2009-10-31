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

package javax.tools;
public class ForwardingFileObject<F extends javax.tools.FileObject> implements javax.tools.FileObject {
	protected final F fileObject; { fileObject = null; }
	protected ForwardingFileObject(F var0) { } 
	public boolean delete() { return false; }
	public java.lang.CharSequence getCharContent(boolean var0) throws java.io.IOException { return null; }
	public long getLastModified() { return 0l; }
	public java.lang.String getName() { return null; }
	public java.io.InputStream openInputStream() throws java.io.IOException { return null; }
	public java.io.OutputStream openOutputStream() throws java.io.IOException { return null; }
	public java.io.Reader openReader(boolean var0) throws java.io.IOException { return null; }
	public java.io.Writer openWriter() throws java.io.IOException { return null; }
	public java.net.URI toUri() { return null; }
}

