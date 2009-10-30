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
public interface MidiChannel {
	void allNotesOff();
	void allSoundOff();
	void controlChange(int var0, int var1);
	int getChannelPressure();
	int getController(int var0);
	boolean getMono();
	boolean getMute();
	boolean getOmni();
	int getPitchBend();
	int getPolyPressure(int var0);
	int getProgram();
	boolean getSolo();
	boolean localControl(boolean var0);
	void noteOff(int var0);
	void noteOff(int var0, int var1);
	void noteOn(int var0, int var1);
	void programChange(int var0);
	void programChange(int var0, int var1);
	void resetAllControllers();
	void setChannelPressure(int var0);
	void setMono(boolean var0);
	void setMute(boolean var0);
	void setOmni(boolean var0);
	void setPitchBend(int var0);
	void setPolyPressure(int var0, int var1);
	void setSolo(boolean var0);
}

