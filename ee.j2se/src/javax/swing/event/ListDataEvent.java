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
public class ListDataEvent extends java.util.EventObject {
	public final static int CONTENTS_CHANGED = 0;
	public final static int INTERVAL_ADDED = 1;
	public final static int INTERVAL_REMOVED = 2;
	public ListDataEvent(java.lang.Object var0, int var1, int var2, int var3)  { super((java.lang.Object) null); } 
	public int getIndex0() { return 0; }
	public int getIndex1() { return 0; }
	public int getType() { return 0; }
}

