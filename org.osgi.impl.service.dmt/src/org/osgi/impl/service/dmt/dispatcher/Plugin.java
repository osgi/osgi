package org.osgi.impl.service.dmt.dispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A plugin represents a Data- or ExecPlugin with its occupied segments in the tree.
 * @author steffen
 *
 */
public class Plugin {

	private static final boolean ADDED = true;
	private static final boolean REMOVED = false;
	
	List<Segment> owns;
	Set<String> mountPoints;

	final ServiceReference reference;
	final Segment root;

	ServiceTracker eaTracker;
	BundleContext context;
	boolean closed;

	Plugin(ServiceReference ref, Segment root, ServiceTracker eaTracker,
			BundleContext context, Collection<String> uris) throws Exception {
		this.reference = ref;
		this.root = root;
		this.eaTracker = eaTracker;
		this.context = context;
		closed = false;
		init(uris);
	}
	
	/**
	 * checks for number of uris, if mountPoints are given and initializes local mountPoint var
	 * if check was OK
	 * @param uris
	 * @throws Exception
	 */
	private boolean init(Collection<String> uris) throws Exception {
		try {
			assert uris != null;

			// mountPoints are only allowed for plugins with just 1 dataRootURI
			// they are treated as relative pathes
			Collection<String> mps = Util.toCollection(reference.getProperty(DataPlugin.MOUNT_POINTS));
			if (mps != null && ! mps.isEmpty() ) {
				String prefix = uris.iterator().next() + "/";
				for ( String mountPoint : mps )
					getMountPoints().add(prefix + mountPoint );
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return true;
	}

	/**
	 * try to add the given uri to the mapping of this plugin
	 * @param rootUri
	 */
	String mapUri( String uri, IDManager idManager ) throws Exception {
		root.lock();
		try {
			assert uri != null;

			String pid = null;
			Collection<String> pids = Util.toCollection(reference.getProperty(Constants.SERVICE_PID));
			if ( pids != null && pids.size() > 0)
				pid = pids.iterator().next();

			// find required segment and check it does not violate any constraints
			Segment s = getValidatedSegment(uri, idManager, pid);
			if ( s == null )
				return null;
			
			s.plugin = this;
			getOwns().add( s );
			invokeMountPointsCallback(Arrays.asList(s), ADDED);
			
			return s.getUri().toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			root.unlock();
		}
		return null;
	}
	

	/**
	 * checks if the node is free and the parent plugin has a mountpoint
	 * defined that matches our desired position
	 * 
	 * @param uri the uri to check (either dataRootURI or execRootURI)
	 * @param idManager reference to the idManager
	 * @param pid the optional pid, that is used to make id mapping persistent
	 * @return the validated Segment if all checks are OK or null, if check where not OK
	 */
	private Segment getValidatedSegment(String uri, IDManager idManager, String pid) throws DmtException {

		String checkedUri = uri;
		if ( uri.endsWith("#")) {
			int id = idManager.getIndex(uri, pid, reference.getBundle().getBundleId() );
			checkedUri = uri.substring(0, uri.length()-1) + id;
		}
		// add this segment temporarily
		Segment s = root.getSegmentFor(Uri.toPath(checkedUri), 1, true);
		Plugin owner = s.getPlugin();
		// remembering the original uri (with '#') makes mountpoint checks easier 
		s.mountedOn = uri;
		
		// check for incompatible sibling plugins (see 117.7.3) 
		// siblings with and without trailing "#" at the same time are incompatible
		if ( s.parent != null ) {
			HashSet<String> siblingUris = new HashSet<String>();
			for (Segment sibling : s.parent.children)
				siblingUris.add(sibling.mountedOn.substring(sibling.mountedOn.length()-1));
			if ( siblingUris.contains("#") && siblingUris.size() > 1) {
				error("plugin has incompatible siblings: " + uri + " --> ignoring this plugin");
				s.release(null);
				return null;
			}
		}

		if (owner == null || owner.isRoot() || owner.hasMountPoint(uri)) {
			// Verify that we do not have mounted descendants that
			// are not mountpoints
			Segment overlappedDescendant = getOverlappedDescendant(s);
			if ( overlappedDescendant != null ) {
				error("plugin overlaps: " + overlappedDescendant.getUri()
						+ " " + overlappedDescendant.plugin);
				s.release(null);
				return null;
			}
		}
		else {
			error("plugin registers under occupied root uri: " + uri + " --> ignoring this plugin");
			// release this segment, if there is no other mapped plugin with exactly the same uri
			if ( ! owner.getOwns().contains(s))
				s.release(null);
			return null;
		}
		return s;
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
		s.getFirstDescendantPlugins(descendants);
		for (Segment descendant : descendants) {
			try {
				if (descendant.plugin != null) {
					if (!this.hasMountPoint(descendant.mountedOn))
						return descendant;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public boolean isRoot() {
		for (Segment segment : getOwns()) {
			if (".".equals(segment.getUri().toString()))
				return true;
		}
		return false;
	}

	boolean hasMountPoint(String path) {
		return getMountPoints().contains(path);
	}
	
	boolean hasOnlyRootParentPlugin( Segment segment ) {
		if ( segment == null )
			return false;
		if ( segment.parent == null )
			return false;
		Plugin p = segment.parent.getPlugin();
		return p==null || p.isRoot();
	}
	/**
	 * Closes the whole Plugin and releases all occupied segments. 
	 * It also releases the mapping for child plugins, that are currently mounted on one of 
	 * the own mountpoints.
	 * 
	 */
	void close() throws InterruptedException {
		root.lock();
		try {
			// MUST also close all Plugins that are currently mapped to one of
			// "my" mount points
			// This is true only if this plugin has a parent plugin other than the root plugin
			// (because the root plugin by def. accepts all mounted plugins)
			Segment own = getOwns().get(0);
			if ( getMountPoints().size() > 0 && ! hasOnlyRootParentPlugin(own) ) {
				for (String mountPoint : getMountPoints()) {
					String[] mpPath = Uri.toPath(mountPoint);
					Segment mpSegment = getSegment(mpPath, root);
					if (mpSegment != null && mpSegment.plugin != null) {
						// plugins with more than one owned segments can't have mountpoints
						if (mpSegment.plugin.getOwns().size() > 1)
							// --> sufficient to just release the segment
							mpSegment.plugin.releaseSegment(mpSegment);
						else
							// close and handle dependend plugins 
							mpSegment.plugin.close();
					}
				}
			}
			for (Segment segment : getOwns())
				segment.release(null);
			
			invokeMountPointsCallback(getOwns(), REMOVED);
			getOwns().clear();
			getMountPoints().clear();
			closed = true;
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			root.unlock();
		}
	}
	
	/**
	 * Removes a single mapping (i.e. for one dataRootURI) of the plugin and 
	 * releases the occupied segments.
	 * 
	 * @param segment .. the segment to be released
	 */
	void releaseSegment(Segment segment){
		owns.remove(segment);
		try {
			segment.release(null);
			invokeMountPointsCallback(Arrays.asList(segment), REMOVED);
		} catch (DmtException e) {
			e.printStackTrace();
		}
	}

	/**
	 * looks for the requested path in the segment hierarchy and
	 * adds the new path if missing
	 * @param path
	 * @param root
	 * @return
	 */
	Segment getSegment(String[] path, Segment root) {
		assert ".".equals(path[0]);
		return root.getSegmentFor(path, 1, true);
	}

	public Set<String> getMountPoints() {
		if (mountPoints == null)
			mountPoints = new HashSet<String>();
		return mountPoints;
	}
	
	public void addMountPoint(String mountPoint ) {
		getMountPoints().add(mountPoint);
	}

	public List<Segment> getOwns() {
		if (owns == null)
			owns = new ArrayList<Segment>();

		return owns;
	}

	public ServiceReference getReference() {
		return reference;
	}

	private void error(String string) {
		System.err.println("# " + string);
	}


	private void invokeMountPointsCallback(	List<Segment> segments, boolean added) {
		Object o = context.getService(this.reference);
		MountPlugin mp = o instanceof MountPlugin ? (MountPlugin) o : null;
		
		try {
			if ( mp != null )
				for (Segment segment : segments) {
					if (added)
						mp.mountPointAdded(new MountPointImpl(this, segment.getPath(), reference.getBundle()));
					else
						mp.mountPointRemoved(new MountPointImpl(this, segment.getPath(), reference.getBundle()));
				}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			context.ungetService(reference);
		}
	}
}
