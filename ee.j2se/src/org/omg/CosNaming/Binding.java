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
public final class Binding implements org.omg.CORBA.portable.IDLEntity {
	public org.omg.CosNaming.NameComponent[] binding_name;
	public org.omg.CosNaming.BindingType binding_type;
	public Binding() { } 
	public Binding(org.omg.CosNaming.NameComponent[] var0, org.omg.CosNaming.BindingType var1) { } 
}

