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
public interface Mixer extends javax.sound.sampled.Line {
	public static class Info {
		protected Info(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) { } 
		public final boolean equals(java.lang.Object var0) { return false; }
		public final java.lang.String getDescription() { return null; }
		public final java.lang.String getName() { return null; }
		public final java.lang.String getVendor() { return null; }
		public final java.lang.String getVersion() { return null; }
		public final int hashCode() { return 0; }
		public final java.lang.String toString() { return null; }
	}
	javax.sound.sampled.Line getLine(javax.sound.sampled.Line.Info var0) throws javax.sound.sampled.LineUnavailableException;
	int getMaxLines(javax.sound.sampled.Line.Info var0);
	javax.sound.sampled.Mixer.Info getMixerInfo();
	javax.sound.sampled.Line.Info[] getSourceLineInfo();
	javax.sound.sampled.Line.Info[] getSourceLineInfo(javax.sound.sampled.Line.Info var0);
	javax.sound.sampled.Line[] getSourceLines();
	javax.sound.sampled.Line.Info[] getTargetLineInfo();
	javax.sound.sampled.Line.Info[] getTargetLineInfo(javax.sound.sampled.Line.Info var0);
	javax.sound.sampled.Line[] getTargetLines();
	boolean isLineSupported(javax.sound.sampled.Line.Info var0);
	boolean isSynchronizationSupported(javax.sound.sampled.Line[] var0, boolean var1);
	void synchronize(javax.sound.sampled.Line[] var0, boolean var1);
	void unsynchronize(javax.sound.sampled.Line[] var0);
}

