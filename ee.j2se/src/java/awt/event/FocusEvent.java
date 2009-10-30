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

package java.awt.event;
public class FocusEvent extends java.awt.event.ComponentEvent {
	public final static int FOCUS_FIRST = 1004;
	public final static int FOCUS_GAINED = 1004;
	public final static int FOCUS_LAST = 1005;
	public final static int FOCUS_LOST = 1005;
	public FocusEvent(java.awt.Component var0, int var1)  { super((java.awt.Component) null, 0); } 
	public FocusEvent(java.awt.Component var0, int var1, boolean var2)  { super((java.awt.Component) null, 0); } 
	public FocusEvent(java.awt.Component var0, int var1, boolean var2, java.awt.Component var3)  { super((java.awt.Component) null, 0); } 
	public java.awt.Component getOppositeComponent() { return null; }
	public boolean isTemporary() { return false; }
}

