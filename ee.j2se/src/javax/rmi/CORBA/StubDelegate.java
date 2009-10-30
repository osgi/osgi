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

package javax.rmi.CORBA;
public interface StubDelegate {
	void connect(javax.rmi.CORBA.Stub var0, org.omg.CORBA.ORB var1) throws java.rmi.RemoteException;
	boolean equals(javax.rmi.CORBA.Stub var0, java.lang.Object var1);
	int hashCode(javax.rmi.CORBA.Stub var0);
	void readObject(javax.rmi.CORBA.Stub var0, java.io.ObjectInputStream var1) throws java.io.IOException, java.lang.ClassNotFoundException;
	java.lang.String toString(javax.rmi.CORBA.Stub var0);
	void writeObject(javax.rmi.CORBA.Stub var0, java.io.ObjectOutputStream var1) throws java.io.IOException;
}

