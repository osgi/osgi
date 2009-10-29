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

package java.awt.peer;
public abstract interface FramePeer extends java.awt.peer.WindowPeer {
	public abstract int getState();
	public abstract void setIconImage(java.awt.Image var0);
	public abstract void setMaximizedBounds(java.awt.Rectangle var0);
	public abstract void setMenuBar(java.awt.MenuBar var0);
	public abstract void setResizable(boolean var0);
	public abstract void setState(int var0);
	public abstract void setTitle(java.lang.String var0);
}

