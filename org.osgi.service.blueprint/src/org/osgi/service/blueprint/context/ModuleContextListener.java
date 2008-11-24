
package org.osgi.service.blueprint.context;

import org.osgi.framework.Bundle;

public interface ModuleContextListener {

	void contextCreated(Bundle forBundle);

	void contextCreationFailed(Bundle forBundle, Throwable rootCause);
	
}
