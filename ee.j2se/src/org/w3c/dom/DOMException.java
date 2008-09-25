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

package org.w3c.dom;
public class DOMException extends java.lang.RuntimeException {
	public DOMException(short var0, java.lang.String var1) { }
	public final static short DOMSTRING_SIZE_ERR = 2;
	public final static short HIERARCHY_REQUEST_ERR = 3;
	public final static short INDEX_SIZE_ERR = 1;
	public final static short INUSE_ATTRIBUTE_ERR = 10;
	public final static short INVALID_ACCESS_ERR = 15;
	public final static short INVALID_CHARACTER_ERR = 5;
	public final static short INVALID_MODIFICATION_ERR = 13;
	public final static short INVALID_STATE_ERR = 11;
	public final static short NAMESPACE_ERR = 14;
	public final static short NOT_FOUND_ERR = 8;
	public final static short NOT_SUPPORTED_ERR = 9;
	public final static short NO_DATA_ALLOWED_ERR = 6;
	public final static short NO_MODIFICATION_ALLOWED_ERR = 7;
	public final static short SYNTAX_ERR = 12;
	public final static short WRONG_DOCUMENT_ERR = 4;
	public short code;
}

