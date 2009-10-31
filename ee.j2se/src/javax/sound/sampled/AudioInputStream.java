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
public class AudioInputStream extends java.io.InputStream {
	protected javax.sound.sampled.AudioFormat format;
	protected long frameLength;
	protected long framePos;
	protected int frameSize;
	public AudioInputStream(java.io.InputStream var0, javax.sound.sampled.AudioFormat var1, long var2) { } 
	public AudioInputStream(javax.sound.sampled.TargetDataLine var0) { } 
	public javax.sound.sampled.AudioFormat getFormat() { return null; }
	public long getFrameLength() { return 0l; }
	public int read() throws java.io.IOException { return 0; }
}

