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
public class Sequence {
	public Sequence(float var0, int var1) throws javax.sound.midi.InvalidMidiDataException { }
	public Sequence(float var0, int var1, int var2) throws javax.sound.midi.InvalidMidiDataException { }
	public javax.sound.midi.Track createTrack() { return null; }
	public boolean deleteTrack(javax.sound.midi.Track var0) { return false; }
	public float getDivisionType() { return 0.0f; }
	public long getMicrosecondLength() { return 0l; }
	public javax.sound.midi.Patch[] getPatchList() { return null; }
	public int getResolution() { return 0; }
	public long getTickLength() { return 0l; }
	public javax.sound.midi.Track[] getTracks() { return null; }
	public final static float PPQ = 0.0f;
	public final static float SMPTE_24 = 24.0f;
	public final static float SMPTE_25 = 25.0f;
	public final static float SMPTE_30 = 30.0f;
	public final static float SMPTE_30DROP = 29.97f;
	protected float divisionType;
	protected int resolution;
	protected java.util.Vector tracks;
}

