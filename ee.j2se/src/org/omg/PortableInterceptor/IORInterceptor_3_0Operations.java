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

package org.omg.PortableInterceptor;
public interface IORInterceptor_3_0Operations extends org.omg.PortableInterceptor.IORInterceptorOperations {
	void adapter_manager_state_changed(int var0, short var1);
	void adapter_state_changed(org.omg.PortableInterceptor.ObjectReferenceTemplate[] var0, short var1);
	void components_established(org.omg.PortableInterceptor.IORInfo var0);
}

