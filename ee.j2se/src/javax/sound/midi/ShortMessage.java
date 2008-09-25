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
public class ShortMessage extends javax.sound.midi.MidiMessage {
	public ShortMessage() { super((byte[]) null); }
	protected ShortMessage(byte[] var0) { super((byte[]) null); }
	public java.lang.Object clone() { return null; }
	public int getChannel() { return 0; }
	public int getCommand() { return 0; }
	public int getData1() { return 0; }
	public int getData2() { return 0; }
	protected final int getDataLength(int var0) throws javax.sound.midi.InvalidMidiDataException { return 0; }
	public void setMessage(int var0) throws javax.sound.midi.InvalidMidiDataException { }
	public void setMessage(int var0, int var1, int var2) throws javax.sound.midi.InvalidMidiDataException { }
	public void setMessage(int var0, int var1, int var2, int var3) throws javax.sound.midi.InvalidMidiDataException { }
	public final static int ACTIVE_SENSING = 254;
	public final static int CHANNEL_PRESSURE = 208;
	public final static int CONTINUE = 251;
	public final static int CONTROL_CHANGE = 176;
	public final static int END_OF_EXCLUSIVE = 247;
	public final static int MIDI_TIME_CODE = 241;
	public final static int NOTE_OFF = 128;
	public final static int NOTE_ON = 144;
	public final static int PITCH_BEND = 224;
	public final static int POLY_PRESSURE = 160;
	public final static int PROGRAM_CHANGE = 192;
	public final static int SONG_POSITION_POINTER = 242;
	public final static int SONG_SELECT = 243;
	public final static int START = 250;
	public final static int STOP = 252;
	public final static int SYSTEM_RESET = 255;
	public final static int TIMING_CLOCK = 248;
	public final static int TUNE_REQUEST = 246;
}

