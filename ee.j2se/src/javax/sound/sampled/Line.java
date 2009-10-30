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
public interface Line {
	public static class Info {
		public Info(java.lang.Class<?> var0) { } 
		public java.lang.Class<?> getLineClass() { return null; }
		public boolean matches(javax.sound.sampled.Line.Info var0) { return false; }
	}
	void addLineListener(javax.sound.sampled.LineListener var0);
	void close();
	javax.sound.sampled.Control getControl(javax.sound.sampled.Control.Type var0);
	javax.sound.sampled.Control[] getControls();
	javax.sound.sampled.Line.Info getLineInfo();
	boolean isControlSupported(javax.sound.sampled.Control.Type var0);
	boolean isOpen();
	void open() throws javax.sound.sampled.LineUnavailableException;
	void removeLineListener(javax.sound.sampled.LineListener var0);
}

