package org.osgi.service.remoteservices;

/**
 * Provide the definition of the constants used in the Remote Services API.
 * 
 */
public class RemoteServiceConstants {
	private RemoteServiceConstants(){}
	
	/**
	 * The Configuration Types supported by this Distribution Provider.
	 * <p>
	 * Services that are suitable for remoting list the configuration types that
	 * describe the distribution metadata for that service in the
	 * {@link RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS} property. Not
	 * every Distribution Provider will support all configuration types. The
	 * value of this property on the Distribution Provider service list the
	 * configuration types supported by this one.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 * 
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_CONFIGS
	 * @see RemoteServiceConstants#SERVICE_IMPORTED_CONFIGS
	 */
	 public final static String REMOTE_CONFIGS_SUPPORTED = "remote.configs.supported";

	/**
	 * Service property that lists the intents supported by the distribution
	 * provider.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 * 
	 * @see RemoteServiceConstants#SERVICE_INTENTS
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_INTENTS
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_INTENTS_EXTRA
	 */
	 public final static String REMOTE_INTENTS_SUPPORTED = "remote.intents.supported";

	
	/**
	 * Defines the interfaces under which this service can be exported. This
	 * list must be a subset of the types listed in the objectClass service
	 * property. The single value of an asterisk (�*�, \u002A) indicates all
	 * interfaces in the registration�s objectClass property (not classes). It
	 * is strongly recommended to only export interfaces and not concrete
	 * classes due to the complexity of creating proxies for some type of
	 * classes.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	 public final static String SERVICE_EXPORTED_INTERFACES = "service.exported.interfaces";

	/**
	 * A list of intents that the distribution provider must implement to
	 * distribute the service. Intents listed in this property are reserved for
	 * intents that are critical for the code to function correctly, for
	 * example, ordering of messages. These intents should not be configurable.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	 public final static String SERVICE_EXPORTED_INTENTS = "service.exported.intents";

	/**
	 * Extra intents configured in addition to the the intents specified in
	 * {@link #SERVICE_EXPORTED_INTENTS} .These intents are merged with the
	 * service.exported.intents and therefore have the same semantics. They are
	 * extra, so that the service.exported.intents can be set by the bundle
	 * developer and this property is then set by the administrator. Bundles
	 * should make this property configurable, for example through the
	 * Configuration Admin service.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	 public final static String SERVICE_EXPORTED_INTENTS_EXTRA = "service.exported.intents.extra";

	/**
	 * A list of configuration types that should be used to export the service.
	 * Each of the configuration types should be synonymous, that is, describing
	 * the an endpoint for the same service using different technologies. A
	 * distribution provider should distribute such a service with one of the
	 * given types it recognizes. Each type has an associated specification that
	 * describes how the configuration data for the exported service is
	 * represented in an OSGi framework.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	 public final static String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";

	/**
	 * Must be set by a distribution provider to true when it registers the
	 * end-point proxy as an imported service. Can be used by a bundle to
	 * control not getting a service that is imported.
	 * <p>
	 * The value of this property is of type String or Boolean.
	 */
	 public final static String SERVICE_IMPORTED = "service.imported";

	/**
	 * The configuration information used to import this services, as described
	 * in {@link #SERVICE_EXPORTED_CONFIGS}. Any associated properties for this
	 * configuration types must be properly mapped to the importing system. For
	 * example, a URL in these properties must point to a valid resource when
	 * used in the importing framework.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	 String SERVICE_IMPORTED_CONFIGS = "service.imported.configs";

	/**
	 * A list of intents that this service implements. This property has dual
	 * purpose. A bundle can use this service property to notify the
	 * distribution provider that these intents are already implemented by the
	 * exported service object. For an imported service, a distribution provider
	 * must use this property to convey the combined intents of the exporting
	 * service and the intents that the distribution provider adds. To export a
	 * service, a distribution provider must recognize all these intents and
	 * expand any qualified intents.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	 public final static String SERVICE_INTENTS = "service.intents";

	/**
	 * The <code>service.id</code> of the Distribution Provider.
	 * {@link ExportedEndpointDescription} services and imported services must be
	 * registered with this service property to identify the Distribution
	 * Provider which is managing the exported and imported services. The value
	 * of the property must be the <code>service.id</code> of the service
	 * identifying the Distribution Provider.
	 * ### do we need this still?
	 * <p>
	 * The value of this property is of type <code>Long</code>.
	 */
	 public final static String DISTRIBUTION_PROVIDER_ID = "distribution.provider.id";

	 public final static String SERVICE_EXPORTED_PREFIX = "service.exported.";
	
	
	
	 public final static String FRAMEWORK_UUID = "framework.uuid";
}
