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
public final class AclEntry {
	public static final class Builder {
		public java.nio.file.attribute.AclEntry build() { return null; }
		public java.nio.file.attribute.AclEntry.Builder setFlags(java.util.Set<java.nio.file.attribute.AclEntryFlag> var0) { return null; }
		public java.nio.file.attribute.AclEntry.Builder setFlags(java.nio.file.attribute.AclEntryFlag... var0) { return null; }
		public java.nio.file.attribute.AclEntry.Builder setPermissions(java.util.Set<java.nio.file.attribute.AclEntryPermission> var0) { return null; }
		public java.nio.file.attribute.AclEntry.Builder setPermissions(java.nio.file.attribute.AclEntryPermission... var0) { return null; }
		public java.nio.file.attribute.AclEntry.Builder setPrincipal(java.nio.file.attribute.UserPrincipal var0) { return null; }
		public java.nio.file.attribute.AclEntry.Builder setType(java.nio.file.attribute.AclEntryType var0) { return null; }
		private Builder() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public java.util.Set<java.nio.file.attribute.AclEntryFlag> flags() { return null; }
	public static java.nio.file.attribute.AclEntry.Builder newBuilder() { return null; }
	public static java.nio.file.attribute.AclEntry.Builder newBuilder(java.nio.file.attribute.AclEntry var0) { return null; }
	public java.util.Set<java.nio.file.attribute.AclEntryPermission> permissions() { return null; }
	public java.nio.file.attribute.UserPrincipal principal() { return null; }
	public java.nio.file.attribute.AclEntryType type() { return null; }
	private AclEntry() { } /* generated constructor to prevent compiler adding default public constructor */
}

