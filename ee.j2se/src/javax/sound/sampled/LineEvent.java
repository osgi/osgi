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

package javax.sound.sampled;
public class LineEvent extends java.util.EventObject {
	public static class Type {
		public final static javax.sound.sampled.LineEvent.Type CLOSE; static { CLOSE = null; }
		public final static javax.sound.sampled.LineEvent.Type OPEN; static { OPEN = null; }
		public final static javax.sound.sampled.LineEvent.Type START; static { START = null; }
		public final static javax.sound.sampled.LineEvent.Type STOP; static { STOP = null; }
		protected Type(java.lang.String var0) { } 
		public final boolean equals(java.lang.Object var0) { return false; }
		public final int hashCode() { return 0; }
	}
	public LineEvent(javax.sound.sampled.Line var0, javax.sound.sampled.LineEvent.Type var1, long var2)  { super((java.lang.Object) null); } 
	public final long getFramePosition() { return 0l; }
	public final javax.sound.sampled.Line getLine() { return null; }
	public final javax.sound.sampled.LineEvent.Type getType() { return null; }
}

