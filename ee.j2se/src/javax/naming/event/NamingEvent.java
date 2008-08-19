/*
 * $Date$
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

package javax.naming.event;
public class NamingEvent extends java.util.EventObject {
	public NamingEvent(javax.naming.event.EventContext var0, int var1, javax.naming.Binding var2, javax.naming.Binding var3, java.lang.Object var4) { super((java.lang.Object) null); }
	public void dispatch(javax.naming.event.NamingListener var0) { }
	public java.lang.Object getChangeInfo() { return null; }
	public javax.naming.event.EventContext getEventContext() { return null; }
	public javax.naming.Binding getNewBinding() { return null; }
	public javax.naming.Binding getOldBinding() { return null; }
	public int getType() { return 0; }
	public final static int OBJECT_ADDED = 0;
	public final static int OBJECT_CHANGED = 3;
	public final static int OBJECT_REMOVED = 1;
	public final static int OBJECT_RENAMED = 2;
	protected java.lang.Object changeInfo;
	protected javax.naming.Binding newBinding;
	protected javax.naming.Binding oldBinding;
	protected int type;
}

