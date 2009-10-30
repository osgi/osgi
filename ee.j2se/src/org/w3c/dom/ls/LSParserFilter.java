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

package org.w3c.dom.ls;
public interface LSParserFilter {
	public final static short FILTER_ACCEPT = 1;
	public final static short FILTER_INTERRUPT = 4;
	public final static short FILTER_REJECT = 2;
	public final static short FILTER_SKIP = 3;
	short acceptNode(org.w3c.dom.Node var0);
	int getWhatToShow();
	short startElement(org.w3c.dom.Element var0);
}

