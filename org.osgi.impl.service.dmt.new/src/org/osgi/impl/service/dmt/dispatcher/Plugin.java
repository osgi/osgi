package org.osgi.impl.service.dmt.dispatcher;

import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;

public interface Plugin {

	public boolean isRegistered();
	
	public boolean handlesData(Node subtreeUri);

	public boolean handlesExec(Node subtreeUri);

	public Node[] getDataRoots();

	public DataPlugin getDataPlugin();

	public ExecPlugin getExecPlugin();
}