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

package java.awt.event;
public class HierarchyEvent extends java.awt.AWTEvent {
	public HierarchyEvent(java.awt.Component var0, int var1, java.awt.Component var2, java.awt.Container var3) { super((java.lang.Object) null, 0); }
	public HierarchyEvent(java.awt.Component var0, int var1, java.awt.Component var2, java.awt.Container var3, long var4) { super((java.lang.Object) null, 0); }
	public long getChangeFlags() { return 0l; }
	public java.awt.Component getChanged() { return null; }
	public java.awt.Container getChangedParent() { return null; }
	public java.awt.Component getComponent() { return null; }
	public final static int ANCESTOR_MOVED = 1401;
	public final static int ANCESTOR_RESIZED = 1402;
	public final static int DISPLAYABILITY_CHANGED = 2;
	public final static int HIERARCHY_CHANGED = 1400;
	public final static int HIERARCHY_FIRST = 1400;
	public final static int HIERARCHY_LAST = 1402;
	public final static int PARENT_CHANGED = 1;
	public final static int SHOWING_CHANGED = 4;
}

