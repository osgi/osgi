package org.osgi.impl.service.dmt.dispatcher;

import info.dmtree.DmtException;

import info.dmtree.Uri;
import info.dmtree.spi.MountPlugin;
import info.dmtree.spi.MountPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.application.Framework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Plugin {

	// SD: removed final
	List<Segment> owns = new ArrayList<Segment>();
	Set<String> mountPoints = new HashSet<String>();

	final ServiceReference reference;
	final Segment root;

	ServiceTracker eaTracker;
	BundleContext context;

	Plugin(ServiceReference ref, Segment root, ServiceTracker eaTracker,
			BundleContext context) throws Exception {
		this.reference = ref;
		this.root = root;
		this.eaTracker = eaTracker;
		this.context = context;
	}

	/**
	 * performs following checks:
	 * 1. is the number of registered uri == 1, if mountPoints are given
	 * 2. are the requested nodes free and "backed up" by mountPoints of an ancestral plugin
	 * 3. do all descendant plugins match one of the own mountPoints, or would a descendant plugin
	 *    potentially be hidden/overlapped
	 * <p>
	 * In all of the above cases the whole plugin is ignored and no mapping will happen at all,
	 * even if some uris have passed the checks.
	 * @param uris
	 * @param idManager
	 * @throws Exception
	 */
	void init(Collection<String> uris, IDManager idManager) throws Exception {
		root.lock();
		try {
			String pid = (String) reference.getProperty(Constants.SERVICE_PID);

			List<Segment> candidates = new ArrayList<Segment>();

			assert uris != null;

			// mountPoints are only allowed for plugins with just 1 dataRootURI
			// they are treated as relative pathes
			if (reference.getProperty(MountPlugin.MOUNT_POINTS) != null) {
				if (uris.size() > 1) {
					error("mountPoints are not allowed for plugins with more than one dataRootURI "
							+ this);
					return;
				} else if (reference.getProperty("mountPoints") instanceof String[]) {
					String[] mps = (String[]) reference
							.getProperty(MountPlugin.MOUNT_POINTS);
					String prefix = uris.iterator().next() + "/";
					for (int i = 0; i < mps.length; i++) {
						mountPoints.add(prefix + mps[i]);
					}
				}
			}

			// Try to find the segments we need and check
			// they do not violate any constraints
			for (final String uri : uris) {

				// checks if the node is free and the ancestral plugin has a mountpoint
				// defined that matches our desired position
				Segment s = getValidatedSegment(uri, idManager, pid);

				if (s != null)
					// System.out.println( "added to candidates: " + s.getUri() );
					candidates.add(s);
				else {
					error("plugin registers under occupied root uri: " + uri
							+ " " + this);
					return;
				}
			}

			// Verify that we do not have mounted descendants that
			// are not mountpoints
			for (Segment s : candidates) {
				Segment overlappedDescendant = getOverlappedDescendant(s);
				if ( overlappedDescendant != null ) {
					error("plugin overlaps: " + overlappedDescendant.getUri()
							+ " " + overlappedDescendant.plugin);
					return;
				}
			}

			// We only get here when all is ok ...
			// So grab the segments
			for (Segment candidate : candidates) {
				candidate.plugin = this;
				// System.out.println( " assigning plugin to candidate segment "
				// + candidate.getUri());
			}
			// SD: was missing
			this.owns = candidates;

			notifyAddedMappings(this.owns);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			root.unlock();
		}
	}

	/**
	 * checks if the node is free and the ancestral plugin has a mountpoint
	 * defined that matches our desired position
	 * 
	 * @param uri the uri to check (either dataRootURI or execRootURI)
	 * @param idManager reference to the idManager
	 * @param pid the optional pid, that is used to make id mapping persistent
	 * @return the validated Segment if all checks are OK or null, if check where not OK
	 */
	private Segment getValidatedSegment(String uri, IDManager idManager, String pid) {
		String path[] = Uri.toPath(uri);
		Segment s = getSegment(path, root);
		Plugin owner = s.getPlugin();

		// Verify that we have a free node or the ancestral
		// plugin has a mount point that matches our desired
		// position
		if (owner == null || owner.isRoot()
				|| owner.isMountPoint(s.getUri().toString())) {

			// TODO: replace the "#" with a unique id
			int id = -1;
			if (uri.endsWith("#")) {
				id = idManager.getIndex(uri, pid);

				String newUri = uri.replaceAll("#", "" + id);
				s.plugin = null;
				s = getSegment(Uri.toPath(newUri), root);
			}
			// SD: makes mountpoint checks easier later on, because it still
			// holds the version with the '#'
			s.mountedOn = uri;
			return s;
		}
		return null;
	}

	/**
	 * 	Verify that any descendants are actually mounted
	 *	on mount points we agree with
	 *
	 * @param s
	 * @return the first found overlapped descendant or null if all checks are OK
	 */
	private Segment getOverlappedDescendant( Segment s ) {
		List<Segment> descendants = new ArrayList<Segment>();
		s.getDescendants(descendants);
		for (Segment descendant : descendants) {
			try {
				if (descendant.plugin != null) {
					// if
					// (!isMountPoint(descendant.getUri().toString())) {
					if (!this.isMountPoint(descendant.mountedOn)) {
						return descendant;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public boolean addRootUri(String uri) {
		boolean success = false;

		return success;
	}

	public boolean isRoot() {
		for (Segment segment : owns) {
			if (".".equals(segment.getUri().toString()))
				return true;
		}
		return false;
	}

	boolean isMountPoint(String path) {
		return mountPoints.contains(path);
	}

	/**
	 * closes a Plugin. This includes: - invoke close also on all other Plugins
	 * that are currently mapped to one of this plugins mount points - invokes
	 * release on all of the owned Segments
	 * 
	 */
	void close() throws InterruptedException {
		root.lock();
		try {
			// MUST also close all Plugins that are currently mapped to one of
			// "my" mount points
			for (String mountPoint : mountPoints) {
				String[] mpPath = Uri.toPath(mountPoint);
				Segment mpSegment = getSegment(mpPath, root);
				if (mpSegment != null && mpSegment.plugin != null)
					// remember all pathes for plugins that have been mapped on
					// this plugins mount points
					mpSegment.plugin.close();
			}
			List<String[]> pathes = new ArrayList<String[]>();
			for (Segment segment : owns) {
				pathes.add(segment.getPath());
				try {
					// System.out.println( "releasing segment: " +
					// segment.getUri() );
					segment.release(null);
				} catch (DmtException e) {
					e.printStackTrace();
				}
			}
			invokeMountPointsCallback(getMountPlugin(), pathes, false);
			
			owns.clear();
			mountPoints.clear();
		} finally {
			root.unlock();
		}
	}

	Segment getSegment(String[] path, Segment root) {
		assert ".".equals(path[0]);
		return root.getSegmentFor(path, 1, true);
	}

	public Set<String> getMountPoints() {
		return mountPoints;
	}

	public List<Segment> getOwns() {
		return (List<Segment>) owns;
	}

	public ServiceReference getReference() {
		return reference;
	}

	private void error(String string) {
		System.err.println("# " + string);
	}

	private MountPlugin getMountPlugin() {
		MountPlugin mp = null;
		Collection<String> classes = toCollection(this.reference.getProperty(Constants.OBJECTCLASS));
		if ( classes.contains(MountPlugin.class.getName())) {
			try {
				mp = (MountPlugin) context.getService(this.reference);
			} catch (ClassCastException e) {
			}
		}

		return mp;
	}

	private Collection<String> toCollection(Object property) {
		if (property instanceof String)
			return Arrays.asList((String) property);

		if (property instanceof String[])
			return Arrays.asList((String[]) property);

		return null;
	}


	private void notifyAddedMappings(List<Segment> segments) {
		List<String[]> pathes = new ArrayList<String[]>();
		for (Segment segment : segments) {
			pathes.add(segment.getPath());
		}
		invokeMountPointsCallback(getMountPlugin(), pathes, true);
	}

	private void invokeMountPointsCallback(MountPlugin mp,
			List<String[]> mountedPathes, boolean added) {
		if (mp == null || mountedPathes == null)
			return;
		try {
			List<MountPoint> mps = new ArrayList<MountPoint>();
			for (String[] path : mountedPathes) {
				mps.add(new MountPointImpl(path, eaTracker));
			}
			if (added)
				mp.mountPointsAdded(mps.toArray(new MountPoint[mountedPathes.size()]));
			else
				mp.mountPointsRemoved(mps.toArray(new MountPoint[mountedPathes
						.size()]));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
