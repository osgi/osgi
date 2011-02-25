package org.osgi.impl.service.dmt.dispatching;

import info.dmtree.DmtException;
import info.dmtree.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Dispatcher extends ServiceTracker {
	Segment root = new Segment();

	// interface SegmentVisitor {
	// void visit(Segment segment);
	// }

	class Segment {
		final Segment parent;
		final String name;
		final List<Segment> children = new ArrayList<Segment>();
		Plugin plugin;
		boolean locked;
		Thread lockedThread;
		boolean mountPoint;

		private Segment() {
			name = ".";
			parent = null;
		}

		Segment(Segment parent, String name) {
			this.name = name;
			this.parent = parent;
		}

		synchronized void release() throws DmtException {
			plugin = null;
		}

		public Segment getSegmentFor(String[] path, int i) {
			if (i >= path.length)
				return this;

			String name = path[i];
			for (Segment child : children) {
				if (child.name.equals(name))
					return child.getSegmentFor(path, i + 1);
			}
			Segment child = new Segment(this, name);
			children.add(child);
			return child.getSegmentFor(path, i + 1);
		}

		public Plugin getPluginFor(String[] path, int i) {
			if (i < path.length) {
				String name = path[i];
				for (Segment child : children) {
					if (child.name.equals(name))
						return child.getPluginFor(path, i + 1);
				}
			}
			return this.getPlugin();
		}

		private Plugin getPlugin() {
//			if (plugin == null)
			if (plugin != null)
				return plugin;

			if (parent != null)
				return parent.getPlugin();

			return null;
		}

		synchronized void lock() throws InterruptedException {
			while (locked)
				wait();
			lockedThread = Thread.currentThread();
		}

		synchronized void unlock() {
			assert lockedThread == Thread.currentThread();
			notifyAll();
		}

		public String[] getPath() {
			return getPath(0);
		}

		String[] getPath(int n) {
			String[] path = parent == null ? new String[n] : parent
					.getPath(n + 1);
			path[path.length - n] = name;
			return path;
		}

		public void getDescendants(List<Segment> result) {
			for (Segment s : children) {
				result.add(s);
				s.getDescendants(result);
			}
		}

		public StringBuffer getUri() {
			StringBuffer sb = parent == null ? new StringBuffer() : parent
					.getUri();
			if (parent != null)
				sb.append("/");
			sb.append(name);
			return sb;
		}
	}

	class RootSegment extends Segment {

	}

	class Plugin {
		final List<Segment> owns = new ArrayList<Segment>();
		final Set<String> mountPoints = new HashSet<String>();
		final ServiceReference reference;

		public Plugin(ServiceReference ref) throws Exception {
			this.reference = ref;
		}

		void init() throws Exception {
			root.lock();
			try {
				Collection<String> uris = toCollection(reference
						.getProperty("dataRootURIs"));

				List<Segment> candidates = new ArrayList<Segment>();

				assert uris != null;

				// Try to find the segments we need and check
				// they do not violate any constraints
				for (final String uri : uris) {

					String path[] = Uri.toPath(uri);
					Segment s = getSegment(path);
					Plugin owner = s.getPlugin();

					// Verify that we have a free node or the ancestral
					// plugin has a mount point that matches our desired
					// position
					if (owner == null
							|| owner.isMountPoint(s.getUri().toString()))
						candidates.add(s);
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
						if (descendant.plugin != null) {
							if (!isMountPoint(descendant.getUri().toString())) {
								error("plugin overlaps: " + descendant + " "
										+ descendant.plugin);
								return;
							}
						}
					}
				}

				// We only get here when all is ok ...
				// So grab the segments
				for (Segment candidate : candidates) {
					candidate.plugin = this;
					System.out.println( " assigning plugin to candidate segment " + candidate);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				root.unlock();
			}
		}

		private boolean isMountPoint(String path) {
			return mountPoints.contains(path);
		}

		private Collection<String> toCollection(Object property) {
			if (property instanceof String)
				return Arrays.asList((String) property);

			if (property instanceof String[])
				return Arrays.asList((String[]) property);

			return null;
		}

		void close() throws InterruptedException {
			root.lock();
			try {
				for (Segment segment : owns) {
					try {
						segment.release();
					} catch (DmtException e) {
						e.printStackTrace();
					}
				}
			} finally {
				root.unlock();
			}
		}

		Segment getSegment(String[] path) {
			assert ".".equals(path[0]);
			return root.getSegmentFor(path, 1);
		}
	}

	public Dispatcher(BundleContext context, Filter filter) {
		super(context, filter, null);
		System.out.println( " **** initialized Dispatcher ");
	}

	public Object addingService(ServiceReference ref) {
		try {
			System.out.println( " # adding service ...");
			Plugin p = new Plugin(ref);
			p.init();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void removedService(ServiceReference ref, Object service) {
		try {
			System.out.println( " # removed service ...");
			((Plugin) service).close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void error(String string) {
		System.err.println("# " + string);
	}
	
	// additional exposed methods
	public ServiceReference getPluginReference( String[] path ) {
		ServiceReference ref = null;
		Plugin plugin = root.getPluginFor(path, 1);
		if ( plugin != null )
			ref = plugin.reference;
		return ref;
	}
	
	public String[] getChildNodeNames( String[] path ) {
		Segment segment = root.getSegmentFor(path, 1);
		if ( segment == null ) 
			return null;
		Vector<String> childNodeNames = new Vector<String>();
		for ( Segment child : segment.children )
			childNodeNames.add( child.name );
		return (String[]) childNodeNames.toArray( new String[childNodeNames.size()] );
	}

}
