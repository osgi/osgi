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

package javax.sound.sampled;
public abstract interface Port extends javax.sound.sampled.Line {
	public static class Info extends javax.sound.sampled.Line.Info {
		public Info(java.lang.Class var0, java.lang.String var1, boolean var2) { super((java.lang.Class) null); }
		public final boolean equals(java.lang.Object var0) { return false; }
		public java.lang.String getName() { return null; }
		public final int hashCode() { return 0; }
		public boolean isSource() { return false; }
		public final java.lang.String toString() { return null; }
		public final static javax.sound.sampled.Port.Info COMPACT_DISC; static { COMPACT_DISC = null; }
		public final static javax.sound.sampled.Port.Info HEADPHONE; static { HEADPHONE = null; }
		public final static javax.sound.sampled.Port.Info LINE_IN; static { LINE_IN = null; }
		public final static javax.sound.sampled.Port.Info LINE_OUT; static { LINE_OUT = null; }
		public final static javax.sound.sampled.Port.Info MICROPHONE; static { MICROPHONE = null; }
		public final static javax.sound.sampled.Port.Info SPEAKER; static { SPEAKER = null; }
	}
}

