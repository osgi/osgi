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

package org.w3c.dom;
public interface DOMError {
	public final static short SEVERITY_ERROR = 2;
	public final static short SEVERITY_FATAL_ERROR = 3;
	public final static short SEVERITY_WARNING = 1;
	org.w3c.dom.DOMLocator getLocation();
	java.lang.String getMessage();
	java.lang.Object getRelatedData();
	java.lang.Object getRelatedException();
	short getSeverity();
	java.lang.String getType();
}

