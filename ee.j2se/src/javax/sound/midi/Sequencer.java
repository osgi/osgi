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
public abstract interface Sequencer extends javax.sound.midi.MidiDevice {
	public abstract int[] addControllerEventListener(javax.sound.midi.ControllerEventListener var0, int[] var1);
	public abstract boolean addMetaEventListener(javax.sound.midi.MetaEventListener var0);
	public abstract javax.sound.midi.Sequencer.SyncMode getMasterSyncMode();
	public abstract javax.sound.midi.Sequencer.SyncMode[] getMasterSyncModes();
	public abstract long getMicrosecondLength();
	public abstract javax.sound.midi.Sequence getSequence();
	public abstract javax.sound.midi.Sequencer.SyncMode getSlaveSyncMode();
	public abstract javax.sound.midi.Sequencer.SyncMode[] getSlaveSyncModes();
	public abstract float getTempoFactor();
	public abstract float getTempoInBPM();
	public abstract float getTempoInMPQ();
	public abstract long getTickLength();
	public abstract long getTickPosition();
	public abstract boolean getTrackMute(int var0);
	public abstract boolean getTrackSolo(int var0);
	public abstract boolean isRecording();
	public abstract boolean isRunning();
	public abstract void recordDisable(javax.sound.midi.Track var0);
	public abstract void recordEnable(javax.sound.midi.Track var0, int var1);
	public abstract int[] removeControllerEventListener(javax.sound.midi.ControllerEventListener var0, int[] var1);
	public abstract void removeMetaEventListener(javax.sound.midi.MetaEventListener var0);
	public abstract void setMasterSyncMode(javax.sound.midi.Sequencer.SyncMode var0);
	public abstract void setMicrosecondPosition(long var0);
	public abstract void setSequence(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	public abstract void setSequence(javax.sound.midi.Sequence var0) throws javax.sound.midi.InvalidMidiDataException;
	public abstract void setSlaveSyncMode(javax.sound.midi.Sequencer.SyncMode var0);
	public abstract void setTempoFactor(float var0);
	public abstract void setTempoInBPM(float var0);
	public abstract void setTempoInMPQ(float var0);
	public abstract void setTickPosition(long var0);
	public abstract void setTrackMute(int var0, boolean var1);
	public abstract void setTrackSolo(int var0, boolean var1);
	public abstract void start();
	public abstract void startRecording();
	public abstract void stop();
	public abstract void stopRecording();
	public static class SyncMode {
		protected SyncMode(java.lang.String var0) { }
		public final boolean equals(java.lang.Object var0) { return false; }
		public final int hashCode() { return 0; }
		public final java.lang.String toString() { return null; }
		public final static javax.sound.midi.Sequencer.SyncMode INTERNAL_CLOCK; static { INTERNAL_CLOCK = null; }
		public final static javax.sound.midi.Sequencer.SyncMode MIDI_SYNC; static { MIDI_SYNC = null; }
		public final static javax.sound.midi.Sequencer.SyncMode MIDI_TIME_CODE; static { MIDI_TIME_CODE = null; }
		public final static javax.sound.midi.Sequencer.SyncMode NO_SYNC; static { NO_SYNC = null; }
	}
}

