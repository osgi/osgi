package org.osgi.service.resourcemanagement;


/**
 * 
 * @Immutable
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
	 * The {@link ResourceContext#addBundle(long)} method has been invoked
	 */
	public static final int	BUNDLE_ADDED				= 2;

	/**
	 * A bundle has been removed from a {@link ResourceContext}
	 * <p>
	 * {@link ResourceContext#removeBundle(long)} method or
	 * {@link ResourceContext#removeBundle(long, ResourceContext)} method have
	 * been invoked, or the bundle has been uninstalled
	 */
	public static final int	BUNDLE_REMOVED				= 3;

	/**
	 * event type.
	 */
	private final int				type;

	/**
	 * bundle.
	 */
	private final long		bundleId;

	/**
	 * context.
	 */
	private final ResourceContext	context;

	/**
	 * Create a new ResourceContextEvent. This constructor should be used when
	 * the type of the event is either {@link #RESOURCE_CONTEXT_CREATED} or {
	 * {@value #RESOURCE_CONTEXT_DELETED}.
	 * 
	 * @param pType event type
	 * @param pResourceContext context
	 */
	public ResourceContextEvent(final int pType, final ResourceContext pResourceContext) {
		type = pType;
		context = pResourceContext;
		bundleId = -1;
	}

	/**
	 * Create a new ResourceContextEvent. This constructor should be used when
	 * the type of the event is either {@link #BUNDLE_ADDED} or
	 * {@link #BUNDLE_REMOVED}.
	 * 
	 * @param pType event type
	 * @param pResourceContext context
	 * @param pBundleId bundle
	 */
	public ResourceContextEvent(final int pType, final ResourceContext pResourceContext, final long pBundleId) {
		type = pType;
		context = pResourceContext;
		bundleId = pBundleId;
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
		return type;
	}

	/**
	 * Retrieves the Resource Context associated to this event
	 * 
	 * @return Resource Context.
	 */
	public ResourceContext getContext() {
		return context;
	}

	/**
	 * <p>
	 * Retrieves the identifier of the bundle being added to or removed from the
	 * Resource Context.
	 * </p>
	 * <p>
	 * This method returns a valid value only when {@link #getType()} returns:
	 * <ul>
	 * <li>{@link #BUNDLE_ADDED}</li>
	 * <li>{@link #BUNDLE_REMOVED}</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the bundle id or -1 (invalid value) .
	 */
	public long getBundleId() {
		return bundleId;
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
		if (var0 == null) {
			return false;
		}
		if (!(var0 instanceof ResourceContextEvent)) {
			return false;
		}
		ResourceContextEvent event = (ResourceContextEvent) var0;
		if (event.getBundleId() != getBundleId()) {
			return false;
		}
		if (event.getContext() == null) {
			return false;
		}
		if (!event.getContext().equals(getContext())) {
			return false;
		}
		return true;
	}

}
