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
public interface BeanContextChild {
	void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1);
	void addVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1);
	java.beans.beancontext.BeanContext getBeanContext();
	void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1);
	void removeVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1);
	void setBeanContext(java.beans.beancontext.BeanContext var0) throws java.beans.PropertyVetoException;
}

