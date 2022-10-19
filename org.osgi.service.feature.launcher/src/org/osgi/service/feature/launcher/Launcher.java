package org.osgi.service.feature.launcher;

import org.osgi.service.feature.Feature;

public interface Launcher {
	Feature getEffectiveFeature();

	void launch();

	void waitForStop();
}
