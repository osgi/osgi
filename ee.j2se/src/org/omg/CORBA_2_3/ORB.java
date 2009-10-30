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

package org.omg.CORBA_2_3;
public abstract class ORB extends org.omg.CORBA.ORB {
	public ORB() { } 
	public org.omg.CORBA.Object get_value_def(java.lang.String var0) { return null; }
	public org.omg.CORBA.portable.ValueFactory lookup_value_factory(java.lang.String var0) { return null; }
	public org.omg.CORBA.portable.ValueFactory register_value_factory(java.lang.String var0, org.omg.CORBA.portable.ValueFactory var1) { return null; }
	public void set_delegate(java.lang.Object var0) { }
	public void unregister_value_factory(java.lang.String var0) { }
}

