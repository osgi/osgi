RFC 66 web container Reference Implementation Notes
===================================================

Equinox-Specific API Usage
--------------------------

The RI depends on the Equinox-specific API org.eclipse.osgi.framework.internal.core.BundleHost.getBundleData.
This usage is required in order to serve static content from a bundle via a file, which is what the RI's
servlet container implementation (Tomcat) requires. This Equinox-specific code is encapsulated in the RI
behind a BundleFileResolver interface to simplify the process of supporting other OSGi frameworks.
Instances of this interface are constructed using the BundleFileResolverFactory which dynamically detects
whether the BundleHost class is available and, if not, falls back to a default, non-framework specific
BundleFileResolver implementation which returns a null File.