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
public interface ActivationMonitor extends java.rmi.Remote {
	void activeObject(java.rmi.activation.ActivationID var0, java.rmi.MarshalledObject var1) throws java.rmi.RemoteException, java.rmi.activation.UnknownObjectException;
	void inactiveGroup(java.rmi.activation.ActivationGroupID var0, long var1) throws java.rmi.RemoteException, java.rmi.activation.UnknownGroupException;
	void inactiveObject(java.rmi.activation.ActivationID var0) throws java.rmi.RemoteException, java.rmi.activation.UnknownObjectException;
}

