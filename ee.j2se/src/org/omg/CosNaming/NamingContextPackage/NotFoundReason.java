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
public class NotFoundReason implements org.omg.CORBA.portable.IDLEntity {
	public final static int _missing_node = 0;
	public final static int _not_context = 1;
	public final static int _not_object = 2;
	public final static org.omg.CosNaming.NamingContextPackage.NotFoundReason missing_node; static { missing_node = null; }
	public final static org.omg.CosNaming.NamingContextPackage.NotFoundReason not_context; static { not_context = null; }
	public final static org.omg.CosNaming.NamingContextPackage.NotFoundReason not_object; static { not_object = null; }
	protected NotFoundReason(int var0) { } 
	public static org.omg.CosNaming.NamingContextPackage.NotFoundReason from_int(int var0) { return null; }
	public int value() { return 0; }
}

