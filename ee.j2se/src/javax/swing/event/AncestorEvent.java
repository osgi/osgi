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

package javax.swing.event;
public class AncestorEvent extends java.awt.AWTEvent {
	public final static int ANCESTOR_ADDED = 1;
	public final static int ANCESTOR_MOVED = 3;
	public final static int ANCESTOR_REMOVED = 2;
	public AncestorEvent(javax.swing.JComponent var0, int var1, java.awt.Container var2, java.awt.Container var3)  { super((java.lang.Object) null, 0); } 
	public java.awt.Container getAncestor() { return null; }
	public java.awt.Container getAncestorParent() { return null; }
	public javax.swing.JComponent getComponent() { return null; }
}

