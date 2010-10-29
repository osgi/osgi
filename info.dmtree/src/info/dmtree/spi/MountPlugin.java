package info.dmtree.spi;

import info.dmtree.MountPoint;

public interface MountPlugin {

	/**
	 * The string to be used as key for the mount points 
	 * property when a DataPlugin or ExecPlugin is registered with mount points.
	 */
	static final String MOUNT_POINTS = "mountPoints"; 
		
	/**
	 * Sets the MountPoints objects describing the path where the plugin is mapped to the overall DMT.
	 * @param mountPoints ... the newly mapped mount points
	 */
	void mountPointsAdded( MountPoint[] mountPoints );
	
	/**
	 * Informs the plugin that the provided MountPoints have been removed from the mapping. 
	 * NOTE: attempts to invoke one of the postEvents on the provided MountPoint will be ignored.
	 * @param mountPoints ... array of MountPoint objects that have been removed from the mapping
	 */
	void mountPointsRemoved( MountPoint[] mountPoints );
}
