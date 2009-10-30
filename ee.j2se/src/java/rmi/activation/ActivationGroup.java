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

package java.rmi.activation;
public abstract class ActivationGroup extends java.rmi.server.UnicastRemoteObject implements java.rmi.activation.ActivationInstantiator {
	protected ActivationGroup(java.rmi.activation.ActivationGroupID var0) throws java.rmi.RemoteException { } 
	protected void activeObject(java.rmi.activation.ActivationID var0, java.rmi.MarshalledObject var1) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { }
	public abstract void activeObject(java.rmi.activation.ActivationID var0, java.rmi.Remote var1) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public static java.rmi.activation.ActivationGroup createGroup(java.rmi.activation.ActivationGroupID var0, java.rmi.activation.ActivationGroupDesc var1, long var2) throws java.rmi.activation.ActivationException { return null; }
	public static java.rmi.activation.ActivationGroupID currentGroupID() { return null; }
	public static java.rmi.activation.ActivationSystem getSystem() throws java.rmi.activation.ActivationException { return null; }
	protected void inactiveGroup() throws java.rmi.RemoteException, java.rmi.activation.UnknownGroupException { }
	public boolean inactiveObject(java.rmi.activation.ActivationID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException { return false; }
	public static void setSystem(java.rmi.activation.ActivationSystem var0) throws java.rmi.activation.ActivationException { }
}

