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

package org.w3c.dom.events;
public interface Event {
	public final static short AT_TARGET = 2;
	public final static short BUBBLING_PHASE = 3;
	public final static short CAPTURING_PHASE = 1;
	boolean getBubbles();
	boolean getCancelable();
	org.w3c.dom.events.EventTarget getCurrentTarget();
	short getEventPhase();
	org.w3c.dom.events.EventTarget getTarget();
	long getTimeStamp();
	java.lang.String getType();
	void initEvent(java.lang.String var0, boolean var1, boolean var2);
	void preventDefault();
	void stopPropagation();
}

