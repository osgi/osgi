package org.osgi.service.feature.launcher;

import java.net.URL;
import java.util.Map;

import org.osgi.service.feature.Feature;

/**
 * Create a Feature Launcher.
 */
public interface LauncherFactory {
	/**
	 * Create a new launcher based on the provided URLs.
	 * 
	 * @param feature URL to the Feature file.
	 * @param variables The feature variables to use.
	 * @return the new launcher;
	 */
	Launcher newLauncher(URL feature, Map<String,Object> variables);

	/**
	 * Create a new launcher based on the provided Feature instances;
	 * 
	 * @param feature The feature the launcher should use.
	 * @param variables The feature variables to use.
	 * @return the new launcher.
	 */
	Launcher newLauncher(Feature feature, Map<String,Object> variables);
}
