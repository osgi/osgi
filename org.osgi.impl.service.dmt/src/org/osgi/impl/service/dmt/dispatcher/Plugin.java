package org.osgi.impl.service.dmt.dispatcher;

import info.dmtree.DmtException;
import info.dmtree.Uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class Plugin {

	// SD: removed final 
	List<Segment> owns = new ArrayList<Segment>();
	Set<String> mountPoints = new HashSet<String>();

	final ServiceReference reference;
	final Segment root;

	Plugin(ServiceReference ref, Segment root) throws Exception {
		this.reference = ref;
		this.root = root;
	}

	void init( Collection<String> uris, IDManager idManager ) throws Exception {
		root.lock();
		try {
			String pid = (String) reference.getProperty( Constants.SERVICE_PID );

			List<Segment> candidates = new ArrayList<Segment>();

			assert uris != null;

			// mountPoints are only allowed for plugins with just 1 dataRootURI
			// they are treated as relative pathes
			if ( reference.getProperty( "mountPoints") != null ) {
				if ( uris.size() > 1 ) {
					error("mountPoints are not allowed for plugins with more than one dataRootURI "
							+ this);
					return;
				}
				else if ( reference.getProperty( "mountPoints" ) instanceof String[] ) {
					String[] mps = (String[]) reference.getProperty( "mountPoints" );
					String prefix = uris.iterator().next() + "/";
					for (int i = 0; i < mps.length; i++) {
						mountPoints.add( prefix + mps[i] );
					}
				}
			}
			

			// Try to find the segments we need and check
			// they do not violate any constraints
			for (final String uri : uris) {

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
					if ( uri.endsWith("#")) {
						id = idManager.getIndex(uri, pid);
						
						String newUri = uri.replaceAll("#", ""+id);
						s.release();
						s = getSegment(Uri.toPath(newUri), root);
					}
					// SD: makes mountpoint checks easier later on, because it still holds the version with the '#'
					s.mountedOn = uri;
//					System.out.println( "added to candidates: " + s.getUri() );
					candidates.add(s);
				}
				else {
					error("plugin registers under occupied root uri: "
							+ uri + " " + this);
					return;
				}
			}


			// Verify that we do not have mounted descendants that
			// are not mountpoints
			for (Segment s : candidates) {
				// Verify that any descendants are actually mounted
				// on mount points we agree with
				List<Segment> descendants = new ArrayList<Segment>();
				s.getDescendants(descendants);
				for (Segment descendant : descendants) {
					try {
						if (descendant.plugin != null) {
//							if (!isMountPoint(descendant.getUri().toString())) {
							if (! this.isMountPoint(descendant.mountedOn)) {
								error("plugin overlaps: " + descendant.getUri() + " "
										+ descendant.plugin);
								return;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// We only get here when all is ok ...
			// So grab the segments
			for (Segment candidate : candidates) {
				candidate.plugin = this;
//				System.out.println( " assigning plugin to candidate segment " + candidate.getUri());
			}
			// SD: was missing
			this.owns = candidates;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			root.unlock();
		}
	}


	boolean isRoot() {
		for (Segment segment : owns ) {
			if ( ".".equals(segment.getUri().toString()) )
				return true;
		}
		return false;
	}

	boolean isMountPoint(String path) {
		return mountPoints.contains(path);
	}
	


	void close() throws InterruptedException {
		root.lock();
		try {
			for (Segment segment : owns) {
				try {
//					System.out.println( "releasing segment: " + segment.getUri() );
					segment.release();
				} catch (DmtException e) {
					e.printStackTrace();
				}
			}
		} finally {
			root.unlock();
		}
	}

	Segment getSegment(String[] path, Segment root ) {
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

}
