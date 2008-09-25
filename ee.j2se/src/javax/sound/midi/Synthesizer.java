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
public abstract interface Synthesizer extends javax.sound.midi.MidiDevice {
	public abstract javax.sound.midi.Instrument[] getAvailableInstruments();
	public abstract javax.sound.midi.MidiChannel[] getChannels();
	public abstract javax.sound.midi.Soundbank getDefaultSoundbank();
	public abstract long getLatency();
	public abstract javax.sound.midi.Instrument[] getLoadedInstruments();
	public abstract int getMaxPolyphony();
	public abstract javax.sound.midi.VoiceStatus[] getVoiceStatus();
	public abstract boolean isSoundbankSupported(javax.sound.midi.Soundbank var0);
	public abstract boolean loadAllInstruments(javax.sound.midi.Soundbank var0);
	public abstract boolean loadInstrument(javax.sound.midi.Instrument var0);
	public abstract boolean loadInstruments(javax.sound.midi.Soundbank var0, javax.sound.midi.Patch[] var1);
	public abstract boolean remapInstrument(javax.sound.midi.Instrument var0, javax.sound.midi.Instrument var1);
	public abstract void unloadAllInstruments(javax.sound.midi.Soundbank var0);
	public abstract void unloadInstrument(javax.sound.midi.Instrument var0);
	public abstract void unloadInstruments(javax.sound.midi.Soundbank var0, javax.sound.midi.Patch[] var1);
}

