package org.osgi.test.cases.rmt.util;

import java.util.*;
import java.util.concurrent.locks.*;

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.*;

public class GDSession<T> implements TransactionalDataSession {

	final boolean				writeable;
	final Node<T>				root;
	final GenericDataPlugin<T>	plugin;
	final Lock					lock;
	final String[]				base;
	final Access				access	= new Access();

	/**
	 */
	public GDSession(GenericDataPlugin<T> plugin, Lock lock, boolean b,
			String[] base, T node) {
		this.writeable = b;
		this.root = access.getNode(node);
		this.plugin = plugin;
		this.lock = lock;
		this.base = base;
		lock.lock();
	}

	@Override
	public void nodeChanged(String[] nodePath) throws DmtException {
		System.out.println("Node changed " + Arrays.toString(nodePath));
	}

	@Override
	public void close() throws DmtException {
		try {
			System.out.println("closed");
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		Node< ? > node = root.find(nodePath, base.length);
		return node.getChildNodeNames();
	}

	@Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {

		System.out.println("getMetaNode " + Arrays.toString(nodePath));
		return null;
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		System.out.println("getNodeSize " + Arrays.toString(nodePath));
		return 0;
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		System.out.println("getNodeTimestamp " + Arrays.toString(nodePath));
		return null;
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		System.out.println("getNodeTitle " + Arrays.toString(nodePath));
		return null;
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		System.out.println("getNodeType " + Arrays.toString(nodePath));
		return null;
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		Node< ? > node = root.find(nodePath, base.length);
		return node != null;
	}

	@Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		Node< ? > n = root.find(nodePath, base.length);
		return n.handler instanceof Handler.Primitive;
	}

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		Node< ? > node = root.find(nodePath, base.length);
		Object n = node.object;
		if (n instanceof Date)
			return new DmtData((Date) n);

		if (n instanceof Long)
			return new DmtData((long)(Long) n);

		if (n instanceof Integer)
			return new DmtData((int)(Integer) n);

		if (n instanceof byte[])
			return new DmtData((byte[]) n);

		if (n instanceof String)
			return new DmtData((String) n);

		if (n instanceof Boolean)
			return new DmtData((boolean)(Boolean) n);

		if (n instanceof Float)
			return new DmtData((float)(Float) n);

		return new DmtData(n);
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		System.out.println("getNodeVersion " + Arrays.toString(nodePath));
		return 0;
	}

	@Override
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		System.out.println("copy " + Arrays.toString(nodePath));

	}

	@Override
	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		System.out.println("createInteriorNode " + Arrays.toString(nodePath));

	}

	@Override
	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		System.out.println("createLeafNode " + Arrays.toString(nodePath));

	}

	@Override
	public void deleteNode(String[] nodePath) throws DmtException {
		System.out.println("deleteNode " + Arrays.toString(nodePath));

	}

	@Override
	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		System.out.println("renameNode " + Arrays.toString(nodePath));

	}

	@Override
	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		System.out.println("setNodeTitle " + Arrays.toString(nodePath));

	}

	@Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
		System.out.println("setNodeType " + Arrays.toString(nodePath));

	}

	@Override
	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		System.out.println("setNodeValue " + Arrays.toString(nodePath));

	}

	@Override
	public void commit() throws DmtException {
		System.out.println("commit");

	}

	@Override
	public void rollback() throws DmtException {
		System.out.println("rollback");

	}

}
