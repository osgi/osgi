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
public abstract interface Clip extends javax.sound.sampled.DataLine {
	public abstract int getFrameLength();
	public abstract long getMicrosecondLength();
	public abstract void loop(int var0);
	public abstract void open(javax.sound.sampled.AudioFormat var0, byte[] var1, int var2, int var3) throws javax.sound.sampled.LineUnavailableException;
	public abstract void open(javax.sound.sampled.AudioInputStream var0) throws java.io.IOException, javax.sound.sampled.LineUnavailableException;
	public abstract void setFramePosition(int var0);
	public abstract void setLoopPoints(int var0, int var1);
	public abstract void setMicrosecondPosition(long var0);
	public final static int LOOP_CONTINUOUSLY = -1;
}

