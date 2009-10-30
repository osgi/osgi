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

package javax.sound.midi;
public interface Synthesizer extends javax.sound.midi.MidiDevice {
	javax.sound.midi.Instrument[] getAvailableInstruments();
	javax.sound.midi.MidiChannel[] getChannels();
	javax.sound.midi.Soundbank getDefaultSoundbank();
	long getLatency();
	javax.sound.midi.Instrument[] getLoadedInstruments();
	int getMaxPolyphony();
	javax.sound.midi.VoiceStatus[] getVoiceStatus();
	boolean isSoundbankSupported(javax.sound.midi.Soundbank var0);
	boolean loadAllInstruments(javax.sound.midi.Soundbank var0);
	boolean loadInstrument(javax.sound.midi.Instrument var0);
	boolean loadInstruments(javax.sound.midi.Soundbank var0, javax.sound.midi.Patch[] var1);
	boolean remapInstrument(javax.sound.midi.Instrument var0, javax.sound.midi.Instrument var1);
	void unloadAllInstruments(javax.sound.midi.Soundbank var0);
	void unloadInstrument(javax.sound.midi.Instrument var0);
	void unloadInstruments(javax.sound.midi.Soundbank var0, javax.sound.midi.Patch[] var1);
}

