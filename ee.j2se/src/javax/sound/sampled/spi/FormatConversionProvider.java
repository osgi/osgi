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

package javax.sound.sampled.spi;
public abstract class FormatConversionProvider {
	public FormatConversionProvider() { } 
	public abstract javax.sound.sampled.AudioInputStream getAudioInputStream(javax.sound.sampled.AudioFormat.Encoding var0, javax.sound.sampled.AudioInputStream var1);
	public abstract javax.sound.sampled.AudioInputStream getAudioInputStream(javax.sound.sampled.AudioFormat var0, javax.sound.sampled.AudioInputStream var1);
	public abstract javax.sound.sampled.AudioFormat.Encoding[] getSourceEncodings();
	public abstract javax.sound.sampled.AudioFormat.Encoding[] getTargetEncodings();
	public abstract javax.sound.sampled.AudioFormat.Encoding[] getTargetEncodings(javax.sound.sampled.AudioFormat var0);
	public abstract javax.sound.sampled.AudioFormat[] getTargetFormats(javax.sound.sampled.AudioFormat.Encoding var0, javax.sound.sampled.AudioFormat var1);
	public boolean isConversionSupported(javax.sound.sampled.AudioFormat.Encoding var0, javax.sound.sampled.AudioFormat var1) { return false; }
	public boolean isConversionSupported(javax.sound.sampled.AudioFormat var0, javax.sound.sampled.AudioFormat var1) { return false; }
	public boolean isSourceEncodingSupported(javax.sound.sampled.AudioFormat.Encoding var0) { return false; }
	public boolean isTargetEncodingSupported(javax.sound.sampled.AudioFormat.Encoding var0) { return false; }
}

