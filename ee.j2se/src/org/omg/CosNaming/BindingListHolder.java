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

package org.omg.CosNaming;
public final class BindingListHolder implements org.omg.CORBA.portable.Streamable {
	public org.omg.CosNaming.Binding[] value;
	public BindingListHolder() { } 
	public BindingListHolder(org.omg.CosNaming.Binding[] var0) { } 
	public void _read(org.omg.CORBA.portable.InputStream var0) { }
	public org.omg.CORBA.TypeCode _type() { return null; }
	public void _write(org.omg.CORBA.portable.OutputStream var0) { }
}

