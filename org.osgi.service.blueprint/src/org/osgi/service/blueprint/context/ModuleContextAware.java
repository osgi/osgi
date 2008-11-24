package org.osgi.service.blueprint.context;

/**
 * If a component implements this interface then the setModuleContext operation
 * will be invoked after the component instance has been instantiated and before
 * the init-method (if specified) has been invoked.
 *
 */
public interface ModuleContextAware {
	
	/**
	 * Set the module context of the module in which the implementor is 
	 * executing.
	 * 
	 * @param context the module context in which the implementor of 
	 * this interface is executing.
	 */
	void setModuleContext(ModuleContext context);
}
