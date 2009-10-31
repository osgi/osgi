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

package sun.awt;
public class CausedFocusEvent extends java.awt.event.FocusEvent {
	public enum Cause {
		ACTIVATION,
		AUTOMATIC_TRAVERSE,
		CLEAR_GLOBAL_FOCUS_OWNER,
		MANUAL_REQUEST,
		MOUSE_EVENT,
		NATIVE_SYSTEM,
		RETARGETED,
		ROLLBACK,
		TRAVERSAL,
		TRAVERSAL_BACKWARD,
		TRAVERSAL_DOWN,
		TRAVERSAL_FORWARD,
		TRAVERSAL_UP,
		UNKNOWN;
	}
	public CausedFocusEvent(java.awt.Component var0, int var1, boolean var2, java.awt.Component var3, sun.awt.CausedFocusEvent.Cause var4)  { super((java.awt.Component) null, 0, false, (java.awt.Component) null); } 
	public sun.awt.CausedFocusEvent.Cause getCause() { return null; }
	public static java.awt.event.FocusEvent retarget(java.awt.event.FocusEvent var0, java.awt.Component var1) { return null; }
}

