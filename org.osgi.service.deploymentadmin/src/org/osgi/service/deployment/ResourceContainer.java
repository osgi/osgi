package org.osgi.service.deployment;

import java.io.*;
import java.util.*;

public interface ResourceContainer {
	void extractResources(Map metadata, InputStream stream, BundleSuite suite ) throws Exception;
	Resource getResource(String identity);
}
