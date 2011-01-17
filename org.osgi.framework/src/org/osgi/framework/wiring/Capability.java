/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
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

package org.osgi.framework.wiring;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * A capability that has been declared from a {@link BundleRevision bundle
 * revision}.
 * 
 * <p>
 * The framework defines capabilities for {@link #PACKAGE_CAPABILITY packages},
 * {@link #BUNDLE_CAPABILITY bundles} and {@link #HOST_CAPABILITY hosts}. These
 * capabilities are defined only to express wiring information by the framework.
 * They must not be used in {@link Constants#PROVIDE_CAPABILITY
 * Provide-Capability} and {@link Constants#REQUIRE_CAPABILITY
 * Require-Capability} manifest headers.
 * 
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */
public interface Capability {
	/**
	 * Capability name space for package capabilities. The name of the package
	 * is stored in the capability attribute of the same name as this name
	 * space. The other directives and attributes of the package, from the
	 * {@link Constants#EXPORT_PACKAGE Export-Package} manifest header, can be
	 * found in the cabability's {@link #getDirectives() directives} and
	 * {@link #getAttributes() attributes}. The
	 * {@link Constants#VERSION_ATTRIBUTE version} capability attribute must
	 * contain the {@link Version} of the package if one is specified.  The
	 * {@link Constants#BUNDLE_SYMBOLICNAME_ATTRIBUTE bundle-symbolic-name}
	 * capability attribute must contain the {@link 
	 * BundleRevision#getSymbolicName() symbolic name} of the provider if one
	 * is specified.
	 * The {@link Constants#BUNDLE_VERSION_ATTRIBUTE bundle-version} capability 
	 * attribute must contain the {@link BundleRevision#getVersion() version}
	 * of the provider if one is specified.
	 * 
	 * <p>
	 * The package capabilities provided by the system bundle, that is the
	 * bundle with id zero, must include the package specified by the
	 * {@link Constants#FRAMEWORK_SYSTEMPACKAGES} and
	 * {@link Constants#FRAMEWORK_SYSTEMPACKAGES_EXTRA} framework properties as
	 * well as any other package exported by the framework implementation.
	 * 
	 * <p>
	 * A bundle revision {@link BundleRevision#getDeclaredCapabilities(String)
	 * declares} zero or more package capabilities (this is, exported packages).
	 * <p> 
	 * A bundle wiring {@link BundleWiring#getProvidedCapabilities(String)
	 * provides} zero or more resolved package capabilities (that is, exported packages)
	 * and {@link BundleWiring#getRequiredCapabilities(String) requires} zero or
	 * more resolved package capabilities (that is, imported packages). The number of
	 * package capabilities required by a bundle wiring may change as the bundle
	 * wiring may dynamically import additional packages.
	 */
	String	PACKAGE_CAPABILITY	= "osgi.wiring.package";

	/**
	 * Capability name space for bundle capabilities. The bundle symbolic name
	 * of the bundle is stored in the capability attribute of the same name as
	 * this name space. The other directives and attributes of the bundle, from
	 * the {@link Constants#BUNDLE_SYMBOLICNAME Bundle-SymbolicName} manifest
	 * header, can be found in the cabability's {@link #getDirectives()
	 * directives} and {@link #getAttributes() attributes}. The
	 * {@link Constants#BUNDLE_VERSION_ATTRIBUTE bundle-version} capability
	 * attribute must contain the {@link Version} of the bundle, from the
	 * {@link Constants#BUNDLE_VERSION Bundle-Version} manifest header.
	 * 
	 * <p>
	 * A bundle wiring {@link BundleWiring#getProvidedCapabilities(String)
	 * provides} exactly one<sup>&#8224;</sup> bundle capability (that is, the
	 * bundle can be required by another bundle) and
	 * {@link BundleWiring#getRequiredCapabilities(String) requires} zero or
	 * more bundle capabilities (that is, requires other bundles).
	 * 
	 * <p>
	 * &#8224; A bundle with no bundle symbolic name (that is, a bundle with
	 * {@link Constants#BUNDLE_MANIFESTVERSION Bundle-ManifestVersion}
	 * {@literal <} 2) must not provide a bundle capability.
	 */
	String	BUNDLE_CAPABILITY	= "osgi.wiring.bundle";

	/**
	 * Capability name space for host capabilities. The bundle symbolic name of
	 * the bundle is stored in the capability attribute of the same name as this
	 * name space. The other directives and attributes of the bundle, from the
	 * {@link Constants#BUNDLE_SYMBOLICNAME Bundle-SymbolicName} manifest
	 * header, can be found in the cabability's {@link #getDirectives()
	 * directives} and {@link #getAttributes() attributes}. The
	 * {@link Constants#BUNDLE_VERSION_ATTRIBUTE bundle-version} capability
	 * attribute must contain the {@link Version} of the bundle, from the
	 * {@link Constants#BUNDLE_VERSION Bundle-Version} manifest header.
	 * 
	 * <p>
	 * A bundle wiring {@link BundleWiring#getProvidedCapabilities(String)
	 * provides} zero or one<sup>&#8224;</sup> host capability if the bundle
	 * {@link Constants#FRAGMENT_ATTACHMENT_DIRECTIVE allows fragments to be
	 * attached} and {@link BundleWiring#getRequiredCapabilities(String)
	 * requires} zero or one host capability if their are attached fragments.
	 * 
	 * <p>
	 * &#8224; A bundle with no bundle symbolic name (that is, a bundle with
	 * {@link Constants#BUNDLE_MANIFESTVERSION Bundle-ManifestVersion}
	 * {@literal <} 2) must not provide a host capability.
	 */
	String	HOST_CAPABILITY		= "osgi.wiring.host";

	/**
	 * Returns the name space of this capability.
	 * 
	 * @return The name space of this capability.
	 */
	String getNamespace();

	/**
	 * Returns the directives of this capability.
	 * 
	 * @return An unmodifiable map of directive names to directive values for this 
	 *         capability, or an empty map if this capability has no directives.
	 */
	Map<String, String> getDirectives();

	/**
	 * Returns the attributes of this capability.
	 * 
	 * @return An unmodifiable map of attribute names to attribute values for this
	 *         capability, or an empty map if this capability has no attributes.
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the bundle revision declaring this capability.
	 * 
	 * @return The bundle revision declaring this capability.
	 */
	BundleRevision getProviderRevision();
}
