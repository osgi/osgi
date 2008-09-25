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

package javax.sound.midi.spi;
public abstract class MidiFileReader {
	public MidiFileReader() { }
	public abstract javax.sound.midi.MidiFileFormat getMidiFileFormat(java.io.File var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	public abstract javax.sound.midi.MidiFileFormat getMidiFileFormat(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	public abstract javax.sound.midi.MidiFileFormat getMidiFileFormat(java.net.URL var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	public abstract javax.sound.midi.Sequence getSequence(java.io.File var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	public abstract javax.sound.midi.Sequence getSequence(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	public abstract javax.sound.midi.Sequence getSequence(java.net.URL var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
}

