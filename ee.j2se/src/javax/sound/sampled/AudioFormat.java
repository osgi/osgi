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
public class AudioFormat {
	public AudioFormat(float var0, int var1, int var2, boolean var3, boolean var4) { }
	public AudioFormat(javax.sound.sampled.AudioFormat.Encoding var0, float var1, int var2, int var3, int var4, float var5, boolean var6) { }
	public int getChannels() { return 0; }
	public javax.sound.sampled.AudioFormat.Encoding getEncoding() { return null; }
	public float getFrameRate() { return 0.0f; }
	public int getFrameSize() { return 0; }
	public float getSampleRate() { return 0.0f; }
	public int getSampleSizeInBits() { return 0; }
	public boolean isBigEndian() { return false; }
	public boolean matches(javax.sound.sampled.AudioFormat var0) { return false; }
	protected boolean bigEndian;
	protected int channels;
	protected javax.sound.sampled.AudioFormat.Encoding encoding;
	protected float frameRate;
	protected int frameSize;
	protected float sampleRate;
	protected int sampleSizeInBits;
	public static class Encoding {
		protected Encoding(java.lang.String var0) { }
		public final boolean equals(java.lang.Object var0) { return false; }
		public final int hashCode() { return 0; }
		public final java.lang.String toString() { return null; }
		public final static javax.sound.sampled.AudioFormat.Encoding ALAW; static { ALAW = null; }
		public final static javax.sound.sampled.AudioFormat.Encoding PCM_SIGNED; static { PCM_SIGNED = null; }
		public final static javax.sound.sampled.AudioFormat.Encoding PCM_UNSIGNED; static { PCM_UNSIGNED = null; }
		public final static javax.sound.sampled.AudioFormat.Encoding ULAW; static { ULAW = null; }
	}
}

