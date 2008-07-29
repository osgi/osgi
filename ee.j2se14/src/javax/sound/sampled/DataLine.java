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

package javax.sound.sampled;
public abstract interface DataLine extends javax.sound.sampled.Line {
	public abstract int available();
	public abstract void drain();
	public abstract void flush();
	public abstract int getBufferSize();
	public abstract javax.sound.sampled.AudioFormat getFormat();
	public abstract int getFramePosition();
	public abstract float getLevel();
	public abstract long getMicrosecondPosition();
	public abstract boolean isActive();
	public abstract boolean isRunning();
	public abstract void start();
	public abstract void stop();
	public static class Info extends javax.sound.sampled.Line.Info {
		public Info(java.lang.Class var0, javax.sound.sampled.AudioFormat var1) { super((java.lang.Class) null); }
		public Info(java.lang.Class var0, javax.sound.sampled.AudioFormat var1, int var2) { super((java.lang.Class) null); }
		public Info(java.lang.Class var0, javax.sound.sampled.AudioFormat[] var1, int var2, int var3) { super((java.lang.Class) null); }
		public javax.sound.sampled.AudioFormat[] getFormats() { return null; }
		public int getMaxBufferSize() { return 0; }
		public int getMinBufferSize() { return 0; }
		public boolean isFormatSupported(javax.sound.sampled.AudioFormat var0) { return false; }
	}
}

