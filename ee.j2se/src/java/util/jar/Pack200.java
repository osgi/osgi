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

package java.util.jar;
public abstract class Pack200 {
	public interface Packer {
		public final static java.lang.String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
		public final static java.lang.String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
		public final static java.lang.String DEFLATE_HINT = "pack.deflate.hint";
		public final static java.lang.String EFFORT = "pack.effort";
		public final static java.lang.String ERROR = "error";
		public final static java.lang.String FALSE = "false";
		public final static java.lang.String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
		public final static java.lang.String KEEP = "keep";
		public final static java.lang.String KEEP_FILE_ORDER = "pack.keep.file.order";
		public final static java.lang.String LATEST = "latest";
		public final static java.lang.String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
		public final static java.lang.String MODIFICATION_TIME = "pack.modification.time";
		public final static java.lang.String PASS = "pass";
		public final static java.lang.String PASS_FILE_PFX = "pack.pass.file.";
		public final static java.lang.String PROGRESS = "pack.progress";
		public final static java.lang.String SEGMENT_LIMIT = "pack.segment.limit";
		public final static java.lang.String STRIP = "strip";
		public final static java.lang.String TRUE = "true";
		public final static java.lang.String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";
		void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
		void pack(java.util.jar.JarFile var0, java.io.OutputStream var1) throws java.io.IOException;
		void pack(java.util.jar.JarInputStream var0, java.io.OutputStream var1) throws java.io.IOException;
		java.util.SortedMap<java.lang.String,java.lang.String> properties();
		void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
	}
	public interface Unpacker {
		public final static java.lang.String DEFLATE_HINT = "unpack.deflate.hint";
		public final static java.lang.String FALSE = "false";
		public final static java.lang.String KEEP = "keep";
		public final static java.lang.String PROGRESS = "unpack.progress";
		public final static java.lang.String TRUE = "true";
		void addPropertyChangeListener(java.beans.PropertyChangeListener var0);
		java.util.SortedMap<java.lang.String,java.lang.String> properties();
		void removePropertyChangeListener(java.beans.PropertyChangeListener var0);
		void unpack(java.io.File var0, java.util.jar.JarOutputStream var1) throws java.io.IOException;
		void unpack(java.io.InputStream var0, java.util.jar.JarOutputStream var1) throws java.io.IOException;
	}
	public static java.util.jar.Pack200.Packer newPacker() { return null; }
	public static java.util.jar.Pack200.Unpacker newUnpacker() { return null; }
	private Pack200() { } /* generated constructor to prevent compiler adding default public constructor */
}

