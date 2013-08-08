/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.nio.file.attribute;
public enum AclEntryPermission {
	APPEND_DATA,
	DELETE,
	DELETE_CHILD,
	EXECUTE,
	READ_ACL,
	READ_ATTRIBUTES,
	READ_DATA,
	READ_NAMED_ATTRS,
	SYNCHRONIZE,
	WRITE_ACL,
	WRITE_ATTRIBUTES,
	WRITE_DATA,
	WRITE_NAMED_ATTRS,
	WRITE_OWNER;
	public final static java.nio.file.attribute.AclEntryPermission ADD_FILE; static { ADD_FILE = null; }
	public final static java.nio.file.attribute.AclEntryPermission ADD_SUBDIRECTORY; static { ADD_SUBDIRECTORY = null; }
	public final static java.nio.file.attribute.AclEntryPermission LIST_DIRECTORY; static { LIST_DIRECTORY = null; }
}

