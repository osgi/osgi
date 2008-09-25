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

package javax.sound.midi;
public class MidiFileFormat {
	public MidiFileFormat(int var0, float var1, int var2, int var3, long var4) { }
	public int getByteLength() { return 0; }
	public float getDivisionType() { return 0.0f; }
	public long getMicrosecondLength() { return 0l; }
	public int getResolution() { return 0; }
	public int getType() { return 0; }
	public final static int UNKNOWN_LENGTH = -1;
	protected int byteLength;
	protected float divisionType;
	protected long microsecondLength;
	protected int resolution;
	protected int type;
}

