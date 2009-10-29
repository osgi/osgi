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

package javax.sound.midi;
public class MidiSystem {
	public static javax.sound.midi.MidiDevice getMidiDevice(javax.sound.midi.MidiDevice.Info var0) throws javax.sound.midi.MidiUnavailableException { return null; }
	public static javax.sound.midi.MidiDevice.Info[] getMidiDeviceInfo() { return null; }
	public static javax.sound.midi.MidiFileFormat getMidiFileFormat(java.io.File var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.MidiFileFormat getMidiFileFormat(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.MidiFileFormat getMidiFileFormat(java.net.URL var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static int[] getMidiFileTypes() { return null; }
	public static int[] getMidiFileTypes(javax.sound.midi.Sequence var0) { return null; }
	public static javax.sound.midi.Receiver getReceiver() throws javax.sound.midi.MidiUnavailableException { return null; }
	public static javax.sound.midi.Sequence getSequence(java.io.File var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.Sequence getSequence(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.Sequence getSequence(java.net.URL var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.Sequencer getSequencer() throws javax.sound.midi.MidiUnavailableException { return null; }
	public static javax.sound.midi.Soundbank getSoundbank(java.io.File var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.Soundbank getSoundbank(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.Soundbank getSoundbank(java.net.URL var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException { return null; }
	public static javax.sound.midi.Synthesizer getSynthesizer() throws javax.sound.midi.MidiUnavailableException { return null; }
	public static javax.sound.midi.Transmitter getTransmitter() throws javax.sound.midi.MidiUnavailableException { return null; }
	public static boolean isFileTypeSupported(int var0) { return false; }
	public static boolean isFileTypeSupported(int var0, javax.sound.midi.Sequence var1) { return false; }
	public static int write(javax.sound.midi.Sequence var0, int var1, java.io.File var2) throws java.io.IOException { return 0; }
	public static int write(javax.sound.midi.Sequence var0, int var1, java.io.OutputStream var2) throws java.io.IOException { return 0; }
	private MidiSystem() { } /* generated constructor to prevent compiler adding default public constructor */
}

