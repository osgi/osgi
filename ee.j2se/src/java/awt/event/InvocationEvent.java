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
public class InvocationEvent extends java.awt.AWTEvent implements java.awt.ActiveEvent {
	protected InvocationEvent(java.lang.Object var0, int var1, java.lang.Runnable var2, java.lang.Object var3, boolean var4) { super((java.lang.Object) null, 0); }
	public InvocationEvent(java.lang.Object var0, java.lang.Runnable var1) { super((java.lang.Object) null, 0); }
	public InvocationEvent(java.lang.Object var0, java.lang.Runnable var1, java.lang.Object var2, boolean var3) { super((java.lang.Object) null, 0); }
	public void dispatch() { }
	public java.lang.Exception getException() { return null; }
	public long getWhen() { return 0l; }
	public final static int INVOCATION_DEFAULT = 1200;
	public final static int INVOCATION_FIRST = 1200;
	public final static int INVOCATION_LAST = 1200;
	protected boolean catchExceptions;
	protected java.lang.Object notifier;
	protected java.lang.Runnable runnable;
}

