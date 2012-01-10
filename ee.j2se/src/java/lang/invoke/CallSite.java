/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.lang.invoke;
public abstract class CallSite {
	public abstract java.lang.invoke.MethodHandle dynamicInvoker();
	public abstract java.lang.invoke.MethodHandle getTarget();
	public abstract void setTarget(java.lang.invoke.MethodHandle var0);
	public java.lang.invoke.MethodType type() { return null; }
	CallSite() { } /* generated constructor to prevent compiler adding default public constructor */
}

