package org.osgi.impl.service.dmt.dispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.dmt.DmtException;

/**
 * A Segment represents one node in the dmtree. Segments can have a Plugin 
 * assigned or not. 
 * Segments with an assigned Plugins are the roots of the subtree that the plugin 
 * is responsible for. Segments without an assigned plugin just ensure that the 
 * whole tree is browsable and are created/removed as needed when plugins are 
 * mapped/unmapped.
 * Each Segment knows its parent and children.
 * @author steffen
 *
 */
public class Segment<P> {

	final Segment<P>		parent;
	final String name;
	final List<Segment<P>>	children	= new ArrayList<>();
	Plugin<P>				plugin;
	boolean locked;
	Thread lockedThread;
	String mountedOn;
	int id = -1;
	Date creationTime;

	Segment() {
		name = ".";
		parent = null;
		creationTime = null;
	}

	Segment(Segment<P> parent, String name) {
		this.name = name;
		this.parent = parent;
		this.creationTime = new Date();
	}

	/**
	 * iterates the parents up until it finds one with a plugin assigned up this
	 * way all segments are released and removed from the parents children list
	 * 
	 * @param child
	 * @throws DmtException
	 */
	synchronized void release(Segment<P> child) throws DmtException {
		if (child != null)
			getChildren().remove(child);
		else
			plugin = null;
		// remove current node only from parent, if:
		// - not top-level
		// - there is no plugin mapped directly to this segment
		// - there are no descendant segments with assigned plugins
		if (parent != null && plugin == null && ! hasDescendantPlugins())
			parent.release(this);
	}

	/**
	 * checks whether there are other segments with mapped plugins below the
	 * current segment
	 * 
	 * @return
	 */
	private boolean hasDescendantPlugins() {
		List<Segment<P>> descendants = new ArrayList<>();
		getFirstDescendantPlugins(descendants);
//		for (Segment descendant : descendants) {
//			if (descendant.plugin != null)
//				return true;
//		}
//		return false;
		return ! descendants.isEmpty();
	}

	protected Segment<P> getSegmentFor(String[] path, int i, boolean add) {
		if (i >= path.length)
			return this;

		@SuppressWarnings("hiding")
		String name = path[i];
		for (Segment<P> child : children) {
			if (child.name.equals(name))
				return child.getSegmentFor(path, i + 1, add);
		}
		if (!add)
			return null;
		Segment<P> child = new Segment<>(this, name);
		// SD: don't add mount points "#"
		if (!"#".equals(name))
			children.add(child);
		return child.getSegmentFor(path, i + 1, add);
	}

	public Plugin<P> getPluginFor(String[] path, int i) {
		if (i < path.length) {
			@SuppressWarnings("hiding")
			String name = path[i];
			for (Segment<P> child : children) {
				if (child.name.equals(name))
					return child.getPluginFor(path, i + 1);
			}
		}
		return this.getPlugin();
	}

	Plugin<P> getPlugin() {
		// if (plugin == null)
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
		return getPath(1);
	}

	String[] getPath(int n) {
		String[] path = parent == null ? new String[n] : parent.getPath(n + 1);
		path[path.length - n] = name;
		return path;
	}

	/**
	 * get the top-level plugins in the given segments descendants list
	 * @param result
	 */
	public void getFirstDescendantPlugins(List<Segment<P>> result) {
		for (Segment<P> s : children) {
			// stop at first descendant segment with attached plugin 
			if ( s.plugin != null )
				result.add(s);
			else 
				s.getFirstDescendantPlugins(result);
		}
	}

//	public void getDescendants(List<Segment> result) {
//		for (Segment s : children) {
//			result.add(s);
//			s.getDescendants(result);
//		}
//	}

	
	public StringBuffer getUri() {
		StringBuffer sb = parent == null ? new StringBuffer() : parent.getUri();
		if (parent != null)
			sb.append("/");
		sb.append(name);
		return sb;
	}

	public List<Segment<P>> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getUri().toString();
	}
	
	public Date getCreationTime() {
		return creationTime;
	}
}
