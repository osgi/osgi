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
public class BeanContextSupport extends java.beans.beancontext.BeanContextChildSupport implements java.beans.PropertyChangeListener, java.beans.VetoableChangeListener, java.beans.beancontext.BeanContext, java.io.Serializable {
	public BeanContextSupport() { }
	public BeanContextSupport(java.beans.beancontext.BeanContext var0) { }
	public BeanContextSupport(java.beans.beancontext.BeanContext var0, java.util.Locale var1) { }
	public BeanContextSupport(java.beans.beancontext.BeanContext var0, java.util.Locale var1, boolean var2) { }
	public BeanContextSupport(java.beans.beancontext.BeanContext var0, java.util.Locale var1, boolean var2, boolean var3) { }
	public boolean add(java.lang.Object var0) { return false; }
	public boolean addAll(java.util.Collection var0) { return false; }
	public void addBeanContextMembershipListener(java.beans.beancontext.BeanContextMembershipListener var0) { }
	public boolean avoidingGui() { return false; }
	protected java.util.Iterator bcsChildren() { return null; }
	protected void bcsPreDeserializationHook(java.io.ObjectInputStream var0) throws java.io.IOException, java.lang.ClassNotFoundException { }
	protected void bcsPreSerializationHook(java.io.ObjectOutputStream var0) throws java.io.IOException { }
	protected void childDeserializedHook(java.lang.Object var0, java.beans.beancontext.BeanContextSupport.BCSChild var1) { }
	protected void childJustAddedHook(java.lang.Object var0, java.beans.beancontext.BeanContextSupport.BCSChild var1) { }
	protected void childJustRemovedHook(java.lang.Object var0, java.beans.beancontext.BeanContextSupport.BCSChild var1) { }
	protected final static boolean classEquals(java.lang.Class var0, java.lang.Class var1) { return false; }
	public void clear() { }
	public boolean contains(java.lang.Object var0) { return false; }
	public boolean containsAll(java.util.Collection var0) { return false; }
	public boolean containsKey(java.lang.Object var0) { return false; }
	protected final java.lang.Object[] copyChildren() { return null; }
	protected java.beans.beancontext.BeanContextSupport.BCSChild createBCSChild(java.lang.Object var0, java.lang.Object var1) { return null; }
	protected final void deserialize(java.io.ObjectInputStream var0, java.util.Collection var1) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public void dontUseGui() { }
	protected final void fireChildrenAdded(java.beans.beancontext.BeanContextMembershipEvent var0) { }
	protected final void fireChildrenRemoved(java.beans.beancontext.BeanContextMembershipEvent var0) { }
	public java.beans.beancontext.BeanContext getBeanContextPeer() { return null; }
	protected final static java.beans.beancontext.BeanContextChild getChildBeanContextChild(java.lang.Object var0) { return null; }
	protected final static java.beans.beancontext.BeanContextMembershipListener getChildBeanContextMembershipListener(java.lang.Object var0) { return null; }
	protected final static java.beans.PropertyChangeListener getChildPropertyChangeListener(java.lang.Object var0) { return null; }
	protected final static java.io.Serializable getChildSerializable(java.lang.Object var0) { return null; }
	protected final static java.beans.VetoableChangeListener getChildVetoableChangeListener(java.lang.Object var0) { return null; }
	protected final static java.beans.Visibility getChildVisibility(java.lang.Object var0) { return null; }
	public java.util.Locale getLocale() { return null; }
	public java.net.URL getResource(java.lang.String var0, java.beans.beancontext.BeanContextChild var1) { return null; }
	public java.io.InputStream getResourceAsStream(java.lang.String var0, java.beans.beancontext.BeanContextChild var1) { return null; }
	protected void initialize() { }
	public java.lang.Object instantiateChild(java.lang.String var0) throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	public boolean isDesignTime() { return false; }
	public boolean isEmpty() { return false; }
	public boolean isSerializing() { return false; }
	public java.util.Iterator iterator() { return null; }
	public boolean needsGui() { return false; }
	public void okToUseGui() { }
	public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	public final void readChildren(java.io.ObjectInputStream var0) throws java.io.IOException, java.lang.ClassNotFoundException { }
	public boolean remove(java.lang.Object var0) { return false; }
	protected boolean remove(java.lang.Object var0, boolean var1) { return false; }
	public boolean removeAll(java.util.Collection var0) { return false; }
	public void removeBeanContextMembershipListener(java.beans.beancontext.BeanContextMembershipListener var0) { }
	public boolean retainAll(java.util.Collection var0) { return false; }
	protected final void serialize(java.io.ObjectOutputStream var0, java.util.Collection var1) throws java.io.IOException { }
	public void setDesignTime(boolean var0) { }
	public void setLocale(java.util.Locale var0) throws java.beans.PropertyVetoException { }
	public int size() { return 0; }
	public java.lang.Object[] toArray() { return null; }
	public java.lang.Object[] toArray(java.lang.Object[] var0) { return null; }
	protected boolean validatePendingAdd(java.lang.Object var0) { return false; }
	protected boolean validatePendingRemove(java.lang.Object var0) { return false; }
	public void vetoableChange(java.beans.PropertyChangeEvent var0) throws java.beans.PropertyVetoException { }
	public final void writeChildren(java.io.ObjectOutputStream var0) throws java.io.IOException { }
	protected java.util.ArrayList bcmListeners;
	protected java.util.HashMap children;
	protected boolean designTime;
	protected java.util.Locale locale;
	protected boolean okToUseGui;
	protected class BCSChild implements java.io.Serializable {
		BCSChild() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	protected static final class BCSIterator implements java.util.Iterator {
		public boolean hasNext() { return false; }
		public java.lang.Object next() { return null; }
		public void remove() { }
		private BCSIterator() { } /* generated constructor to prevent compiler adding default public constructor */
	}
}

