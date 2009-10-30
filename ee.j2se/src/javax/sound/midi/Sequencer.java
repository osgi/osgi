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
public interface Sequencer extends javax.sound.midi.MidiDevice {
	public static class SyncMode {
		public final static javax.sound.midi.Sequencer.SyncMode INTERNAL_CLOCK; static { INTERNAL_CLOCK = null; }
		public final static javax.sound.midi.Sequencer.SyncMode MIDI_SYNC; static { MIDI_SYNC = null; }
		public final static javax.sound.midi.Sequencer.SyncMode MIDI_TIME_CODE; static { MIDI_TIME_CODE = null; }
		public final static javax.sound.midi.Sequencer.SyncMode NO_SYNC; static { NO_SYNC = null; }
		protected SyncMode(java.lang.String var0) { } 
		public final boolean equals(java.lang.Object var0) { return false; }
		public final int hashCode() { return 0; }
		public final java.lang.String toString() { return null; }
	}
	public final static int LOOP_CONTINUOUSLY = -1;
	int[] addControllerEventListener(javax.sound.midi.ControllerEventListener var0, int[] var1);
	boolean addMetaEventListener(javax.sound.midi.MetaEventListener var0);
	int getLoopCount();
	long getLoopEndPoint();
	long getLoopStartPoint();
	javax.sound.midi.Sequencer.SyncMode getMasterSyncMode();
	javax.sound.midi.Sequencer.SyncMode[] getMasterSyncModes();
	long getMicrosecondLength();
	javax.sound.midi.Sequence getSequence();
	javax.sound.midi.Sequencer.SyncMode getSlaveSyncMode();
	javax.sound.midi.Sequencer.SyncMode[] getSlaveSyncModes();
	float getTempoFactor();
	float getTempoInBPM();
	float getTempoInMPQ();
	long getTickLength();
	long getTickPosition();
	boolean getTrackMute(int var0);
	boolean getTrackSolo(int var0);
	boolean isRecording();
	boolean isRunning();
	void recordDisable(javax.sound.midi.Track var0);
	void recordEnable(javax.sound.midi.Track var0, int var1);
	int[] removeControllerEventListener(javax.sound.midi.ControllerEventListener var0, int[] var1);
	void removeMetaEventListener(javax.sound.midi.MetaEventListener var0);
	void setLoopCount(int var0);
	void setLoopEndPoint(long var0);
	void setLoopStartPoint(long var0);
	void setMasterSyncMode(javax.sound.midi.Sequencer.SyncMode var0);
	void setMicrosecondPosition(long var0);
	void setSequence(java.io.InputStream var0) throws java.io.IOException, javax.sound.midi.InvalidMidiDataException;
	void setSequence(javax.sound.midi.Sequence var0) throws javax.sound.midi.InvalidMidiDataException;
	void setSlaveSyncMode(javax.sound.midi.Sequencer.SyncMode var0);
	void setTempoFactor(float var0);
	void setTempoInBPM(float var0);
	void setTempoInMPQ(float var0);
	void setTickPosition(long var0);
	void setTrackMute(int var0, boolean var1);
	void setTrackSolo(int var0, boolean var1);
	void start();
	void startRecording();
	void stop();
	void stopRecording();
}

