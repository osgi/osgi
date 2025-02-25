/**
 * Feature Launcher Repository Package Version 1.0.
 * <p>
 * Bundles wishing to use this package must list the package in the
 * Import-Package header of the bundle's manifest. This package has two types of
 * users: the consumers that use the API in this package and the providers that
 * implement the API in this package.
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.featurelauncher.repository; version="[1.0,2.0)"}
 * <p>
 * Example import for providers implementing the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.featurelauncher.repository; version="[1.0,1.1)"}
 */
@Version(FEATURE_LAUNCHER_SPECIFICATION_VERSION)
package org.osgi.service.featurelauncher.repository;

import static org.osgi.service.featurelauncher.FeatureLauncherConstants.FEATURE_LAUNCHER_SPECIFICATION_VERSION;

import org.osgi.annotation.versioning.Version;
