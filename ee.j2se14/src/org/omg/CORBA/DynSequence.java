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
public abstract interface DynSequence extends org.omg.CORBA.DynAny, org.omg.CORBA.Object {
	public abstract org.omg.CORBA.Any[] get_elements();
	public abstract int length();
	public abstract void length(int var0);
	public abstract void set_elements(org.omg.CORBA.Any[] var0) throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}

