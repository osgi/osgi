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

package javax.sound.midi.spi;
public abstract class MidiFileWriter {
	public MidiFileWriter() { } 
	public abstract int[] getMidiFileTypes();
	public abstract int[] getMidiFileTypes(javax.sound.midi.Sequence var0);
	public boolean isFileTypeSupported(int var0) { return false; }
	public boolean isFileTypeSupported(int var0, javax.sound.midi.Sequence var1) { return false; }
	public abstract int write(javax.sound.midi.Sequence var0, int var1, java.io.File var2) throws java.io.IOException;
	public abstract int write(javax.sound.midi.Sequence var0, int var1, java.io.OutputStream var2) throws java.io.IOException;
}

