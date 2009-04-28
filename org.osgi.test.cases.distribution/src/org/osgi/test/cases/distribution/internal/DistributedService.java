package org.osgi.test.cases.distribution.internal;

import java.io.Serializable;

public interface DistributedService extends Serializable {
	String reverse(String string);
}
