package org.osgi.service.resource;

import org.osgi.framework.Bundle;

/**
 * 
 */
public class ResourceContextEvent {

	/**
	 * A new {@link ResourceContext} has been created.
	 * <p>
	 * The {@link ResourceManager#createContext(String, ResourceContext)} method
	 * has been invoked.
	 */
	public static final int	RESOURCE_CONTEXT_CREATED	= 0;

	/**
	 * A {@link ResourceContext} has been deleted
	 * <p>
	 * The {@link ResourceContext#removeContext(ResourceContext)} method has
	 * been invoked
	 */
	public static final int	RESOURCE_CONTEXT_DELETED	= 1;

	/**
	 * A bundle has been added to e {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#addBundle(Bundle)} method has been invoked
	 */
	public static final int	BUNDLE_ADDED				= 2;

	/**
	 * A bundle has been removed from a {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#removeBundle(Bundle, ResourceContext)} method
	 * has been invoked, or the bundle has been uninstalled
	 */
	public static final int	BUNDLE_REMOVED				= 3;

	/**
	 * event type.
	 */
	private int				type;

	/**
	 * bundle.
	 */
	private Bundle			bundle;

	/**
	 * context.
	 */
	private ResourceContext	context;

	/**
	 * Create a new ResourceContextEvent. This constructor should be used when
	 * the type of the event is either {@link #RESOURCE_CONTEXT_CREATED} or {
	 * {@value #RESOURCE_CONTEXT_DELETED}.
	 * 
	 * @param pType event type
	 * @param pResourceContext context
	 */
	public ResourceContextEvent(int pType, ResourceContext pResourceContext) {
		type = pType;
		context = pResourceContext;
	}

	/**
	 * Create a new ResourceContextEvent. This constructor should be used when
	 * the type of the event is either {@link #BUNDLE_ADDED} or
	 * {@link #BUNDLE_REMOVED}.
	 * 
	 * @param pType event type
	 * @param pResourceContext context
	 * @param pBundle bundle
	 */
	public ResourceContextEvent(int pType, ResourceContext pResourceContext, Bundle pBundle) {
		this(pType, pResourceContext);
		bundle = pBundle;
	}


	/**
	 * Retrieves the type of this Resource Context Event.
	 * 
	 * @return the type of the event. One of:
	 *         <ul>
	 *         <li>{@link #RESOURCE_CONTEXT_CREATED}</li>
	 *         <li>{@link #RESOURCE_CONTEXT_DELETED}</li>
	 *         <li>{@link #BUNDLE_ADDED}</li>
	 *         <li>{@link #BUNDLE_REMOVED}</li>
	 *         </ul>
	 * 
	 */
	public int getType() {
		return 0;
	}

	/**
	 * Retrieves the Resource Context associated to this event
	 * 
	 * @return Resource Context.
	 */
	public ResourceContext getContext() {
		return null;
	}

	/**
	 * <p>
	 * Retrieves the Bundle added to or removed from the Resource Context.
	 * </p>
	 * <p>
	 * This method returns a valid value only when {@link #getType()} returns:
	 * <ul>
	 * <li>{@link #BUNDLE_ADDED}</li>
	 * <li>{@link #BUNDLE_REMOVED}</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the bundle or null.
	 */
	public Bundle getBundle() {
		return null;
	}

	public String toString() {
		// TODO
		return super.toString();
	}

	public int hashCode() {
		// TODO
		return super.hashCode();
	}

	public boolean equals(Object var0) {
		// TODO
		return super.equals(var0);
	}

}
