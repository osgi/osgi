package org.osgi.tools.btool;

import java.io.*;
import java.util.Collection;

public interface Source {
	InputStream getEntry(String resourceName) throws IOException;

	Collection getResources(String dir) throws IOException;

	boolean contains(String path);

	boolean isDirectory(String path);

	File getFile();
	
	long lastModified(String resource);
}
