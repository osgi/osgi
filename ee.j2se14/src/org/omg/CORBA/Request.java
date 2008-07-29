/*
 * $Date$
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

package org.omg.CORBA;
public abstract class Request {
	public Request() { }
	public abstract org.omg.CORBA.Any add_in_arg();
	public abstract org.omg.CORBA.Any add_inout_arg();
	public abstract org.omg.CORBA.Any add_named_in_arg(java.lang.String var0);
	public abstract org.omg.CORBA.Any add_named_inout_arg(java.lang.String var0);
	public abstract org.omg.CORBA.Any add_named_out_arg(java.lang.String var0);
	public abstract org.omg.CORBA.Any add_out_arg();
	public abstract org.omg.CORBA.NVList arguments();
	public abstract org.omg.CORBA.ContextList contexts();
	public abstract org.omg.CORBA.Context ctx();
	public abstract void ctx(org.omg.CORBA.Context var0);
	public abstract org.omg.CORBA.Environment env();
	public abstract org.omg.CORBA.ExceptionList exceptions();
	public abstract void get_response() throws org.omg.CORBA.WrongTransaction;
	public abstract void invoke();
	public abstract java.lang.String operation();
	public abstract boolean poll_response();
	public abstract org.omg.CORBA.NamedValue result();
	public abstract org.omg.CORBA.Any return_value();
	public abstract void send_deferred();
	public abstract void send_oneway();
	public abstract void set_return_type(org.omg.CORBA.TypeCode var0);
	public abstract org.omg.CORBA.Object target();
}

