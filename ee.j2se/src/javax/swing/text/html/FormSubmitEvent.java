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

package javax.swing.text.html;
public class FormSubmitEvent extends javax.swing.text.html.HTMLFrameHyperlinkEvent {
	public enum MethodType {
		GET,
		POST;
	}
	public java.lang.String getData() { return null; }
	public javax.swing.text.html.FormSubmitEvent.MethodType getMethod() { return null; }
	private FormSubmitEvent()  { super((java.lang.Object) null, (javax.swing.event.HyperlinkEvent.EventType) null, (java.net.URL) null, (javax.swing.text.Element) null, (java.lang.String) null); } /* generated constructor to prevent compiler adding default public constructor */
}

