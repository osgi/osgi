/*
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
public class AudioFileFormat {
	protected AudioFileFormat(javax.sound.sampled.AudioFileFormat.Type var0, int var1, javax.sound.sampled.AudioFormat var2, int var3) { }
	public AudioFileFormat(javax.sound.sampled.AudioFileFormat.Type var0, javax.sound.sampled.AudioFormat var1, int var2) { }
	public int getByteLength() { return 0; }
	public javax.sound.sampled.AudioFormat getFormat() { return null; }
	public int getFrameLength() { return 0; }
	public javax.sound.sampled.AudioFileFormat.Type getType() { return null; }
	public static class Type {
		protected Type(java.lang.String var0, java.lang.String var1) { }
		public final boolean equals(java.lang.Object var0) { return false; }
		public java.lang.String getExtension() { return null; }
		public final int hashCode() { return 0; }
		public final java.lang.String toString() { return null; }
		public final static javax.sound.sampled.AudioFileFormat.Type AIFC; static { AIFC = null; }
		public final static javax.sound.sampled.AudioFileFormat.Type AIFF; static { AIFF = null; }
		public final static javax.sound.sampled.AudioFileFormat.Type AU; static { AU = null; }
		public final static javax.sound.sampled.AudioFileFormat.Type SND; static { SND = null; }
		public final static javax.sound.sampled.AudioFileFormat.Type WAVE; static { WAVE = null; }
	}
}

