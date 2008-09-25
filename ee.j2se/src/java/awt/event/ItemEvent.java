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

package java.awt.event;
public class ItemEvent extends java.awt.AWTEvent {
	public ItemEvent(java.awt.ItemSelectable var0, int var1, java.lang.Object var2, int var3) { super((java.lang.Object) null, 0); }
	public java.lang.Object getItem() { return null; }
	public java.awt.ItemSelectable getItemSelectable() { return null; }
	public int getStateChange() { return 0; }
	public final static int DESELECTED = 2;
	public final static int ITEM_FIRST = 701;
	public final static int ITEM_LAST = 701;
	public final static int ITEM_STATE_CHANGED = 701;
	public final static int SELECTED = 1;
}

