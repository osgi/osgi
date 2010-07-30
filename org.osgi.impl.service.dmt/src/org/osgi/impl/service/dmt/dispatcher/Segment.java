package org.osgi.impl.service.dmt.dispatcher;

import info.dmtree.DmtException;

import java.util.ArrayList;
import java.util.List;


public class Segment {

	final Segment parent;
	final String name;
	final List<Segment> children = new ArrayList<Segment>();
	Plugin plugin;
	boolean locked;
	Thread lockedThread;
	String mountedOn;
	int id = -1;

	Segment() {
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

	protected Segment getSegmentFor(String[] path, int i, boolean add) {
		if (i >= path.length)
			return this;

		String name = path[i];
		for (Segment child : children) {
			if (child.name.equals(name))
				return child.getSegmentFor(path, i + 1, add );
		}
		if ( !add )
			return null;
		Segment child = new Segment(this, name);
		// SD: don't add children with name "#"
		if ( ! "#".equals(name ))
			children.add(child);
		return child.getSegmentFor(path, i + 1, add);
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

	Plugin getPlugin() {
//		if (plugin == null)
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
	
	public List<Segment> getChildren() {
		return children;
	}
	
	public String getName() {
		return name;
	}

	public String toString() {
		return getUri().toString();
	}
}
