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

package org.omg.PortableServer;
public interface POAOperations {
	byte[] activate_object(org.omg.PortableServer.Servant var0) throws org.omg.PortableServer.POAPackage.ServantAlreadyActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	void activate_object_with_id(byte[] var0, org.omg.PortableServer.Servant var1) throws org.omg.PortableServer.POAPackage.ObjectAlreadyActive, org.omg.PortableServer.POAPackage.ServantAlreadyActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.PortableServer.POA create_POA(java.lang.String var0, org.omg.PortableServer.POAManager var1, org.omg.CORBA.Policy[] var2) throws org.omg.PortableServer.POAPackage.AdapterAlreadyExists, org.omg.PortableServer.POAPackage.InvalidPolicy;
	org.omg.PortableServer.IdAssignmentPolicy create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue var0);
	org.omg.PortableServer.IdUniquenessPolicy create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue var0);
	org.omg.PortableServer.ImplicitActivationPolicy create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue var0);
	org.omg.PortableServer.LifespanPolicy create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue var0);
	org.omg.CORBA.Object create_reference(java.lang.String var0) throws org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.CORBA.Object create_reference_with_id(byte[] var0, java.lang.String var1);
	org.omg.PortableServer.RequestProcessingPolicy create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue var0);
	org.omg.PortableServer.ServantRetentionPolicy create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue var0);
	org.omg.PortableServer.ThreadPolicy create_thread_policy(org.omg.PortableServer.ThreadPolicyValue var0);
	void deactivate_object(byte[] var0) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	void destroy(boolean var0, boolean var1);
	org.omg.PortableServer.POA find_POA(java.lang.String var0, boolean var1) throws org.omg.PortableServer.POAPackage.AdapterNonExistent;
	org.omg.PortableServer.Servant get_servant() throws org.omg.PortableServer.POAPackage.NoServant, org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.PortableServer.ServantManager get_servant_manager() throws org.omg.PortableServer.POAPackage.WrongPolicy;
	byte[] id();
	org.omg.CORBA.Object id_to_reference(byte[] var0) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.PortableServer.Servant id_to_servant(byte[] var0) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	byte[] reference_to_id(org.omg.CORBA.Object var0) throws org.omg.PortableServer.POAPackage.WrongAdapter, org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.PortableServer.Servant reference_to_servant(org.omg.CORBA.Object var0) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongAdapter, org.omg.PortableServer.POAPackage.WrongPolicy;
	byte[] servant_to_id(org.omg.PortableServer.Servant var0) throws org.omg.PortableServer.POAPackage.ServantNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.CORBA.Object servant_to_reference(org.omg.PortableServer.Servant var0) throws org.omg.PortableServer.POAPackage.ServantNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;
	void set_servant(org.omg.PortableServer.Servant var0) throws org.omg.PortableServer.POAPackage.WrongPolicy;
	void set_servant_manager(org.omg.PortableServer.ServantManager var0) throws org.omg.PortableServer.POAPackage.WrongPolicy;
	org.omg.PortableServer.POAManager the_POAManager();
	org.omg.PortableServer.AdapterActivator the_activator();
	void the_activator(org.omg.PortableServer.AdapterActivator var0);
	org.omg.PortableServer.POA[] the_children();
	java.lang.String the_name();
	org.omg.PortableServer.POA the_parent();
}

