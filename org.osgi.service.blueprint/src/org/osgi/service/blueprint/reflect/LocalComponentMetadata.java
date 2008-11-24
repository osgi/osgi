package org.osgi.service.blueprint.reflect;

import java.util.Collection;


/**
 * Metadata for a component defined locally with a module context.
 *
 */
public interface LocalComponentMetadata extends ComponentMetadata {
	
	static final String SCOPE_SINGLETON = "singleton";
	static final String SCOPE_PROTOTYPE = "prototype";
	static final String SCOPE_BUNDLE = "bundle";

	/**
	 * The name of the class type specified for this component.
	 * 
	 * @return the name of the component class. If no class was specified
	 * in the component definition (because the a factory component is used
	 * instead) then this method will return null.
	 */
	String getClassName();
	
	/**
	 * The name of the init method specified for this component, if any.
	 *  
	 * @return the method name of the specified init method, or null if 
	 * no init method was specified.
	 */
	String getInitMethodName();
	
	/**
	 * The name of the destroy method specified for this component, if any.
	 * 
	 * @return the method name of the specified destroy method, or null if no
	 * destroy method was specified.
	 */
	String getDestroyMethodName();
	
	/**
	 * The constructor injection metadata for this component.
	 * 	
	 * @return the constructor injection metadata. This is guaranteed to be
	 * non-null and will refer to the default constructor if no explicit
	 * constructor injection was specified for the component.
	 */
	ConstructorInjectionMetadata getConstructorInjectionMetadata();
	
	/**
	 * The property injection metadata for this component.
	 * 
	 * @return an immutable collection of PropertyInjectionMetadata, with one entry for each property to be injected. If
	 * no property injection was specified for this component then an empty collection
	 * will be returned.
	 * 
	 */
	Collection /*<PropertyInjectionMetadata>*/ getPropertyInjectionMetadata();
	
	/**
	 * Is this component to be lazily instantiated?
	 * 
	 * @return true, iff this component definition specifies lazy
	 * instantiation.
	 */
	boolean isLazy();
	
	/**
	 * The metadata describing how to create the component instance by invoking a 
	 * method (as opposed to a constructor) if factory methods are used.
	 * 
	 * @return the method injection metadata for the specified factory method, or null if no
	 * factory method is used for this component.
	 */
	MethodInjectionMetadata getFactoryMethodMetadata();
	
	/**
	 * The component instance on which to invoke the factory method (if specified).
	 * 
	 * @return when a factory method and factory component has been specified for this
	 * component, this operation returns the metadata specifying the component on which
	 * the factory method is to be invoked. When no factory component has been specified
	 * this operation will return null. A return value of null with a non-null factory method
	 * indicates that the factory method should be invoked as a static method on the 
	 * component class itself.
	 */
	ComponentMetadata getFactoryComponent();
	
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
}
