/*
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

package java.beans.beancontext;
public abstract class BeanContextEvent extends java.util.EventObject {
	protected BeanContextEvent(java.beans.beancontext.BeanContext var0) { super((java.lang.Object) null); }
	public java.beans.beancontext.BeanContext getBeanContext() { return null; }
	public java.beans.beancontext.BeanContext getPropagatedFrom() { return null; }
	public boolean isPropagated() { return false; }
	public void setPropagatedFrom(java.beans.beancontext.BeanContext var0) { }
	protected java.beans.beancontext.BeanContext propagatedFrom;
}

