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

package javax.sound.sampled;
public interface TargetDataLine extends javax.sound.sampled.DataLine {
	void open(javax.sound.sampled.AudioFormat var0) throws javax.sound.sampled.LineUnavailableException;
	void open(javax.sound.sampled.AudioFormat var0, int var1) throws javax.sound.sampled.LineUnavailableException;
	int read(byte[] var0, int var1, int var2);
}

