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
public class BeanContextChildSupport implements java.beans.beancontext.BeanContextChild, java.beans.beancontext.BeanContextServicesListener, java.io.Serializable {
	public BeanContextChildSupport() { }
	public BeanContextChildSupport(java.beans.beancontext.BeanContextChild var0) { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void addVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1) { }
	public void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public void fireVetoableChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) throws java.beans.PropertyVetoException { }
	public java.beans.beancontext.BeanContext getBeanContext() { return null; }
	public java.beans.beancontext.BeanContextChild getBeanContextChildPeer() { return null; }
	protected void initializeBeanContextResources() { }
	public boolean isDelegated() { return false; }
	protected void releaseBeanContextResources() { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void removeVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1) { }
	public void serviceAvailable(java.beans.beancontext.BeanContextServiceAvailableEvent var0) { }
	public void serviceRevoked(java.beans.beancontext.BeanContextServiceRevokedEvent var0) { }
	public void setBeanContext(java.beans.beancontext.BeanContext var0) throws java.beans.PropertyVetoException { }
	public boolean validatePendingSetBeanContext(java.beans.beancontext.BeanContext var0) { return false; }
	protected java.beans.beancontext.BeanContext beanContext;
	public java.beans.beancontext.BeanContextChild beanContextChildPeer;
	protected java.beans.PropertyChangeSupport pcSupport;
	protected boolean rejectedSetBCOnce;
	protected java.beans.VetoableChangeSupport vcSupport;
}

