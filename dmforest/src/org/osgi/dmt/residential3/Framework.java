package org.osgi.dmt.residential3;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;

import org.osgi.dmt.ddf.*;

/**
 * Framework node that represents the information about the Framework itself.
 * 
 * The Framework node allows manipulation of the OSGi framework, start level,
 * framework life cycle, and bundle life cycle.
 * <p>
 * All modifications to a Framework object must occur in an atomic session. All
 * changes to the framework must occur during the commit.
 * <p>
 * The Framework node allows the manager to install (create a new child node in
 * {@link #Bundles()}, to uninstall (delete the appropriate child node in
 * {@link #Bundles()}, change the state of the bundle (see
 * {@link Bundle#RequestedState()}, update the bundle {@link Bundle#URL()}, and
 * update the framework ({@link #Restart()}). The implementation must execute
 * these actions in the following order during the commit of the session:
 * <ol>
 * <li>Create a snapshot of the current installed bundles and their state.</li>
 * <li>Uninstall all the to be uninstalled bundles (bundles whose RequestedState
 * is {@link Bundle#UNINSTALLED} or where the corresponding node was deleted)</li>
 * <li>Update all bundles that have a modified {@link Bundle#URL()} with this
 * URL using the Bundle {@code update(InputStream)} method.</li>
 * <li>Install any new bundles from their {@link Bundle#URL()}.</li>
 * <li>Refresh all bundles that were updated and installed</li>
 * <li>Ensure that any Bundles that have the {@link Bundle#AutoStart()} flag set
 * to {@code true} are started persistently. Transiently started bundles that
 * were stopped in this process are not restarted</li>
 * <li>Return from the commit without error.</li>
 * <li>If the {@link #Restart()} flag was set to {@code true} then reset the
 * framework by calling Bundle {@code update()} method in a different thread.</li>
 * </ol>
 * If any of the above steps runs in an error (except the restart) than the
 * actions must be undone and the system state must be restored to the snapshot.
 */

public interface Framework {
	/**
	 * 
	 */

	/**
	 * The StartLevel manages the Framework's current Start Level. Maps to the
	 * Bundle Start Level {@code set/getStartLevel()} methods.
	 * 
	 * @return A Start Level node.
	 */
	Mutable<Integer> StartLevel();

	/**
	 * Configures the initial bundle start level, maps to the the
	 * FrameworkStartLevel {@code set/getInitialBundleStartLevel()} method.
	 * 
	 * @return A Mutable for the initial bundle start level.
	 */
	Mutable<Integer> InitialBundleStartLevel();

	/**
	 * Restart the current framework by calling {@code update()} on the System
	 * Bundle after the commit has been successfully closed.
	 * 
	 * The restart must happen after all bundles operations have been finished
	 * and should provide time for the management session to close properly.
	 * 
	 * @return The Restart node.
	 */
	Mutable<Boolean> Restart();

	/**
	 * The MAP of location -> Bundle. Each Bundle is uniquely identified by its
	 * location. The location is a string that must be unique for each bundle
	 * and can be chosen by the management system.
	 * <p>
	 * The Bundles node will be automatically filled from the installed bundles,
	 * representing the actual state. Deleting a child node must uninstall that
	 * bundle during commit.
	 * <p>
	 * New bundles can be installed by creating a new node with a given
	 * location. At commit, this bundle will be installed from their
	 * {@link Bundle#URL} node.
	 * <p>
	 * The location of the System Bundle must be "System Bundle", this node
	 * cannot be deleted and most operations on this node have special meaning.
	 * 
	 * @return The Bundles node.
	 */
	MutableMAP<String, Bundle> Bundles();

	/**
	 * The Framework Properties.
	 * <p>
	 * The Framework properties come from the Bundle Context
	 * {@code getProperty()} method. However, this method does not provide the
	 * names of the available properties. If the handler of this node
	 * is aware of the framework properties then these should be used
	 * to provide the node names. If these properties are now known, the
	 * handler must synthesize the names from the following sources
	 * <ul>
	 * <li>System Properties (as they are backing the Framework properties)</li>
	 * <li>Launching properties as defined in the OSGi Core specification</li>
	 * <li>Properties in the residential specification</li>
	 * <li>Other known properties</li>
	 * </ul>
	 * The names must only be enumerated as child names when a value is present.
	 * <p>
	 * It must be possible to access a property that is present in the framework
	 * but not know to the handler.
	 * 
	 * @return The Framework's properties.
	 */
	@Scope(A)
	MAP<String, String> Properties();

}
