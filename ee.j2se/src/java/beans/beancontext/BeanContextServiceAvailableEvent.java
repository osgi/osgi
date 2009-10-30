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

package java.beans.beancontext;
public class BeanContextServiceAvailableEvent extends java.beans.beancontext.BeanContextEvent {
	protected java.lang.Class serviceClass;
	public BeanContextServiceAvailableEvent(java.beans.beancontext.BeanContextServices var0, java.lang.Class var1)  { super((java.beans.beancontext.BeanContext) null); } 
	public java.util.Iterator getCurrentServiceSelectors() { return null; }
	public java.lang.Class getServiceClass() { return null; }
	public java.beans.beancontext.BeanContextServices getSourceAsBeanContextServices() { return null; }
}

