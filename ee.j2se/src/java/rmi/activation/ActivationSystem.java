/*
 * $Revision$
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

package java.rmi.activation;
public abstract interface ActivationSystem extends java.rmi.Remote {
	public abstract java.rmi.activation.ActivationMonitor activeGroup(java.rmi.activation.ActivationGroupID var0, java.rmi.activation.ActivationInstantiator var1, long var2) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract java.rmi.activation.ActivationDesc getActivationDesc(java.rmi.activation.ActivationID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract java.rmi.activation.ActivationGroupDesc getActivationGroupDesc(java.rmi.activation.ActivationGroupID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract java.rmi.activation.ActivationGroupID registerGroup(java.rmi.activation.ActivationGroupDesc var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract java.rmi.activation.ActivationID registerObject(java.rmi.activation.ActivationDesc var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract java.rmi.activation.ActivationDesc setActivationDesc(java.rmi.activation.ActivationID var0, java.rmi.activation.ActivationDesc var1) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract java.rmi.activation.ActivationGroupDesc setActivationGroupDesc(java.rmi.activation.ActivationGroupID var0, java.rmi.activation.ActivationGroupDesc var1) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract void shutdown() throws java.rmi.RemoteException;
	public abstract void unregisterGroup(java.rmi.activation.ActivationGroupID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public abstract void unregisterObject(java.rmi.activation.ActivationID var0) throws java.rmi.RemoteException, java.rmi.activation.ActivationException;
	public final static int SYSTEM_PORT = 1098;
}

