/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.lang;
public final class ProcessBuilder {
	public static abstract class Redirect {
		public enum Type {
			APPEND,
			INHERIT,
			PIPE,
			READ,
			WRITE;
		}
		public final static java.lang.ProcessBuilder.Redirect INHERIT; static { INHERIT = null; }
		public final static java.lang.ProcessBuilder.Redirect PIPE; static { PIPE = null; }
		public static java.lang.ProcessBuilder.Redirect appendTo(java.io.File var0) { return null; }
		public java.io.File file() { return null; }
		public static java.lang.ProcessBuilder.Redirect from(java.io.File var0) { return null; }
		public static java.lang.ProcessBuilder.Redirect to(java.io.File var0) { return null; }
		public abstract java.lang.ProcessBuilder.Redirect.Type type();
		private Redirect() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public ProcessBuilder(java.util.List<java.lang.String> var0) { } 
	public ProcessBuilder(java.lang.String... var0) { } 
	public java.util.List<java.lang.String> command() { return null; }
	public java.lang.ProcessBuilder command(java.util.List<java.lang.String> var0) { return null; }
	public java.lang.ProcessBuilder command(java.lang.String... var0) { return null; }
	public java.io.File directory() { return null; }
	public java.lang.ProcessBuilder directory(java.io.File var0) { return null; }
	public java.util.Map<java.lang.String,java.lang.String> environment() { return null; }
	public java.lang.ProcessBuilder inheritIO() { return null; }
	public java.lang.ProcessBuilder.Redirect redirectError() { return null; }
	public java.lang.ProcessBuilder redirectError(java.io.File var0) { return null; }
	public java.lang.ProcessBuilder redirectError(java.lang.ProcessBuilder.Redirect var0) { return null; }
	public boolean redirectErrorStream() { return false; }
	public java.lang.ProcessBuilder redirectErrorStream(boolean var0) { return null; }
	public java.lang.ProcessBuilder.Redirect redirectInput() { return null; }
	public java.lang.ProcessBuilder redirectInput(java.io.File var0) { return null; }
	public java.lang.ProcessBuilder redirectInput(java.lang.ProcessBuilder.Redirect var0) { return null; }
	public java.lang.ProcessBuilder.Redirect redirectOutput() { return null; }
	public java.lang.ProcessBuilder redirectOutput(java.io.File var0) { return null; }
	public java.lang.ProcessBuilder redirectOutput(java.lang.ProcessBuilder.Redirect var0) { return null; }
	public java.lang.Process start() throws java.io.IOException { return null; }
}

