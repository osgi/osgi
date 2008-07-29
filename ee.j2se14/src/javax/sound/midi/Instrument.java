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
public abstract class Instrument extends javax.sound.midi.SoundbankResource {
	protected Instrument(javax.sound.midi.Soundbank var0, javax.sound.midi.Patch var1, java.lang.String var2, java.lang.Class var3) { super((javax.sound.midi.Soundbank) null, (java.lang.String) null, (java.lang.Class) null); }
	public javax.sound.midi.Patch getPatch() { return null; }
}

