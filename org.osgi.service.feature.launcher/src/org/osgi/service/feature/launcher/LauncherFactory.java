package org.osgi.service.feature.launcher;

import java.net.URL;

import org.osgi.service.feature.Feature;

public interface LauncherFactory {
	Launcher newLauncher(URL... models);

	Launcher newLauncher(Feature... models);
}
