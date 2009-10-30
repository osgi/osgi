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

package org.omg.CosNaming.NamingContextPackage;
public final class NotFound extends org.omg.CORBA.UserException {
	public org.omg.CosNaming.NameComponent[] rest_of_name;
	public org.omg.CosNaming.NamingContextPackage.NotFoundReason why;
	public NotFound() { } 
	public NotFound(java.lang.String var0, org.omg.CosNaming.NamingContextPackage.NotFoundReason var1, org.omg.CosNaming.NameComponent[] var2) { } 
	public NotFound(org.omg.CosNaming.NamingContextPackage.NotFoundReason var0, org.omg.CosNaming.NameComponent[] var1) { } 
}

