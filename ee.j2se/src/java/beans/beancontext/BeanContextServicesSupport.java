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
public class BeanContextServicesSupport extends java.beans.beancontext.BeanContextSupport implements java.beans.beancontext.BeanContextServices {
	public BeanContextServicesSupport() { }
	public BeanContextServicesSupport(java.beans.beancontext.BeanContextServices var0) { }
	public BeanContextServicesSupport(java.beans.beancontext.BeanContextServices var0, java.util.Locale var1) { }
	public BeanContextServicesSupport(java.beans.beancontext.BeanContextServices var0, java.util.Locale var1, boolean var2) { }
	public BeanContextServicesSupport(java.beans.beancontext.BeanContextServices var0, java.util.Locale var1, boolean var2, boolean var3) { }
	public void addBeanContextServicesListener(java.beans.beancontext.BeanContextServicesListener var0) { }
	public boolean addService(java.lang.Class var0, java.beans.beancontext.BeanContextServiceProvider var1) { return false; }
	protected boolean addService(java.lang.Class var0, java.beans.beancontext.BeanContextServiceProvider var1, boolean var2) { return false; }
	protected void bcsPreDeserializationHook(java.io.ObjectInputStream var0) throws java.io.IOException, java.lang.ClassNotFoundException { }
	protected void bcsPreSerializationHook(java.io.ObjectOutputStream var0) throws java.io.IOException { }
	protected java.beans.beancontext.BeanContextServicesSupport.BCSSServiceProvider createBCSSServiceProvider(java.lang.Class var0, java.beans.beancontext.BeanContextServiceProvider var1) { return null; }
	protected final void fireServiceAdded(java.beans.beancontext.BeanContextServiceAvailableEvent var0) { }
	protected final void fireServiceAdded(java.lang.Class var0) { }
	protected final void fireServiceRevoked(java.beans.beancontext.BeanContextServiceRevokedEvent var0) { }
	protected final void fireServiceRevoked(java.lang.Class var0, boolean var1) { }
	public java.beans.beancontext.BeanContextServices getBeanContextServicesPeer() { return null; }
	protected final static java.beans.beancontext.BeanContextServicesListener getChildBeanContextServicesListener(java.lang.Object var0) { return null; }
	public java.util.Iterator getCurrentServiceClasses() { return null; }
	public java.util.Iterator getCurrentServiceSelectors(java.lang.Class var0) { return null; }
	public java.lang.Object getService(java.beans.beancontext.BeanContextChild var0, java.lang.Object var1, java.lang.Class var2, java.lang.Object var3, java.beans.beancontext.BeanContextServiceRevokedListener var4) throws java.util.TooManyListenersException { return null; }
	public boolean hasService(java.lang.Class var0) { return false; }
	public void initialize() { }
	protected void initializeBeanContextResources() { }
	protected void releaseBeanContextResources() { }
	public void releaseService(java.beans.beancontext.BeanContextChild var0, java.lang.Object var1, java.lang.Object var2) { }
	public void removeBeanContextServicesListener(java.beans.beancontext.BeanContextServicesListener var0) { }
	public void revokeService(java.lang.Class var0, java.beans.beancontext.BeanContextServiceProvider var1, boolean var2) { }
	protected java.util.ArrayList bcsListeners;
	protected java.beans.beancontext.BeanContextServicesSupport.BCSSProxyServiceProvider proxy;
	protected int serializable;
	protected java.util.HashMap services;
	protected class BCSSChild extends java.beans.beancontext.BeanContextSupport.BCSChild {
		private BCSSChild() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	protected class BCSSProxyServiceProvider implements java.beans.beancontext.BeanContextServiceProvider, java.beans.beancontext.BeanContextServiceRevokedListener {
		public java.util.Iterator getCurrentServiceSelectors(java.beans.beancontext.BeanContextServices var0, java.lang.Class var1) { return null; }
		public java.lang.Object getService(java.beans.beancontext.BeanContextServices var0, java.lang.Object var1, java.lang.Class var2, java.lang.Object var3) { return null; }
		public void releaseService(java.beans.beancontext.BeanContextServices var0, java.lang.Object var1, java.lang.Object var2) { }
		public void serviceRevoked(java.beans.beancontext.BeanContextServiceRevokedEvent var0) { }
		private BCSSProxyServiceProvider() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	protected static class BCSSServiceProvider implements java.io.Serializable {
		protected java.beans.beancontext.BeanContextServiceProvider getServiceProvider() { return null; }
		protected java.beans.beancontext.BeanContextServiceProvider serviceProvider;
		private BCSSServiceProvider() { } /* generated constructor to prevent compiler adding default public constructor */
	}
}

