package org.osgi.service.deployment;

import java.security.cert.*;
import java.util.*;

public interface BundleSuite {
	Map[] getMetaData();

	void addResource(Resource resource, boolean created);

	Resource[] getResources();

	Certificate[] getSigners();

	void remove();
}
