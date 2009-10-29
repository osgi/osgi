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

package javax.swing.event;
public class EventListenerList implements java.io.Serializable {
	public EventListenerList() { }
	public void add(java.lang.Class var0, java.util.EventListener var1) { }
	public int getListenerCount() { return 0; }
	public int getListenerCount(java.lang.Class var0) { return 0; }
	public java.lang.Object[] getListenerList() { return null; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public void remove(java.lang.Class var0, java.util.EventListener var1) { }
	protected java.lang.Object[] listenerList;
}

