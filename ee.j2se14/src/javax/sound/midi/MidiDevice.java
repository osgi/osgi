/*
 * $Date$
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
public abstract interface MidiDevice {
	public abstract void close();
	public abstract javax.sound.midi.MidiDevice.Info getDeviceInfo();
	public abstract int getMaxReceivers();
	public abstract int getMaxTransmitters();
	public abstract long getMicrosecondPosition();
	public abstract javax.sound.midi.Receiver getReceiver() throws javax.sound.midi.MidiUnavailableException;
	public abstract javax.sound.midi.Transmitter getTransmitter() throws javax.sound.midi.MidiUnavailableException;
	public abstract boolean isOpen();
	public abstract void open() throws javax.sound.midi.MidiUnavailableException;
	public static class Info {
		protected Info(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) { }
		public final boolean equals(java.lang.Object var0) { return false; }
		public final java.lang.String getDescription() { return null; }
		public final java.lang.String getName() { return null; }
		public final java.lang.String getVendor() { return null; }
		public final java.lang.String getVersion() { return null; }
		public final int hashCode() { return 0; }
		public final java.lang.String toString() { return null; }
	}
}

