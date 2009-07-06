/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.reflect;

import java.util.List;

/**
 * Metadata for a Bean Manager.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface BeanMetadata extends Target, ComponentMetadata {

	/**
	 * Used when the <code>scope</code> is <code>singleton</code>. See
	 * {@link #getScope()}
	 */

	static final String SCOPE_SINGLETON = "singleton";
	/**
	 * Used when the <code>scope</code> is <code>prototype</code> See
	 * {@link #getScope()}
	 */
	static final String SCOPE_PROTOTYPE = "prototype";

	/**
	 * The name of the class specified for this bean.
	 * 
	 * This is the <code>class</code> attribute.
	 * 
	 * @return the name of the bean class. If no class was specified in the
	 *         definition (because the a factory component is used instead) then
	 *         this method will return <code>null</code>.
	 */
	String getClassName();

	/**
	 * The name of the init method specified for this bean, if any.
	 * 
	 * This is the <code>init-method</code> attribute.
	 * 
	 * @return the method name of the specified init method, or
	 *         <code>null</code> if no init method was specified.
	 */
	String getInitMethod();

	/**
	 * The name of the destroy method specified for this bean, if any.
	 * 
	 * This is the <code>destroy-method</code> attribute.
	 * 
	 * @return the method name of the specified destroy method, or
	 *         <code>null</code> if no destroy method was specified.
	 */
	String getDestroyMethod();

	/**
	 * The arguments for the factory method or constructor.
	 * 
	 * Specified in all the child
	 * <code>argument<code> elements. The return is a list of {@link BeanArgument} objects.
	 * 
	 * @return List of Bean Arguments for the factory method or constructor, can
	 *         be empty if no arguments are specified
	 */
	List/* <BeanArgument> */getArguments();

	/**
	 * The property injection {@link BeanProperty} Metadata for this bean.
	 * 
	 * Specified in all the child <code>property</code> elements.
	 * 
	 * @return an immutable List of {@link BeanProperty}, with one entry for
	 *         each property to be injected. If no property injection was
	 *         specified for this bean then an empty List will be returned.
	 * 
	 */
	List /* <BeanProperty> */getProperties();

	/**
	 * Provides the name of the optional factory method.
	 * 
	 * This is the <code>factory-method</code> attribute.
	 * 
	 * @return The name of the factory method or <code>null</code>.
	 */
	String getFactoryMethod();

	/**
	 * The component instance on which to invoke the factory method (if
	 * specified).
	 * 
	 * The to be used component instance is referred to by the
	 * <code>factory-component</code>.
	 * 
	 * When a factory method and factory ref has been specified for this
	 * component, this operation returns the Metadata specifying the manager on
	 * which component instance the factory method is to be invoked. When no
	 * factory component has been specified this operation will return
	 * <code>null</code>.
	 * 
	 * A return value of <code>null with a <code>non-null</code> factory method
	 * indicates that the factory method should be invoked as a static method on
	 * the given class itself. For a <code>non-null</code> return value, the
	 * Metadata returned will be a {@link Target} instance.
	 * 
	 * @return A {@link Target} or <code>null</code> if no factory component was
	 *         specified.
	 */
	Target getFactoryComponent();

	/**
	 * The specified scope for the manager's lifecycle.
	 * 
	 * @return The scope specified for the component.
	 * 
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	String getScope();
}
