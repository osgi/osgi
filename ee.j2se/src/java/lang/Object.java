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

package java.lang;
public class Object {
	public Object() { } 
	protected native java.lang.Object clone() throws java.lang.CloneNotSupportedException;
	public boolean equals(java.lang.Object var0) { return false; }
	protected void finalize() throws java.lang.Throwable { }
	public final native java.lang.Class<?> getClass();
	public native int hashCode();
	public final native void notify();
	public final native void notifyAll();
	public java.lang.String toString() { return null; }
	public final void wait() throws java.lang.InterruptedException { }
	public final native void wait(long var0) throws java.lang.InterruptedException;
	public final void wait(long var0, int var1) throws java.lang.InterruptedException { }
}

