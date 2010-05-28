package org.osgi.impl.service.dmtsubtree;

import org.osgi.impl.service.dmtsubtree.mapping.flags.MappedPath;
import org.osgi.impl.service.dmtsubtree.sessions.AtomicDataSession;
import org.osgi.impl.service.dmtsubtree.sessions.ReadOnlyDataSession;
import org.osgi.impl.service.dmtsubtree.sessions.WriteableDataSession;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.Uri;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

public class RootPlugin implements DataPlugin, MappedPath {
	
	private String[] rootPath;
	private String rootUri;
	private Activator activator;
	
	public RootPlugin( String[] rootPath, Activator activator ) {
		this.rootPath = rootPath;
		this.rootUri = Uri.toUri( rootPath );
		this.activator = activator;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return new AtomicDataSession( activator, sessionRoot );
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return new ReadOnlyDataSession( activator, sessionRoot );
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return new WriteableDataSession( activator, sessionRoot );
	}

	public String[] getRootPath() {
		return rootPath;
	}
}
