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

package org.w3c.dom.events;
public abstract interface MouseEvent extends org.w3c.dom.events.UIEvent {
	public abstract boolean getAltKey();
	public abstract short getButton();
	public abstract int getClientX();
	public abstract int getClientY();
	public abstract boolean getCtrlKey();
	public abstract boolean getMetaKey();
	public abstract org.w3c.dom.events.EventTarget getRelatedTarget();
	public abstract int getScreenX();
	public abstract int getScreenY();
	public abstract boolean getShiftKey();
	public abstract void initMouseEvent(java.lang.String var0, boolean var1, boolean var2, org.w3c.dom.views.AbstractView var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, boolean var11, boolean var12, short var13, org.w3c.dom.events.EventTarget var14);
}

