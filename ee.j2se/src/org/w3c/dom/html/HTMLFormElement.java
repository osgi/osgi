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

package org.w3c.dom.html;
public interface HTMLFormElement extends org.w3c.dom.html.HTMLElement {
	java.lang.String getAcceptCharset();
	java.lang.String getAction();
	org.w3c.dom.html.HTMLCollection getElements();
	java.lang.String getEnctype();
	int getLength();
	java.lang.String getMethod();
	java.lang.String getName();
	java.lang.String getTarget();
	void reset();
	void setAcceptCharset(java.lang.String var0);
	void setAction(java.lang.String var0);
	void setEnctype(java.lang.String var0);
	void setMethod(java.lang.String var0);
	void setName(java.lang.String var0);
	void setTarget(java.lang.String var0);
	void submit();
}

