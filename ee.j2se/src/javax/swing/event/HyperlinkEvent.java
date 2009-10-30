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
public class HyperlinkEvent extends java.util.EventObject {
	public static final class EventType {
		public final static javax.swing.event.HyperlinkEvent.EventType ACTIVATED; static { ACTIVATED = null; }
		public final static javax.swing.event.HyperlinkEvent.EventType ENTERED; static { ENTERED = null; }
		public final static javax.swing.event.HyperlinkEvent.EventType EXITED; static { EXITED = null; }
		private EventType() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public HyperlinkEvent(java.lang.Object var0, javax.swing.event.HyperlinkEvent.EventType var1, java.net.URL var2)  { super((java.lang.Object) null); } 
	public HyperlinkEvent(java.lang.Object var0, javax.swing.event.HyperlinkEvent.EventType var1, java.net.URL var2, java.lang.String var3)  { super((java.lang.Object) null); } 
	public HyperlinkEvent(java.lang.Object var0, javax.swing.event.HyperlinkEvent.EventType var1, java.net.URL var2, java.lang.String var3, javax.swing.text.Element var4)  { super((java.lang.Object) null); } 
	public java.lang.String getDescription() { return null; }
	public javax.swing.event.HyperlinkEvent.EventType getEventType() { return null; }
	public javax.swing.text.Element getSourceElement() { return null; }
	public java.net.URL getURL() { return null; }
}

