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

package javax.management;
public class ImmutableDescriptor implements javax.management.Descriptor {
	public final static javax.management.ImmutableDescriptor EMPTY_DESCRIPTOR; static { EMPTY_DESCRIPTOR = null; }
	public ImmutableDescriptor(java.util.Map<java.lang.String,?> var0) { } 
	public ImmutableDescriptor(java.lang.String... var0) { } 
	public ImmutableDescriptor(java.lang.String[] var0, java.lang.Object[] var1) { } 
	public javax.management.Descriptor clone() { return null; }
	public final java.lang.String[] getFieldNames() { return null; }
	public final java.lang.Object getFieldValue(java.lang.String var0) { return null; }
	public final java.lang.Object[] getFieldValues(java.lang.String... var0) { return null; }
	public final java.lang.String[] getFields() { return null; }
	public boolean isValid() { return false; }
	public final void removeField(java.lang.String var0) { }
	public final void setField(java.lang.String var0, java.lang.Object var1) { }
	public final void setFields(java.lang.String[] var0, java.lang.Object[] var1) { }
	public static javax.management.ImmutableDescriptor union(javax.management.Descriptor... var0) { return null; }
}

