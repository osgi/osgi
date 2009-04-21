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
 * Metadata for a Bean Component.
 *
 * This class describes a <code>bean</code> element.
 */
public interface BeanMetadata extends Target, ComponentMetadata {

	static final String SCOPE_SINGLETON = "singleton";
	static final String SCOPE_PROTOTYPE = "prototype";
	static final String SCOPE_BUNDLE = "bundle";

	/**
	 * The name of the class type specified for this component.
	 *
	 * This is the <code>class</code> attribute.
	 *
	 * @return the name of the component class. If no class was specified in the
	 *         component definition (because the a factory component is used
	 *         instead) then this method will return null.
	 */
	String getClassName();

	/**
	 * The name of the init method specified for this component, if any.
	 *
	 * This is the <code>init-method</code> attribute.
	 *
	 * @return the method name of the specified init method, or null if no init
	 *         method was specified.
	 */
	String getInitMethodName();

	/**
	 * The name of the destroy method specified for this component, if any.
	 *
	 * This is the <code>destroy-method</code> attribute.
	 *
	 * @return the method name of the specified destroy method, or null if no
	 *         destroy method was specified.
	 */
	String getDestroyMethodName();

	/**
	 * The arguments for the factory method or constructor.
	 *
	 * Specified in all the child <code>argument<code> elements.
	 *
	 * @return The metadata for the factory method or constructor. Can be empty if no arguments are specified
	 */
	List/* <BeanArgument> */getArguments();

	/**
	 * The property injection metadata for this component.
	 *
	 * Specified in all the child <code>property</code> elements.
	 *
	 * @return an immutable collection of BeanProperty, with one
	 *         entry for each property to be injected. If no property injection
	 *         was specified for this component then an empty collection will be
	 *         returned.
	 *
	 */
	List /* <BeanProperty> */getProperties();

	/**
	 * Is this component to be lazily instantiated?
	 *
	 * ### I assume the default is processed here?
	 *
	 * This is the <code>lazy-init</code> attribute or the
	 * <code>default-lazy-init</code> in the <code>blueprint</code> element
	 * if not set. ### I've renamed this from isLazy
	 *
	 * @return true, iff this component definition specifies lazy instantiation.
	 */
	boolean isLazyInit();

	/**
	 * Provides the name of the optional factory method.
	 *
	 * This is the <code>factory-method</code> attribute.
	 *
	 * @return The name of the factory method or <code>null</code>.
	 */
	String getFactoryMethodName();

	/**
	 * The component instance on which to invoke the factory method (if
	 * specified).
	 *
	 * The component is defined in the <code>factory-component</code>.
	 *
	 * @return when a factory method and factory component has been specified
	 *         for this component, this operation returns the metadata
	 *         specifying the component on which the factory method is to be
	 *         invoked. When no factory component has been specified this
	 *         operation will return null. A return value of null with a
	 *         non-null factory method indicates that the factory method should
	 *         be invoked as a static method on the component class itself. For
	 *         a non-null return value, the Metadata object returned will be a
	 *         RefMetadata or a TargetComponentMetadata instance.
	 */
	Target getFactoryComponent();

	/**
	 * The specified scope for the component lifecycle.
	 *
	 * @return a String indicating the scope specified for the component.
	 *
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 * @see #SCOPE_BUNDLE
	 */
	String getScope();

	/**
	 * Provide an actual class, this overrides the class name if set. This is
	 * useful for Namespace Handler services that do not want to force the
	 * Blueprint bundle to import implementation classes.
	 *
	 * @return Return the class to use in runtime or <code>null</code>.
	 */

	Class /* <?> */getRuntimeClass();

    /**
     * The names of any components listed in a "depends-on" attribute for this
     * component.
     *
     * @return an immutable List of component names for components that we have explicitly
     * declared a dependency on, or an empty set if none.
     */
    List/*<String>*/ getExplicitDependencies();
}
