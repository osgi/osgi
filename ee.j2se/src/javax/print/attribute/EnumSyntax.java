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

package javax.print.attribute;
public abstract class EnumSyntax implements java.io.Serializable, java.lang.Cloneable {
	protected EnumSyntax(int var0) { } 
	public java.lang.Object clone() { return null; }
	protected javax.print.attribute.EnumSyntax[] getEnumValueTable() { return null; }
	protected int getOffset() { return 0; }
	protected java.lang.String[] getStringTable() { return null; }
	public int getValue() { return 0; }
	public int hashCode() { return 0; }
	protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
}

