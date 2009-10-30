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
public interface BeanContextServices extends java.beans.beancontext.BeanContext, java.beans.beancontext.BeanContextServicesListener {
	void addBeanContextServicesListener(java.beans.beancontext.BeanContextServicesListener var0);
	boolean addService(java.lang.Class var0, java.beans.beancontext.BeanContextServiceProvider var1);
	java.util.Iterator getCurrentServiceClasses();
	java.util.Iterator getCurrentServiceSelectors(java.lang.Class var0);
	java.lang.Object getService(java.beans.beancontext.BeanContextChild var0, java.lang.Object var1, java.lang.Class var2, java.lang.Object var3, java.beans.beancontext.BeanContextServiceRevokedListener var4) throws java.util.TooManyListenersException;
	boolean hasService(java.lang.Class var0);
	void releaseService(java.beans.beancontext.BeanContextChild var0, java.lang.Object var1, java.lang.Object var2);
	void removeBeanContextServicesListener(java.beans.beancontext.BeanContextServicesListener var0);
	void revokeService(java.lang.Class var0, java.beans.beancontext.BeanContextServiceProvider var1, boolean var2);
}

