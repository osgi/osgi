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

package javax.naming.event;
public class NamingExceptionEvent extends java.util.EventObject {
	public NamingExceptionEvent(javax.naming.event.EventContext var0, javax.naming.NamingException var1) { super((java.lang.Object) null); }
	public void dispatch(javax.naming.event.NamingListener var0) { }
	public javax.naming.event.EventContext getEventContext() { return null; }
	public javax.naming.NamingException getException() { return null; }
}

