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

package java.nio.channels;
public abstract class Pipe {
	protected Pipe() { }
	public static java.nio.channels.Pipe open() throws java.io.IOException { return null; }
	public abstract java.nio.channels.Pipe.SinkChannel sink();
	public abstract java.nio.channels.Pipe.SourceChannel source();
	public static abstract class SinkChannel extends java.nio.channels.spi.AbstractSelectableChannel implements java.nio.channels.GatheringByteChannel, java.nio.channels.WritableByteChannel {
		protected SinkChannel(java.nio.channels.spi.SelectorProvider var0) { super((java.nio.channels.spi.SelectorProvider) null); }
		public final int validOps() { return 0; }
	}
	public static abstract class SourceChannel extends java.nio.channels.spi.AbstractSelectableChannel implements java.nio.channels.ReadableByteChannel, java.nio.channels.ScatteringByteChannel {
		protected SourceChannel(java.nio.channels.spi.SelectorProvider var0) { super((java.nio.channels.spi.SelectorProvider) null); }
		public final int validOps() { return 0; }
	}
}

