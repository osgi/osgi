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

package org.omg.CORBA;
public abstract class ORB {
	public ORB() { } 
	public void connect(org.omg.CORBA.Object var0) { }
	public org.omg.CORBA.TypeCode create_abstract_interface_tc(java.lang.String var0, java.lang.String var1) { return null; }
	public abstract org.omg.CORBA.TypeCode create_alias_tc(java.lang.String var0, java.lang.String var1, org.omg.CORBA.TypeCode var2);
	public abstract org.omg.CORBA.Any create_any();
	public abstract org.omg.CORBA.TypeCode create_array_tc(int var0, org.omg.CORBA.TypeCode var1);
	/** @deprecated */ public org.omg.CORBA.DynAny create_basic_dyn_any(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode { return null; }
	public abstract org.omg.CORBA.ContextList create_context_list();
	/** @deprecated */ public org.omg.CORBA.DynAny create_dyn_any(org.omg.CORBA.Any var0) { return null; }
	/** @deprecated */ public org.omg.CORBA.DynArray create_dyn_array(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode { return null; }
	/** @deprecated */ public org.omg.CORBA.DynEnum create_dyn_enum(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode { return null; }
	/** @deprecated */ public org.omg.CORBA.DynSequence create_dyn_sequence(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode { return null; }
	/** @deprecated */ public org.omg.CORBA.DynStruct create_dyn_struct(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode { return null; }
	/** @deprecated */ public org.omg.CORBA.DynUnion create_dyn_union(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.ORBPackage.InconsistentTypeCode { return null; }
	public abstract org.omg.CORBA.TypeCode create_enum_tc(java.lang.String var0, java.lang.String var1, java.lang.String[] var2);
	public abstract org.omg.CORBA.Environment create_environment();
	public abstract org.omg.CORBA.ExceptionList create_exception_list();
	public abstract org.omg.CORBA.TypeCode create_exception_tc(java.lang.String var0, java.lang.String var1, org.omg.CORBA.StructMember[] var2);
	public org.omg.CORBA.TypeCode create_fixed_tc(short var0, short var1) { return null; }
	public abstract org.omg.CORBA.TypeCode create_interface_tc(java.lang.String var0, java.lang.String var1);
	public abstract org.omg.CORBA.NVList create_list(int var0);
	public abstract org.omg.CORBA.NamedValue create_named_value(java.lang.String var0, org.omg.CORBA.Any var1, int var2);
	public org.omg.CORBA.TypeCode create_native_tc(java.lang.String var0, java.lang.String var1) { return null; }
	public org.omg.CORBA.NVList create_operation_list(org.omg.CORBA.Object var0) { return null; }
	public abstract org.omg.CORBA.portable.OutputStream create_output_stream();
	public org.omg.CORBA.Policy create_policy(int var0, org.omg.CORBA.Any var1) throws org.omg.CORBA.PolicyError { return null; }
	/** @deprecated */ public abstract org.omg.CORBA.TypeCode create_recursive_sequence_tc(int var0, int var1);
	public org.omg.CORBA.TypeCode create_recursive_tc(java.lang.String var0) { return null; }
	public abstract org.omg.CORBA.TypeCode create_sequence_tc(int var0, org.omg.CORBA.TypeCode var1);
	public abstract org.omg.CORBA.TypeCode create_string_tc(int var0);
	public abstract org.omg.CORBA.TypeCode create_struct_tc(java.lang.String var0, java.lang.String var1, org.omg.CORBA.StructMember[] var2);
	public abstract org.omg.CORBA.TypeCode create_union_tc(java.lang.String var0, java.lang.String var1, org.omg.CORBA.TypeCode var2, org.omg.CORBA.UnionMember[] var3);
	public org.omg.CORBA.TypeCode create_value_box_tc(java.lang.String var0, java.lang.String var1, org.omg.CORBA.TypeCode var2) { return null; }
	public org.omg.CORBA.TypeCode create_value_tc(java.lang.String var0, java.lang.String var1, short var2, org.omg.CORBA.TypeCode var3, org.omg.CORBA.ValueMember[] var4) { return null; }
	public abstract org.omg.CORBA.TypeCode create_wstring_tc(int var0);
	public void destroy() { }
	public void disconnect(org.omg.CORBA.Object var0) { }
	/** @deprecated */ public org.omg.CORBA.Current get_current() { return null; }
	public abstract org.omg.CORBA.Context get_default_context();
	public abstract org.omg.CORBA.Request get_next_response() throws org.omg.CORBA.WrongTransaction;
	public abstract org.omg.CORBA.TypeCode get_primitive_tc(org.omg.CORBA.TCKind var0);
	public boolean get_service_information(short var0, org.omg.CORBA.ServiceInformationHolder var1) { return false; }
	public static org.omg.CORBA.ORB init() { return null; }
	public static org.omg.CORBA.ORB init(java.applet.Applet var0, java.util.Properties var1) { return null; }
	public static org.omg.CORBA.ORB init(java.lang.String[] var0, java.util.Properties var1) { return null; }
	public abstract java.lang.String[] list_initial_services();
	public abstract java.lang.String object_to_string(org.omg.CORBA.Object var0);
	public void perform_work() { }
	public abstract boolean poll_next_response();
	public abstract org.omg.CORBA.Object resolve_initial_references(java.lang.String var0) throws org.omg.CORBA.ORBPackage.InvalidName;
	public void run() { }
	public abstract void send_multiple_requests_deferred(org.omg.CORBA.Request[] var0);
	public abstract void send_multiple_requests_oneway(org.omg.CORBA.Request[] var0);
	protected abstract void set_parameters(java.applet.Applet var0, java.util.Properties var1);
	protected abstract void set_parameters(java.lang.String[] var0, java.util.Properties var1);
	public void shutdown(boolean var0) { }
	public abstract org.omg.CORBA.Object string_to_object(java.lang.String var0);
	public boolean work_pending() { return false; }
}

