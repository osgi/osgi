package info.dmtree.spi;

/**
 * This interface can be optionally implemented by a <code>DataPlugin</code> or
 * <code>ExecPlugin</code> in order to get information about its absolute mount
 * points in the overall DMT.
 * <p>
 * This is especially interesting, if the plugin is mapped to the tree as part
 * of a list. In such a case the id for this particular data plugin is
 * determined by the DmtAdmin after the registration of the plugin and therefore
 * unknown to the plugin in advance.
 * 
 */
public interface MountPlugin {

	/**
	 * The string to be used as key for the mount points property when a
	 * DataPlugin or ExecPlugin is registered with mount points.
	 */
	static final String MOUNT_POINTS = "mountPoints";

	/**
	 * Provides the <code>MountPoint</code> objects describing the path where
	 * the plugin is mapped to the overall DMT.
	 * 
	 * @param mountPoints
	 *            the newly mapped mount points
	 */
	void mountPointsAdded(MountPoint[] mountPoints);

	/**
	 * Informs the plugin that the provided <code>MountPoint</code> objects have
	 * been removed from the mapping.
	 * <p>
	 * NOTE: attempts to invoke the <code>postEvent</code> or
	 * <code>sendEvent</code> on the provided <code>MountPoint</code> will be
	 * ignored.
	 * 
	 * @param mountPoints
	 *            array of <code>MountPoint</code> objects that have been
	 *            removed from the mapping
	 */
	void mountPointsRemoved(MountPoint[] mountPoints);
}
