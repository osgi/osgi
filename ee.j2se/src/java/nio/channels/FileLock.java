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

package java.nio.channels;
public abstract class FileLock {
	protected FileLock(java.nio.channels.FileChannel var0, long var1, long var2, boolean var3) { }
	public final java.nio.channels.FileChannel channel() { return null; }
	public final boolean isShared() { return false; }
	public abstract boolean isValid();
	public final boolean overlaps(long var0, long var1) { return false; }
	public final long position() { return 0l; }
	public abstract void release() throws java.io.IOException;
	public final long size() { return 0l; }
	public final java.lang.String toString() { return null; }
}

