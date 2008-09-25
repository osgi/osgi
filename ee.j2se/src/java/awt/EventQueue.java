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

package java.awt;
public class EventQueue {
	public EventQueue() { }
	protected void dispatchEvent(java.awt.AWTEvent var0) { }
	public static java.awt.AWTEvent getCurrentEvent() { return null; }
	public static long getMostRecentEventTime() { return 0l; }
	public java.awt.AWTEvent getNextEvent() throws java.lang.InterruptedException { return null; }
	public static void invokeAndWait(java.lang.Runnable var0) throws java.lang.InterruptedException, java.lang.reflect.InvocationTargetException { }
	public static void invokeLater(java.lang.Runnable var0) { }
	public static boolean isDispatchThread() { return false; }
	public java.awt.AWTEvent peekEvent() { return null; }
	public java.awt.AWTEvent peekEvent(int var0) { return null; }
	protected void pop() { }
	public void postEvent(java.awt.AWTEvent var0) { }
	public void push(java.awt.EventQueue var0) { }
}

