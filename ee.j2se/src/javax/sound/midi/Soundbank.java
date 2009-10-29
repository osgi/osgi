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
public abstract interface Soundbank {
	public abstract java.lang.String getDescription();
	public abstract javax.sound.midi.Instrument getInstrument(javax.sound.midi.Patch var0);
	public abstract javax.sound.midi.Instrument[] getInstruments();
	public abstract java.lang.String getName();
	public abstract javax.sound.midi.SoundbankResource[] getResources();
	public abstract java.lang.String getVendor();
	public abstract java.lang.String getVersion();
}

