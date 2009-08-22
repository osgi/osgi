package org.osgi.test.cases.cm.shared;

import java.util.Dictionary;

public interface Synchronizer {
	void signal();

	void signal(Dictionary props);
}
