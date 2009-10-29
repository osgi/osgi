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

package java.awt.im.spi;
public abstract interface InputMethodContext extends java.awt.im.InputMethodRequests {
	public abstract javax.swing.JFrame createInputMethodJFrame(java.lang.String var0, boolean var1);
	public abstract java.awt.Window createInputMethodWindow(java.lang.String var0, boolean var1);
	public abstract void dispatchInputMethodEvent(int var0, java.text.AttributedCharacterIterator var1, int var2, java.awt.font.TextHitInfo var3, java.awt.font.TextHitInfo var4);
	public abstract void enableClientWindowNotification(java.awt.im.spi.InputMethod var0, boolean var1);
}

