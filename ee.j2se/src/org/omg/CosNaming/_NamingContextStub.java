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
public class _NamingContextStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CosNaming.NamingContext {
	public _NamingContextStub() { } 
	public java.lang.String[] _ids() { return null; }
	public void bind(org.omg.CosNaming.NameComponent[] var0, org.omg.CORBA.Object var1) throws org.omg.CosNaming.NamingContextPackage.AlreadyBound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { }
	public void bind_context(org.omg.CosNaming.NameComponent[] var0, org.omg.CosNaming.NamingContext var1) throws org.omg.CosNaming.NamingContextPackage.AlreadyBound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { }
	public org.omg.CosNaming.NamingContext bind_new_context(org.omg.CosNaming.NameComponent[] var0) throws org.omg.CosNaming.NamingContextPackage.AlreadyBound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { return null; }
	public void destroy() throws org.omg.CosNaming.NamingContextPackage.NotEmpty { }
	public void list(int var0, org.omg.CosNaming.BindingListHolder var1, org.omg.CosNaming.BindingIteratorHolder var2) { }
	public org.omg.CosNaming.NamingContext new_context() { return null; }
	public void rebind(org.omg.CosNaming.NameComponent[] var0, org.omg.CORBA.Object var1) throws org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { }
	public void rebind_context(org.omg.CosNaming.NameComponent[] var0, org.omg.CosNaming.NamingContext var1) throws org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { }
	public org.omg.CORBA.Object resolve(org.omg.CosNaming.NameComponent[] var0) throws org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { return null; }
	public void unbind(org.omg.CosNaming.NameComponent[] var0) throws org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound { }
}

