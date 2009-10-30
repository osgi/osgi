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
public class SysexMessage extends javax.sound.midi.MidiMessage {
	public final static int SPECIAL_SYSTEM_EXCLUSIVE = 247;
	public final static int SYSTEM_EXCLUSIVE = 240;
	public SysexMessage()  { super((byte[]) null); } 
	protected SysexMessage(byte[] var0)  { super((byte[]) null); } 
	public java.lang.Object clone() { return null; }
	public byte[] getData() { return null; }
	public void setMessage(int var0, byte[] var1, int var2) throws javax.sound.midi.InvalidMidiDataException { }
	public void setMessage(byte[] var0, int var1) throws javax.sound.midi.InvalidMidiDataException { }
}

