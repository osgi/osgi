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
public interface NamingContextExtOperations extends org.omg.CosNaming.NamingContextOperations {
	org.omg.CORBA.Object resolve_str(java.lang.String var0) throws org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.NotFound;
	org.omg.CosNaming.NameComponent[] to_name(java.lang.String var0) throws org.omg.CosNaming.NamingContextPackage.InvalidName;
	java.lang.String to_string(org.omg.CosNaming.NameComponent[] var0) throws org.omg.CosNaming.NamingContextPackage.InvalidName;
	java.lang.String to_url(java.lang.String var0, java.lang.String var1) throws org.omg.CosNaming.NamingContextExtPackage.InvalidAddress, org.omg.CosNaming.NamingContextPackage.InvalidName;
}

