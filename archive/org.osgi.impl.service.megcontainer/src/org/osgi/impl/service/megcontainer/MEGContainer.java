package org.osgi.impl.service.megcontainer;

import org.osgi.service.application.*;

public interface MEGContainer extends ApplicationContainer {
	public ApplicationDescriptor[] installApplication(long bundleID)
			throws Exception;
}
