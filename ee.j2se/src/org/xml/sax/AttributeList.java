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

package org.xml.sax;
/** @deprecated */ public interface AttributeList {
	int getLength();
	java.lang.String getName(int var0);
	java.lang.String getType(int var0);
	java.lang.String getType(java.lang.String var0);
	java.lang.String getValue(int var0);
	java.lang.String getValue(java.lang.String var0);
}

