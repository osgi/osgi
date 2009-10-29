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

package org.w3c.dom.events;
public abstract interface Event {
	public abstract boolean getBubbles();
	public abstract boolean getCancelable();
	public abstract org.w3c.dom.events.EventTarget getCurrentTarget();
	public abstract short getEventPhase();
	public abstract org.w3c.dom.events.EventTarget getTarget();
	public abstract long getTimeStamp();
	public abstract java.lang.String getType();
	public abstract void initEvent(java.lang.String var0, boolean var1, boolean var2);
	public abstract void preventDefault();
	public abstract void stopPropagation();
	public final static short AT_TARGET = 2;
	public final static short BUBBLING_PHASE = 3;
	public final static short CAPTURING_PHASE = 1;
}

