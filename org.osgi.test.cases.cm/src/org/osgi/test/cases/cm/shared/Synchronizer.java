package org.osgi.test.cases.cm.shared;

import java.util.Dictionary;

public interface Synchronizer {
	void signal(Dictionary<String,Object> props);

	void signal();

	void signalDeleted(String pid);
}
