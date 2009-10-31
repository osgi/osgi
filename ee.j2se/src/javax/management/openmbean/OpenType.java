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

package javax.management.openmbean;
public abstract class OpenType<T> implements java.io.Serializable {
	/** @deprecated */
	@java.lang.Deprecated
	public final static java.lang.String[] ALLOWED_CLASSNAMES; static { ALLOWED_CLASSNAMES = null; }
	public final static java.util.List<java.lang.String> ALLOWED_CLASSNAMES_LIST; static { ALLOWED_CLASSNAMES_LIST = null; }
	protected OpenType(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws javax.management.openmbean.OpenDataException { } 
	public abstract boolean equals(java.lang.Object var0);
	public java.lang.String getClassName() { return null; }
	public java.lang.String getDescription() { return null; }
	public java.lang.String getTypeName() { return null; }
	public abstract int hashCode();
	public boolean isArray() { return false; }
	public abstract boolean isValue(java.lang.Object var0);
	public abstract java.lang.String toString();
}

