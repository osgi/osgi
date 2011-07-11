package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import info.dmtree.*;

import org.osgi.dmt.ddf.*;

/**
 * Framework node that represents the information about the Framework itself.
 * 
 * The Framework node allows manipulation of the OSGi framework, start level,
 * framework life cycle, and bundle life cycle. Some of the sub-trees are
 * intentionally left optional; if the given management protocol provides its
 * own management over the bundles or OSGi framework start levels, the
 * BundleControl, InstallBundle or StartLevel sub-tree can be excluded from the
 * data model.
 * <p>
 * All modifications to a Framework object must occur in an atomic session.
 * 
 * @remark There are issues with ordering on commit
 */

public interface Framework {

	/**
	 * OSGi framework events. Provides access to catching events and retrieving
	 * them, see {@link FrameworkEvent}.
	 * 
	 * @remark Should this not be at top level?
	 * @remark Why not use log? I know it is optional but it would be easier
	 *         then to make it mandatory?
	 * 
	 * @return FrameworkEvent node
	 */
	FrameworkEvent FrameworkEvent();

	/**
	 * Get the Refresh Packages state node. If not touched,this node is
	 * {@code false}. If during commit this node has been set to {@code true}
	 * then all the packages must be refreshed.
	 * 
	 * @remark should be moved to bundle 0?
	 * 
	 * @return the Mutable for the RefreshPackage node
	 */
	Mutable<Boolean> RefreshPackages();

	/**
	 * Start Level configuration node. The {@link StartLevel} configuration node
	 * allows the change of the default start level and the current start level.
	 * 
	 * @remark Should we not expand this here? And move bundle aspect to bundle
	 *         0?
	 * @return A Start Level configuration node.
	 */
	StartLevel StartLevel();

	/**
	 * Sub-tree to install new bundles in the corresponding framework, see
	 * {@link InstallBundle}. Bundles will be installed in ascending order of
	 * their id.
	 * 
	 * To install a bundle, the management system must create a list of
	 * InstallBundle nodes. The operations described in InstallBundle will then
	 * be executed in LIST order.
	 * 
	 * @remark must this be optional?
	 * 
	 * @return The Optional MultipleMAP for Install Bundle
	 */
	Opt<MutableLIST<InstallBundle>> InstallBundle();

	/**
	 * The BundleControl node is a sub-tree that has an entry for every
	 * installed bundle in this framework. This tree can be used to control the
	 * life cycle of these bundles. See {@link BundleControl}. The names of the
	 * children (keys) are the corresponding Bundle ids. When the bundle is
	 * uninstalled the corresponding child node must be removed.
	 * 
	 * @remark must this be optional? As it is a sub-tree, zero children is also
	 *         ok?
	 * 
	 * @return The Optional MAP for the BundleControl nodes.
	 */
	Opt<MAP<Long, BundleControl>> BundleControl();

	/**
	 * Models the {@link Framework#FrameworkEvent()} node for the OSGi framework
	 * events. If {@link #CatchEvents()} is set to {@code true} then the
	 * {@link #Event()} node will then gather the occurring events numbered
	 * sequentially. Event collection is stopped and all events are cleared when
	 * FrameworkEvent/CatchEvents is set to {@code false}. The total number of
	 * buffered events may be limited by the implementation.
	 * 
	 * @remark The Framework Management Object can reduce the memory usage with
	 *         an additional restrictions over the buffering mechanism. For
	 *         example, only the last ten events can be stored. -- Is rather
	 *         vague?
	 * @remark should it not destroy the earlier ones?
	 */
	public interface FrameworkEvent {
		/**
		 * Control to start/stop the buffering of the OSGi framework events. If
		 * set to {@code true}, all OSGi Framework Events must be collected
		 * under the {@link #Event()} node. If {@code false}, buffering must
		 * stop and the existing events must be cleared. Default is
		 * {@code false}, meaning no buffering. Frameworks may limit the number of
		 * events that are recorded; they must stop adding new events when this
		 * limit is reached.
		 * 
		 * @remark does it make sense to clear when stopping?
		 * 
		 * @return A Mutable to set/get the value of the CatchEvents node.
		 */
		Mutable<Boolean> CatchEvents();

		/**
		 * LIST of the Framework events. Events are gathered when the
		 * {@link #CatchEvents()} is {@code true} and cleared when it is set to
		 * {@code false}
		 * 
		 * @return A LIST of Event nodes
		 */
		@Scope(A)
		LIST<Event> Event();

		/**
		 * Extension node.
		 * 
		 * @return The Ext node
		 */
		Opt<NODE> Ext();
	}

	/**
	 * The Even node describes a single framework event.
	 */
	public interface Event {

		/**
		 * The event exception part of a Framework Event. There can be an
		 * exception error message and the stack trace. If there is no event
		 * exception, an empty string will be set.
		 * 
		 * @remark so this is not machine parseable, just a human message?
		 * 
		 * @return The value of the Throwable node
		 */
		@Scope(A)
		String Throwable();

		/**
		 * The type of the OSGi Framework event from the
		 * {@code FrameworkEvent.getType()} method. The node value is the name
		 * of one of the FrameworkEvent types where an underscore in a Framework
		 * Event type is replaced with a space. Possible values are therefore:
		 * <ul>
		 * <li>{@code STARTED}</li>
		 * <li>{@code ERROR}</li>
		 * <li>{@code PACKAGES REFRESHED}</li>
		 * <li>{@code STARTLEVEL CHANGED}</li>
		 * <li>{@code WARNING}</li>
		 * <li>{@code INFO}</li>
		 * <li>{@code STOPPED}</li>
		 * <li>{@code STOPPED UPDATE}</li>
		 * <li>{@code STOPPED BOOTCLASSPATH MODIFIED}</li>
		 * <li>{@code WAIT TIMEDOUT}</li>
		 * </ul>
		 * 
		 * @remark spaces in constants?????
		 * 
		 * @return the Type node value
		 */
		@Scope(A)
		String Type();

		/**
		 * The id of the bundle that is associated with the event or -1 if no
		 * bundle is associated with the event.
		 * 
		 * @return The value of the BundleId node.
		 */
		@Scope(A)
		long BundleId();

		/**
		 * Extension node.
		 * 
		 * @return An optional Ext node.
		 */
		@Scope(A)
		Opt<NODE> Ext();

	}

	/**
	 * The {@link Framework#StartLevel()} node represents the
	 * FrameworkStartLevel adapt interface. It is used to set the current
	 * Framework Start Level as well as the initial start level for newly
	 * installed bundles.
	 * 
	 * @remark Should move this to the top level in Framework
	 */
	public interface StartLevel {

		/**
		 * The OSGi framework's active start level. When the RequestStartLevel
		 * is replaced, the OSGi Framework’s active start level will be modified
		 * asynchronously with the specified value. The default is 0, which
		 * means no change of the active start level.
		 * 
		 * @remark So if I write 0, it means no change? Replacing this node maps
		 *         to the FrameworkStartLevel setStartLevel method. The valid
		 *         new node values must be greater than or equal to 0. Dmt
		 *         Exception must be thrown when an invalid value is set.
		 * @remark not sure what I read when 0 means no change? Shouldn’t this
		 *         one not become equal to ActiveStartLevel?
		 * @remark Does the error happen at commit or immediately?
		 * @remark What type will the Dmt Exception have?
		 * @remark Wouldnt a better name not be StartLevel?
		 * 
		 * @return the Mutable for the requested start level.
		 */
		Mutable<Integer> RequestedStartLevel();

		/**
		 * Current OSGi Framework’s start level.
		 * 
		 * @return The value of the ActiveStartLevel node
		 */
		int ActiveStartLevel();

		/**
		 * The initial start level of the OSGi Framework, maps to the
		 * FrameworkStartLevel getInitialBundleStartLevel method.
		 * 
		 * @remark Why is this called beginning and not initial as it is in the
		 *         spec? Also removed -1 value because 4.3 has startlevel
		 *         mandatory as it is not a service
		 * 
		 * @return The value of the BeginningStartLevel node
		 */
		int BeginningStartLevel();

		/**
		 * Configures the initial bundle start level, maps to the the
		 * FrameworkStartLevel {@code setInitialBundleStartLevel()} method.
		 * 
		 * @return A Mutable for the initial bundle start level.
		 */
		Mutable<Integer> InitialBundleStartLevel();

		/**
		 * Extension node.
		 * 
		 * @return The value of the optional Ext node
		 */
		Opt<NODE> Ext();
	}

	/**
	 * To install new bundles in the corresponding framework.
	 * 
	 * The following steps are required to install a new Bundle.
	 * <ol>
	 * <li>Open a {@link DmtSession#LOCK_TYPE_ATOMIC} session. This may be the
	 * root and therefore cross frameworks</li>
	 * <li>For each bundle:</li>
	 * <li>Create an InstallBundle/[id] node with a unique id. The name of the
	 * id node must be numeric string value from 1..Long.MAX_VALUE. As the
	 * InstalLBundle LIST is cleared there are no nodes resident when an atomic
	 * session is opened.</li>
	 * <li>The Framework must automatically create all sub-nodes but must not
	 * set any values.</li>
	 * <li>Set the value of the {@link #Location()} node to a desired bundle
	 * location. If no {@link #Url()} node is set the Location will be
	 * interpreted as the URL to a bundle.</li>
	 * <li>Set the {@link #Option()} node. This ensure that no previous value is
	 * accidentally reused.</li>
	 * <li>Optionally set the {@link #Url()} sub node. If the Url node is set
	 * the installBundle(String location, InputStream source) must be used where
	 * the source comes from the URL node. If only the Location node is set the
	 * installBundle(String location) must be used.</li>
	 * <li>Repeat from Step 2 for installing multiple bundles</li>
	 * <li>Commit the Dmt Session. If this fails then one or more bundle
	 * installs failed and all the OperationResult node must be checked for
	 * error messages that are not SUCCESS.</li>
	 * <li>Open a new Dmt Session to verify the result in the
	 * [id]/OperationResult node(s).</li>
	 * <li>Delete the [id] sub-tree once the sub-tree is no longer needed.</li>
	 * <li>Close the Dmt Session. The Framework object must install any bundles
	 * that have been defined in the first atomic Dmt Session during the commit
	 * phase. When there is a failure, all operations, across all frameworks,
	 * must be rolled back</li>
	 * <li>After the commit ends, the InstallBundle nodes are removed.
	 * </ol>
	 * 
	 * @remark Are we really sure we want to cross frameworks?
	 */
	public interface InstallBundle {
		/**
		 * Magic string which indicates success for {@link #OperationResult()}
		 */
		String SUCCESS = "SUCCESS";

		/**
		 * The bundle location under which this bundle must be installed. This
		 * maps to either the location in the installBundle(String) or
		 * installBundle(String,InputStream) method depending on the presence of
		 * the URL node.
		 * 
		 * @return The Mutable for the Location node.
		 */
		@Scope(A)
		Mutable<String> Location();

		/**
		 * URL for the bundle install. If the URL node is not set, the
		 * {@code installBundle(String)} method must be used, otherwise the
		 * {@code installBundle(String,InputStream)} method must be used where
		 * the Input Stream argument is created from the URL.
		 * 
		 * @return the Mutable for the URL node.
		 */
		@Scope(A)
		Mutable<String> Url();

		/**
		 * Defines the start policy of the bundle after it has been successfully
		 * installed. It can only accept the following strings:
		 * <ul>
		 * <li>{@code NO OPTION} – (default) Do not start.</li>
		 * <li>{@code NO START OPTION} – Start the bundle with the start()
		 * method.</li>
		 * <li>{@code START TRANSIENT} – Start the bundle transient using the
		 * Bundle’s {@code START_TRANSIENT} start option.</li>
		 * <li>{@code START ACTIVATION POLICY} – Start the bundle with the
		 * Bundle’s {@code START_ACTIVATION_POLICY} option.</li>
		 * <li>
		 * {@code START ACTIVATION POLICY AND TRANSIENT} – Start the bundle with
		 * the Bundle’s {@code START_ACTIVATION_POLICY | START_TRANSIENT}.</li>
		 * <li>{@code RESOLVE} – Resolve the bundle after installation.</li>
		 * </ul>
		 * 
		 * @return The Mutable for the Option node
		 */
		@Scope(A)
		Mutable<String> Option();

		/**
		 * Status of the last executed operation. The node will be created at
		 * the same time as the [id] node creation but will have no value. once
		 * the Framework has installed all bundles it will set the operation
		 * results during the commit phase. The value must specify a human
		 * readable error reason when the bundle installation fails. If the
		 * operation succeeds, the node value must be {@link #SUCCESS}.
		 * 
		 * @return The value of the OperationResult node
		 */
		@Scope(A)
		String OperationResult();

		/**
		 * Extension node.
		 * 
		 * @return The optional value of the Ext node
		 */
		@Scope(A)
		Opt<NODE> Ext();
	}

	/**
	 * The BundleControl node provides information about an installed bundle and
	 * allows for their life cycle and start level control to be changed.
	 */
	public interface BundleControl {
		/**
		 * After commit, trigger a refresh operation for this bundle as the
		 * root. If during commit this node is set to {@code true} then
		 * corresponding bundle, and its dependencies, will be refreshed. The
		 * initial value in the beginning of a session must be {@code false}.
		 * 
		 * @return The RefreshPackages node.
		 */
		@Scope(A)
		Mutable<Boolean> RefreshPackages();

		/**
		 * The Bundle’s current start level.
		 * 
		 * @return The Mutable for the BundleStartLevel node.
		 */
		@Scope(A)
		Mutable<Integer> BundleStartLevel();

		/**
		 * Node for commands related to the bundle's life cycle.
		 * 
		 * @remark Couldn't we not expand the Life Cycle node here? Seems a bit
		 *         artificial to go yet another level deeper?
		 * 
		 * @return The value of the Lifecyle node
		 */
		@Scope(A)
		Lifecycle Lifecycle();

		/**
		 * Extension node.
		 * 
		 * @return The optional value for the Ext node.
		 */
		@Scope(A)
		Opt<NODE> Ext();

		/**
		 * To control the life cycle of one or more bundles the following steps
		 * must be taken. It is important to set all the nodes for each
		 * operation to ensure no remains that are then accidentally used.
		 * 
		 * @remark I am puzzled why we gratuitously differ from the OSGi API?
		 *         Why not follow this API exactly with the same named
		 *         constants?
		 * 
		 * @remark This Lifecycle node is an RPC, we should replace it with an
		 *         RPC or we should change it we a more declarative model.
		 */
		public interface Lifecycle {
			/**
			 * The value of a successful result.
			 */
			final static String SUCCESS = "SUCCESS";

			/**
			 * NO OPTION, if the operation has no option.
			 */
			final static String NO_OPTION = "NO OPTION";
			/**
			 * Option for start and stop to indicate transient start/stop.
			 */
			final static String TRANSIENT = "TRANSIENT";
			/**
			 * 
			 */
			final static String START_ACTIVATION_POLICY = "START ACTIVATION POLICY";
			/**
			 * 
			 */
			final static String START_ACTIVATION_POLICY_AND_TRANSIENT = "START ACTIVATION POLICY AND TRANSIENT";

			/**
			 * Resolve the bundle. Must have no options specified.
			 */
			final static String RESOLVE = "RESOLVE";

			/**
			 * Start the bundle. Can accept the following options:
			 * <ul>
			 * <li>{@link #NO_OPTION} - Call Bundle start method without
			 * options.</li>
			 * <li>{@link #TRANSIENT} - Call the start(START_TRANSIENT) method.</li>
			 * <li>{@link #START_ACTIVATION_POLICY} - Call the
			 * start(START_ACTIVATION_POLICY) method.</li>
			 * <li>{@link #START_ACTIVATION_POLICY_AND_TRANSIENT} - Call the
			 * start(START_ACTIVATION_POLICY|TRANSIENT) method.</li>
			 * </ul>
			 */
			final static String START = "START";

			/**
			 * Start the bundle. Can accept the following options:
			 * <ul>
			 * <li>"NO OPTION" - Call Bundle start method without options.</li>
			 * <li>"TRANSIENT" - Call the start(STOP_TRANSIENT) method.</li>
			 * </ul>
			 */
			final static String STOP = "STOP";

			/**
			 * Update the bundle. The option, if not {@link #NO_OPTION}, must be
			 * a URL to a value Bundle.
			 */
			final static String UPDATE = "UPDATE";

			/**
			 * Uninstall the bundle. The option must be set to
			 * {@link #NO_OPTION};
			 */
			final static String UNINSTALL = "UNINSTALL";

			/**
			 * The operation that controls the Bundle's life-cycle. The value
			 * must be one of:
			 * <ul>
			 * <li>"" - The empty string ,is the default value, indicates no
			 * operation.</li>
			 * <li>{@link #RESOLVE}</li>
			 * <li>{@link #START}</li>
			 * <li>{@link #STOP}</li>
			 * <li>{@link #UPDATE}</li>
			 * <li>{@link #UNINSTALL}</li>
			 * </ul>
			 * The operation is executed during the commit phase of the required
			 * atomic session.
			 * 
			 * @return The the Operation node.
			 */
			@Scope(A)
			Mutable<String> Operation();

			/**
			 * The Option belong to the set Operation. It can have one of the
			 * following values:
			 * <ul>
			 * <li>{@link #NO_OPTION} for all</li>
			 * <li>{@link #TRANSIENT} for {@link #START} and {@link #STOP}</li>
			 * <li>{@link #START_ACTIVATION_POLICY} for {@link #START}</li>
			 * <li>{@link #START_ACTIVATION_POLICY_AND_TRANSIENT} for
			 * {@link #START}</li>
			 * <li>A URL (for {@link #UPDATE} Operation)</li>
			 * 
			 * @return The the Option node.
			 */
			@Scope(A)
			Mutable<String> Option();

			/**
			 * The result of the previous Lifecycle Operation. If this operation
			 * has been successful, it is equals to {@link #SUCCESS}. If the
			 * operation failed, the contents will be a human readable error
			 * message.
			 * 
			 * @remark close session, then read?
			 * 
			 * @return The the OperationResult node
			 */
			@Scope(A)
			String OperationResult();

			/**
			 * Extension node.
			 * 
			 * @return The optional Ext node
			 */
			@Scope(A)
			Opt<NODE> Ext();
		}
	}

	/**
	 * Extension node.
	 * 
	 * @return The optional Ext node
	 */
	Opt<NODE> Ext();
}
