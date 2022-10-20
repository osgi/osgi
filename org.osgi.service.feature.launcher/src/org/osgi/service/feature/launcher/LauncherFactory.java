package org.osgi.service.feature.launcher;

import java.net.URL;

import org.osgi.service.feature.Feature;

/**
 * Create a Feature Launcher.
 */
public interface LauncherFactory {
	/**
	 * Create a new launcher based on the provided URLs.
	 * 
	 * @param features URLs to the Feature files.
	 * @return the new launcher;
	 */
	Launcher newLauncher(URL... features);

	/**
	 * Create a new launcher based on the provided Feature instances;
	 * 
	 * @param features The features the launcher should use.
	 * @return the new launcher.
	 */
	Launcher newLauncher(Feature... features);
}
