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

package javax.sound.sampled;
public class AudioSystem {
	public static javax.sound.sampled.AudioFileFormat getAudioFileFormat(java.io.File var0) throws java.io.IOException, javax.sound.sampled.UnsupportedAudioFileException { return null; }
	public static javax.sound.sampled.AudioFileFormat getAudioFileFormat(java.io.InputStream var0) throws java.io.IOException, javax.sound.sampled.UnsupportedAudioFileException { return null; }
	public static javax.sound.sampled.AudioFileFormat getAudioFileFormat(java.net.URL var0) throws java.io.IOException, javax.sound.sampled.UnsupportedAudioFileException { return null; }
	public static javax.sound.sampled.AudioFileFormat.Type[] getAudioFileTypes() { return null; }
	public static javax.sound.sampled.AudioFileFormat.Type[] getAudioFileTypes(javax.sound.sampled.AudioInputStream var0) { return null; }
	public static javax.sound.sampled.AudioInputStream getAudioInputStream(java.io.File var0) throws java.io.IOException, javax.sound.sampled.UnsupportedAudioFileException { return null; }
	public static javax.sound.sampled.AudioInputStream getAudioInputStream(java.io.InputStream var0) throws java.io.IOException, javax.sound.sampled.UnsupportedAudioFileException { return null; }
	public static javax.sound.sampled.AudioInputStream getAudioInputStream(java.net.URL var0) throws java.io.IOException, javax.sound.sampled.UnsupportedAudioFileException { return null; }
	public static javax.sound.sampled.AudioInputStream getAudioInputStream(javax.sound.sampled.AudioFormat.Encoding var0, javax.sound.sampled.AudioInputStream var1) { return null; }
	public static javax.sound.sampled.AudioInputStream getAudioInputStream(javax.sound.sampled.AudioFormat var0, javax.sound.sampled.AudioInputStream var1) { return null; }
	public static javax.sound.sampled.Line getLine(javax.sound.sampled.Line.Info var0) throws javax.sound.sampled.LineUnavailableException { return null; }
	public static javax.sound.sampled.Mixer getMixer(javax.sound.sampled.Mixer.Info var0) { return null; }
	public static javax.sound.sampled.Mixer.Info[] getMixerInfo() { return null; }
	public static javax.sound.sampled.Line.Info[] getSourceLineInfo(javax.sound.sampled.Line.Info var0) { return null; }
	public static javax.sound.sampled.AudioFormat.Encoding[] getTargetEncodings(javax.sound.sampled.AudioFormat.Encoding var0) { return null; }
	public static javax.sound.sampled.AudioFormat.Encoding[] getTargetEncodings(javax.sound.sampled.AudioFormat var0) { return null; }
	public static javax.sound.sampled.AudioFormat[] getTargetFormats(javax.sound.sampled.AudioFormat.Encoding var0, javax.sound.sampled.AudioFormat var1) { return null; }
	public static javax.sound.sampled.Line.Info[] getTargetLineInfo(javax.sound.sampled.Line.Info var0) { return null; }
	public static boolean isConversionSupported(javax.sound.sampled.AudioFormat.Encoding var0, javax.sound.sampled.AudioFormat var1) { return false; }
	public static boolean isConversionSupported(javax.sound.sampled.AudioFormat var0, javax.sound.sampled.AudioFormat var1) { return false; }
	public static boolean isFileTypeSupported(javax.sound.sampled.AudioFileFormat.Type var0) { return false; }
	public static boolean isFileTypeSupported(javax.sound.sampled.AudioFileFormat.Type var0, javax.sound.sampled.AudioInputStream var1) { return false; }
	public static boolean isLineSupported(javax.sound.sampled.Line.Info var0) { return false; }
	public static int write(javax.sound.sampled.AudioInputStream var0, javax.sound.sampled.AudioFileFormat.Type var1, java.io.File var2) throws java.io.IOException { return 0; }
	public static int write(javax.sound.sampled.AudioInputStream var0, javax.sound.sampled.AudioFileFormat.Type var1, java.io.OutputStream var2) throws java.io.IOException { return 0; }
	public final static int NOT_SPECIFIED = -1;
	private AudioSystem() { } /* generated constructor to prevent compiler adding default public constructor */
}

