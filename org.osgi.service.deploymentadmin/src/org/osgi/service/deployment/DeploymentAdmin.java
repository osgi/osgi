package org.osgi.service.deployment;

import java.io.*;

public interface DeploymentAdmin {
	BundleSuite installBundleSuite(InputStream in);

	BundleSuite getBundleSuites();
}
