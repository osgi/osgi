/*
 * $Revision$
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

package java.awt.peer;
public abstract interface TextComponentPeer extends java.awt.peer.ComponentPeer {
	public abstract long filterEvents(long var0);
	public abstract int getCaretPosition();
	public abstract java.awt.Rectangle getCharacterBounds(int var0);
	public abstract int getIndexAtPoint(int var0, int var1);
	public abstract int getSelectionEnd();
	public abstract int getSelectionStart();
	public abstract java.lang.String getText();
	public abstract void select(int var0, int var1);
	public abstract void setCaretPosition(int var0);
	public abstract void setEditable(boolean var0);
	public abstract void setText(java.lang.String var0);
}

