package org.osgi.test.shared;

import java.io.IOException;
import java.util.Dictionary;

public interface IRun {
	final static String TEST_PROPERTIES_FILE = "org.osgi.test.properties.file";
	void linkClosed() throws Exception;

	void push(String bundle, Object msg);

	void sendLog(String bundle, Log log) throws IOException;

	void setTargetProperties(Dictionary properties) throws IOException;

	void stopped(String bundle);
}
