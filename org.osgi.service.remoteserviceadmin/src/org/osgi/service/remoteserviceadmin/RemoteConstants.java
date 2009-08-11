package org.osgi.service.remoteserviceadmin;

/**
 * Provide the definition of the constants used in the Remote Services API.
 * 
 */
public class RemoteConstants {
	private RemoteConstants() {
	}

	/**
	 * The configuration types supported by this Distribution Provider.
	 * 
	 * Services that are suitable for distribution list the configuration types
	 * that describe the configuration information for that service in the
	 * {@link #SERVICE_EXPORTED_CONFIGS} or {@link #SERVICE_IMPORTED_CONFIGS}
	 * property.
	 * 
	 * A distribution provider must register a service that has this property
	 * and enumerate all configuration types that it supports.
	 * 
	 * The type of this property <code>String+</code>
	 * 
	 * @see #SERVICE_EXPORTED_CONFIGS
	 * @see #SERVICE_IMPORTED_CONFIGS
	 */
	public final static String REMOTE_CONFIGS_SUPPORTED = "remote.configs.supported";

	/**
	 * Service property that lists the intents supported by the distribution
	 * provider.
	 * 
	 * Each distribution provider must register a service that has this property
	 * and enumerate all the supported intents, having any qualified intents
	 * expanded.
	 * 
	 * The value of this property is of type <code>String+</code>.
	 * 
	 * @see #SERVICE_INTENTS
	 * @see #SERVICE_EXPORTED_INTENTS
	 * @see #SERVICE_EXPORTED_INTENTS_EXTRA
	 */
	public final static String REMOTE_INTENTS_SUPPORTED = "remote.intents.supported";

	/**
	 * Defines the interfaces under which this service can be exported.
	 * 
	 * This list must be a subset of the types listed in the objectClass service
	 * property. The single value of an asterisk ('*' \u002A) indicates all
	 * interfaces in the registration's objectClass property (not classes). It
	 * is highly recommended to only export interfaces and not concrete classes
	 * due to the complexity of creating proxies for some type of classes.
	 * 
	 * The value of this property is of type String+.
	 */
	public final static String SERVICE_EXPORTED_INTERFACES = "service.exported.interfaces";

	/**
	 * A list of intents that the distribution provider must implement to
	 * distribute the service. Intents listed in this property are reserved for
	 * intents that are critical for the code to function correctly, for
	 * example, ordering of messages. These intents should not be configurable.
	 * 
	 * The value of this property is of type <code>String+</code>.
	 */
	public final static String SERVICE_EXPORTED_INTENTS = "service.exported.intents";

	/**
	 * Extra intents configured in addition to the the intents specified in
	 * {@link #SERVICE_EXPORTED_INTENTS}.
	 * 
	 * These intents are merged with the service.exported.intents and therefore
	 * have the same semantics. They are extra, so that the
	 * {@link #SERVICE_EXPORTED_INTENTS} can be set by the bundle developer and
	 * this property is then set by the administrator/deployer. Bundles should
	 * make this property configurable, for example through the Configuration
	 * Admin service.
	 * 
	 * The value of this property is of type <code>String+</code>.
	 */
	public final static String SERVICE_EXPORTED_INTENTS_EXTRA = "service.exported.intents.extra";

	/**
	 * A list of configuration types that should be used to export the service.
	 * 
	 * Configuration types can be <em>synonymous</em> or <em>alternatives</em>.
	 * In principle, a distribution provider should create an endpoint for each
	 * recognized configuration type, the deployer is responsible that synonyms
	 * do not clash.
	 * 
	 * Each configuration type has an associated specification that describes
	 * how the configuration data for the exported service is represented in an
	 * OSGi framework.
	 * 
	 * The value of this property is of type <code>String+</code>.
	 */
	public final static String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";

	/**
	 * Must be set by a distribution provider to <code>true</code> when it
	 * registers the end-point proxy as an imported service. Can be used by a
	 * bundle to prevent it from getting an imported service.
	 * 
	 * The value of this property is not defined, setting it is sufficient.
	 */
	public final static String SERVICE_IMPORTED = "service.imported";

	/**
	 * The configuration type used to import this services, as described in
	 * {@link #SERVICE_EXPORTED_CONFIGS}. Any associated properties for this
	 * configuration types must be properly mapped to the importing system. For
	 * example, a URL in these properties must point to a valid resource when
	 * used in the importing framework. Configuration types in this property
	 * must be synonymous.
	 *
	 * The value of this property is of type <code>String+</code>.
	 */
	public final String SERVICE_IMPORTED_CONFIGS = "service.imported.configs";

	/**
	 * A list of intents that this service implements. This property has dual
	 * purpose. A bundle can use this service property to notify the
	 * distribution provider that these intents are already implemented by the
	 * exported service object. For an imported service, a distribution provider
	 * must use this property to convey the combined intents of the exporting
	 * service and the intents that the distribution providers add. To export a
	 * service, a distribution provider must recognize all these intents and
	 * expand any qualified intents.
	 * 
	 * The value of this property is of type <code>String+</code>.
	 */
	public final static String SERVICE_INTENTS = "service.intents";


	/**
	 * The property key for the endpoint URI. This is a unique id for an
	 * endpoint following the URI syntax. As far as this specification is
	 * concerned, this unique id is opaque.
	 */
	final public static String ENDPOINT_URI = "endpoint.uri";

	/**
	 * The property key for the endpoint service id. This is a unique id for a
	 * service based on the framework id '.' service id or another model. As far as this specification is
	 * concerned, this unique id is opaque.
	 */
	final public static String ENDPOINT_REMOTE_SERVICE_ID = "endpoint.remote.service.id";

	/**
	 * The key for a framework property that defines the UUID of the framework.
	 * 
	 * The property must be set by the framework or through configuration before
	 * the VM is started or some bundle. The value must be a Universally Unique
	 * Id, it must not contain any dots ('.' \u002E).
	 */
	public final static String FRAMEWORK_UUID = "org.osgi.framework.uuid";
}
